package de.corneliusmay.silkspawners.wiring.processor;

import de.corneliusmay.silkspawners.wiring.Initializes;
import de.corneliusmay.silkspawners.wiring.Loader;
import de.corneliusmay.silkspawners.wiring.Requires;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

class WiredValidator {

    private static final String LOADER = Loader.class.getCanonicalName();

    private static final String LISTENER = Listener.class.getCanonicalName();

    private static final String EVENT = Event.class.getCanonicalName();

    private final ProcessingEnvironment processingEnv;

    private final WiringModel model;

    private final Set<String> sources = new HashSet<>();

    private String registry;

    private String registryPackage;

    private List<TypeMirror> ambient = List.of();

    WiredValidator(ProcessingEnvironment processingEnv, WiringModel model) {
        this.processingEnv = processingEnv;
        this.model = model;
    }

    void addSources(RoundEnvironment roundEnvironment) {
        for (TypeElement type : ElementFilter.typesIn(roundEnvironment.getRootElements()))
            sources.add(type.getQualifiedName().toString());
    }

    String registryPackage() {
        return registryPackage;
    }

    void validateRegistry(TypeElement element) {
        String name = element.getQualifiedName().toString();
        if (registry != null && !registry.equals(name)) {
            error(element, "@Registry is already declared on " + registry);
            return;
        }
        List<ExecutableElement> constructors = publicConstructors(element);
        if (constructors.size() != 1) {
            error(element, "@Registry classes need exactly one public constructor");
            return;
        }
        registry = name;
        registryPackage = processingEnv
                .getElementUtils()
                .getPackageOf(element)
                .getQualifiedName()
                .toString();
        ambient = constructors.get(0).getParameters().stream()
                .map(VariableElement::asType)
                .toList();
    }

    void validateComponent(TypeElement component) {
        if (component.getKind() != ElementKind.CLASS || component.getModifiers().contains(Modifier.ABSTRACT)) {
            error(component, "@Wired requires a concrete class");
            return;
        }
        // Type parameters erase to their bounds, so resolution could match arbitrary arguments
        if (!component.getTypeParameters().isEmpty()) {
            error(component, "@Wired classes cannot be generic");
            return;
        }
        if (!isInstantiable(component)) {
            error(component, "@Wired classes must be public and top-level or static nested");
            return;
        }
        List<ExecutableElement> constructors = publicConstructors(component);
        if (constructors.size() != 1) {
            error(component, "@Wired components need exactly one public constructor");
            return;
        }
        if (isSubtype(component, LISTENER)) validateListener(component);
        collect(component, constructors.get(0));
    }

    void validateProduct(ExecutableElement getter) {
        TypeElement owner = (TypeElement) getter.getEnclosingElement();
        if (!getter.getModifiers().contains(Modifier.PUBLIC)
                || !getter.getParameters().isEmpty()
                || getter.getReturnType().getKind() == TypeKind.VOID) {
            error(getter, "@Provides requires a public no-argument getter");
            return;
        }
        if (owner.getAnnotation(Wired.class) == null || !isSubtype(owner, LOADER)) {
            error(getter, "@Provides getters must live on a @Wired loader");
            return;
        }
        String product =
                processingEnv.getTypeUtils().erasure(getter.getReturnType()).toString();
        if (!isReferencable(getter.getReturnType())) {
            error(getter, "Product type must be public: " + product);
            return;
        }
        WiringModel.Product previous = model.addProduct(
                product,
                new WiringModel.Product(
                        owner.getQualifiedName().toString(),
                        getter.getSimpleName().toString()));
        if (previous != null) error(getter, "Product is already provided by " + previous.owner());
    }

    void validateInitializes(TypeElement loader) {
        if (loader.getAnnotation(Wired.class) == null || !isSubtype(loader, LOADER)) {
            error(loader, "@Initializes requires a @Wired loader");
            return;
        }
        for (String holder : holders(loader.getAnnotation(Initializes.class)::value)) {
            String previous =
                    model.addInitializer(holder, loader.getQualifiedName().toString());
            if (previous != null) error(loader, "Holder is already initialized by " + previous);
        }
    }

    void validateRequires(TypeElement component) {
        if (component.getAnnotation(Wired.class) == null) {
            error(component, "@Requires classes must be @Wired");
            return;
        }
        model.addRequirements(
                component.getQualifiedName().toString(), holders(component.getAnnotation(Requires.class)::value));
    }

    // Class values in annotations are only accessible as mirrors during processing
    private List<String> holders(Supplier<Class<?>[]> value) {
        try {
            value.get();
            return List.of();
        } catch (MirroredTypesException ex) {
            return ex.getTypeMirrors().stream()
                    .map(mirror -> processingEnv.getTypeUtils().erasure(mirror).toString())
                    .toList();
        }
    }

    // Only external types can arrive as explicit arguments, so anything from this build must be resolvable
    void validateGraph() {
        if (registry == null)
            processingEnv
                    .getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "No @Registry class to generate the component registry for");
        for (Map.Entry<String, WiringModel.Component> component :
                model.components().entrySet()) {
            TypeElement element = processingEnv.getElementUtils().getTypeElement(component.getKey());
            List<WiringModel.Parameter> parameters = component.getValue().parameters();
            for (int index = 0; index < parameters.size(); index++) {
                WiringModel.Parameter parameter = parameters.get(index);
                if (model.isComponent(parameter.erasedClass())) continue;
                if (parameter.loader())
                    error(
                            parameterAnchor(element, index),
                            "Loader dependency is not @Wired: " + parameter.erasedClass());
                else if (parameter.internal() && !model.isProduct(parameter.erasedClass()))
                    error(
                            parameterAnchor(element, index),
                            "Dependency must be @Wired or a @Provides product: " + parameter.erasedClass());
            }
        }
        for (Map.Entry<String, WiringModel.Product> product : model.products().entrySet()) {
            if (model.isComponent(product.getKey()))
                error(getterAnchor(product.getValue()), "Product type is also a @Wired component: " + product.getKey());
        }
        for (Map.Entry<String, String> initializer : model.initializers().entrySet()) {
            String holder = initializer.getKey();
            Element anchor = processingEnv.getElementUtils().getTypeElement(initializer.getValue());
            if (model.isComponent(holder)) error(anchor, "Initialized holder is also a @Wired component: " + holder);
            else if (model.isProduct(holder))
                error(anchor, "Initialized holder is also a @Provides product: " + holder);
        }
        for (Map.Entry<String, List<String>> requirement : model.requirements().entrySet()) {
            Element anchor = processingEnv.getElementUtils().getTypeElement(requirement.getKey());
            for (String holder : requirement.getValue()) {
                if (!model.isInitialized(holder)) error(anchor, "No @Wired loader initializes: " + holder);
            }
        }
        for (String component : model.components().keySet()) checkCycle(component, new ArrayList<>());
    }

    private void checkCycle(String component, List<String> path) {
        if (path.contains(component)) {
            error(
                    processingEnv.getElementUtils().getTypeElement(component),
                    "Component dependency cycle: " + String.join(" -> ", path) + " -> " + component);
            return;
        }
        path.add(component);
        for (String dependency : model.dependencies(component)) checkCycle(dependency, path);
        path.remove(path.size() - 1);
    }

    private void validateListener(TypeElement listener) {
        List<ExecutableElement> handlers = ElementFilter.methodsIn(listener.getEnclosedElements()).stream()
                .filter(method -> method.getAnnotation(EventHandler.class) != null)
                .toList();
        if (handlers.isEmpty()) error(listener, "Listeners need at least one @EventHandler method");
        for (ExecutableElement handler : handlers) {
            if (!handler.getModifiers().contains(Modifier.PUBLIC)) error(handler, "Event handlers must be public");
            if (handler.getParameters().size() != 1) {
                error(handler, "Event handlers take exactly one event parameter");
                continue;
            }
            TypeMirror event = mirror(EVENT);
            TypeMirror parameter = handler.getParameters().get(0).asType();
            if (event != null && !processingEnv.getTypeUtils().isAssignable(parameter, event))
                error(handler, "Event handler parameter must be a Bukkit event");
        }
    }

    private void collect(TypeElement component, ExecutableElement constructor) {
        Set<String> parameterTypes = new HashSet<>();
        for (VariableElement parameter : constructor.getParameters()) {
            TypeMirror type = parameter.asType();
            String erased = processingEnv.getTypeUtils().erasure(type).toString();
            if (type.getKind().isPrimitive()) error(parameter, "@Wired constructors cannot take primitive parameters");
            else if (!isReferencable(type)) error(parameter, "Constructor parameter types must be public: " + erased);
            // Resolution is type-based, so a second parameter of the same type could only receive the same value
            if (!parameterTypes.add(erased)) error(parameter, "Duplicate constructor parameter type: " + erased);
        }
        List<WiringModel.Parameter> parameters = constructor.getParameters().stream()
                .map(VariableElement::asType)
                .map(type -> new WiringModel.Parameter(
                        processingEnv.getTypeUtils().erasure(type).toString(),
                        type.toString(),
                        isLoader(type),
                        isInternal(type)))
                .toList();
        model.addComponent(component.getQualifiedName().toString(), isSubtype(component, LOADER), parameters);
    }

    private List<ExecutableElement> publicConstructors(TypeElement type) {
        return ElementFilter.constructorsIn(type.getEnclosedElements()).stream()
                .filter(constructor -> constructor.getModifiers().contains(Modifier.PUBLIC))
                .toList();
    }

    private Element parameterAnchor(TypeElement component, int index) {
        List<ExecutableElement> constructors = publicConstructors(component);
        return constructors.size() == 1 ? constructors.get(0).getParameters().get(index) : component;
    }

    private Element getterAnchor(WiringModel.Product product) {
        TypeElement owner = processingEnv.getElementUtils().getTypeElement(product.owner());
        return ElementFilter.methodsIn(owner.getEnclosedElements()).stream()
                .filter(method -> method.getSimpleName().contentEquals(product.getter()))
                .findFirst()
                .map(Element.class::cast)
                .orElse(owner);
    }

    private boolean isInstantiable(TypeElement component) {
        for (Element element = component;
                element instanceof TypeElement type;
                element = element.getEnclosingElement()) {
            if (!type.getModifiers().contains(Modifier.PUBLIC)) return false;
            if (type.getNestingKind() == NestingKind.MEMBER
                    && !type.getModifiers().contains(Modifier.STATIC)) return false;
            if (type.getNestingKind() != NestingKind.TOP_LEVEL && type.getNestingKind() != NestingKind.MEMBER)
                return false;
        }
        return true;
    }

    private boolean isReferencable(TypeMirror type) {
        for (Element element = processingEnv
                        .getTypeUtils()
                        .asElement(processingEnv.getTypeUtils().erasure(type));
                element instanceof TypeElement typeElement;
                element = element.getEnclosingElement()) {
            if (!typeElement.getModifiers().contains(Modifier.PUBLIC)) return false;
        }
        return true;
    }

    private boolean isLoader(TypeMirror type) {
        TypeMirror loader = mirror(LOADER);
        return loader != null && processingEnv.getTypeUtils().isAssignable(type, loader);
    }

    // Internal means declared in this compilation and not already satisfied by a @Registry constructor argument
    private boolean isInternal(TypeMirror type) {
        for (TypeMirror provided : ambient) if (processingEnv.getTypeUtils().isAssignable(provided, type)) return false;
        for (Element element = processingEnv.getTypeUtils().asElement(type);
                element instanceof TypeElement typeElement;
                element = element.getEnclosingElement()) {
            if (sources.contains(typeElement.getQualifiedName().toString())) return true;
        }
        return false;
    }

    private boolean isSubtype(TypeElement type, String supertype) {
        TypeMirror mirror = mirror(supertype);
        return mirror != null && processingEnv.getTypeUtils().isAssignable(type.asType(), mirror);
    }

    private TypeMirror mirror(String qualifiedName) {
        TypeElement element = processingEnv.getElementUtils().getTypeElement(qualifiedName);
        return element == null ? null : element.asType();
    }

    private void error(Element element, String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}

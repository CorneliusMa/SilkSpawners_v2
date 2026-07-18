package de.corneliusmay.silkspawners.wiring.processor;

import com.google.auto.service.AutoService;
import de.corneliusmay.silkspawners.wiring.Provides;
import de.corneliusmay.silkspawners.wiring.Registry;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class WiredProcessor extends AbstractProcessor {

    private final WiringModel model = new WiringModel();

    private WiredValidator validator;

    private boolean generated;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        validator = new WiredValidator(processingEnv, model);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(
                Registry.class.getCanonicalName(), Wired.class.getCanonicalName(), Provides.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        validator.addSources(roundEnvironment);
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Registry.class))
            validator.validateRegistry((TypeElement) element);
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Wired.class))
            validator.validateComponent((TypeElement) element);
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Provides.class))
            validator.validateProduct((ExecutableElement) element);
        // Emit the registry only once and before the final round so it still gets processed itself
        if (!generated && !model.isEmpty() && !roundEnvironment.processingOver()) {
            validator.validateGraph();
            if (validator.registryPackage() != null)
                new RegistryGenerator(model, validator.registryPackage()).write(processingEnv);
            generated = true;
        }
        return true;
    }
}

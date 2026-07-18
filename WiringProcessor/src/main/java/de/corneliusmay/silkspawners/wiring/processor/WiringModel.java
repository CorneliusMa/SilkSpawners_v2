package de.corneliusmay.silkspawners.wiring.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

class WiringModel {

    record Component(boolean loader, List<Parameter> parameters) {}

    record Parameter(String erasedClass, String declaredType, boolean loader, boolean internal) {}

    record Product(String owner, String getter) {}

    private final Map<String, Component> components = new TreeMap<>();

    private final Map<String, Product> products = new TreeMap<>();

    void addComponent(String component, boolean loader, List<Parameter> parameters) {
        components.put(component, new Component(loader, parameters));
    }

    Product addProduct(String product, Product provider) {
        return products.put(product, provider);
    }

    boolean isComponent(String type) {
        return components.containsKey(type);
    }

    boolean isProduct(String type) {
        return products.containsKey(type);
    }

    boolean isEmpty() {
        return components.isEmpty();
    }

    Map<String, Component> components() {
        return Collections.unmodifiableMap(components);
    }

    Map<String, Product> products() {
        return Collections.unmodifiableMap(products);
    }

    List<String> dependencies(String component) {
        List<String> dependencies = new ArrayList<>();
        for (Parameter parameter : components.get(component).parameters()) {
            if (isComponent(parameter.erasedClass())) dependencies.add(parameter.erasedClass());
            else if (isProduct(parameter.erasedClass())) {
                String owner = products.get(parameter.erasedClass()).owner();
                if (isComponent(owner)) dependencies.add(owner);
            }
        }
        return dependencies;
    }

    // Seeded from name-sorted loaders so the load sequence and its console output stay deterministic
    List<String> loadOrder() {
        List<String> order = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        components.forEach((component, definition) -> {
            if (definition.loader()) visit(component, visited, order);
        });
        return order;
    }

    private void visit(String component, Set<String> visited, List<String> order) {
        if (!visited.add(component)) return;
        for (String dependency : dependencies(component)) visit(dependency, visited, order);
        if (components.get(component).loader()) order.add(component);
    }
}

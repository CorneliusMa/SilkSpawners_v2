package de.corneliusmay.silkspawners.plugin.dump;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class DumpObject<S extends DumpObject<S>> extends DumpScope {

    final Map<String, Object> values = new LinkedHashMap<>();

    DumpObject(DumpTree tree) {
        super(tree);
    }

    abstract S self();

    public S value(String key, Object value) {
        values.put(key, value);
        return focus(self());
    }

    void order(List<String> names) {
        Map<String, Object> ordered = new LinkedHashMap<>();
        for (String name : names) if (values.containsKey(name)) ordered.put(name, values.remove(name));
        ordered.putAll(values);
        values.clear();
        values.putAll(ordered);
    }

    public DumpEntry<S> section(String name) {
        DumpEntry<S> child = new DumpEntry<>(tree, self());
        values.put(name, child);
        return focus(child);
    }

    public DumpList<S> list(String name) {
        DumpList<S> child = new DumpList<>(tree, self());
        values.put(name, child);
        return focus(child);
    }

    @Override
    Object contents() {
        return values;
    }
}

package de.corneliusmay.silkspawners.plugin.dump;

import java.util.ArrayList;
import java.util.List;

public final class DumpList<P extends DumpScope> extends DumpScope {

    private final P parent;

    final List<Object> items = new ArrayList<>();

    DumpList(DumpTree tree, P parent) {
        super(tree);
        this.parent = parent;
    }

    public DumpEntry<DumpList<P>> item() {
        DumpEntry<DumpList<P>> child = new DumpEntry<>(tree, this);
        items.add(child);
        return focus(child);
    }

    public DumpList<DumpList<P>> list() {
        DumpList<DumpList<P>> child = new DumpList<>(tree, this);
        items.add(child);
        return focus(child);
    }

    public DumpList<P> item(Object value) {
        items.add(value);
        return focus(this);
    }

    public P end() {
        return focus(parent);
    }

    @Override
    Object contents() {
        return items;
    }
}

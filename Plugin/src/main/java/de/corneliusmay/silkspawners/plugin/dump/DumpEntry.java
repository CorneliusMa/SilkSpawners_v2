package de.corneliusmay.silkspawners.plugin.dump;

public final class DumpEntry<P extends DumpScope> extends DumpObject<DumpEntry<P>> {

    private final P parent;

    DumpEntry(DumpTree tree, P parent) {
        super(tree);
        this.parent = parent;
    }

    @Override
    DumpEntry<P> self() {
        return this;
    }

    public P end() {
        return focus(parent);
    }
}

package de.corneliusmay.silkspawners.plugin.dump;

public final class DumpRoot extends DumpObject<DumpRoot> {

    DumpRoot(DumpTree tree) {
        super(tree);
    }

    @Override
    DumpRoot self() {
        return this;
    }

    @Override
    void error(String message) {
        values.put("error", message);
    }
}

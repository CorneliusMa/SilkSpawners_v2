package de.corneliusmay.silkspawners.plugin.dump;

import java.util.List;

class DumpReport {

    private final DumpTree tree = new DumpTree();

    DumpReport collect(List<Dumpable> sections) {
        sections.forEach(this::collect);
        return this;
    }

    DumpReport collect(Dumpable section) {
        try {
            section.describe(tree.writer());
        } catch (RuntimeException ex) {
            tree.error("collection failed (" + ex + ")");
        }
        return this;
    }

    String render() {
        return tree.render();
    }
}

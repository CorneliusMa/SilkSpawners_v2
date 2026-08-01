package de.corneliusmay.silkspawners.plugin.dump;

class DumpTree {

    private final DumpRoot root = new DumpRoot(this);

    private DumpScope cursor = root;

    DumpRoot writer() {
        return root.focus(root);
    }

    void error(String message) {
        cursor.error(message);
    }

    void focus(DumpScope scope) {
        cursor = scope;
    }

    String render() {
        return DumpJson.render(root.node());
    }
}

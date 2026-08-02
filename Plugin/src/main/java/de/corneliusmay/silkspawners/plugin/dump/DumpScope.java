package de.corneliusmay.silkspawners.plugin.dump;

import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class DumpScope {

    final DumpTree tree;

    private String error;

    abstract Object contents();

    void error(String message) {
        this.error = message;
    }

    final Object node() {
        return error == null ? contents() : Map.of("error", error);
    }

    <S extends DumpScope> S focus(S scope) {
        tree.focus(scope);
        return scope;
    }
}

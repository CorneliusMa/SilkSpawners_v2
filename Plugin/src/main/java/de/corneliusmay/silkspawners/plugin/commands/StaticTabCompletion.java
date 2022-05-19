package de.corneliusmay.silkspawners.plugin.commands;

import java.util.List;

public class StaticTabCompletion implements TabCompletion {

    private final List<String> completions;

    public StaticTabCompletion(String... completions) {
        this.completions = List.of(completions);
    }

    @Override
    public List<String> update() {
        return completions;
    }
}

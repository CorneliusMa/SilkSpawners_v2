package de.corneliusmay.silkspawners.plugin.commands;

public class StaticTabCompletion implements TabCompletion {

    private final String[] completions;

    public StaticTabCompletion(String... completions) {
        this.completions = completions;
    }

    @Override
    public String[] get() {
        return completions;
    }
}

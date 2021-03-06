package de.corneliusmay.silkspawners.plugin.commands.handler;

import org.bukkit.command.CommandSender;

import java.util.List;

public class StaticTabCompletion implements TabCompletion {

    private final List<String> completions;

    public StaticTabCompletion(String... completions) {
        this.completions = List.of(completions);
    }

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender) {
        return completions;
    }
}

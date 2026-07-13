package de.corneliusmay.silkspawners.plugin.commands.handler;

import java.util.List;
import org.bukkit.command.CommandSender;

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

package de.corneliusmay.silkspawners.plugin.commands.handler;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;

public class CompositeTabCompletion implements TabCompletion {

    private final TabCompletion[] completions;

    public CompositeTabCompletion(TabCompletion... completions) {
        this.completions = completions;
    }

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        return Arrays.stream(completions)
                .flatMap(completion -> completion.update(command, sender, args).stream())
                .distinct()
                .toList();
    }
}

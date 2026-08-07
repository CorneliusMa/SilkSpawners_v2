package de.corneliusmay.silkspawners.plugin.commands.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class RepeatingTabCompletion implements TabCompletion {

    private final TabCompletion completion;

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        return completion.update(command, sender, args);
    }
}

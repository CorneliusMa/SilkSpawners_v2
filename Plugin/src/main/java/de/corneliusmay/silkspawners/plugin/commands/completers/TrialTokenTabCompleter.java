package de.corneliusmay.silkspawners.plugin.commands.completers;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.TabCompletion;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class TrialTokenTabCompleter implements TabCompletion {

    private final String token;

    private final int entityPosition;

    private final TabCompletion permitted;

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        if (args.length <= entityPosition) return List.of();
        if (!permitted.update(command, sender, args).contains(args[entityPosition])) return List.of();
        return List.of(token);
    }
}

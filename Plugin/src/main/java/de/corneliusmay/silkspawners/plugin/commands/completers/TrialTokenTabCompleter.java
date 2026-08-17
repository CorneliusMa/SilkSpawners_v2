package de.corneliusmay.silkspawners.plugin.commands.completers;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.TabCompletion;
import java.util.List;
import java.util.function.BooleanSupplier;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class TrialTokenTabCompleter implements TabCompletion {

    private final String token;

    private final BooleanSupplier enabled;

    private final TabCompletion permitted;

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        if (!enabled.getAsBoolean() || permitted.update(command, sender, args).isEmpty()) return List.of();
        return List.of(token);
    }
}

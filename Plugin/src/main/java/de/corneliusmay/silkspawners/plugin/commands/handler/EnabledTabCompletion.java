package de.corneliusmay.silkspawners.plugin.commands.handler;

import java.util.List;
import java.util.function.BooleanSupplier;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class EnabledTabCompletion implements TabCompletion {

    private final BooleanSupplier enabled;

    private final TabCompletion delegate;

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        return enabled.getAsBoolean() ? delegate.update(command, sender, args) : List.of();
    }
}

package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnableEntities;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.weftkit.wiring.Wired;

@Wired
class EntitiesCommand extends SilkSpawnersCommand {

    EntitiesCommand() {
        super("entities", true);
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length != 0) return invalidSyntax(sender);

        sendMessage(
                sender,
                "MESSAGE",
                SpawnableEntities.TYPES.stream()
                        .map(EntityType::getName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(", ")));

        return true;
    }
}

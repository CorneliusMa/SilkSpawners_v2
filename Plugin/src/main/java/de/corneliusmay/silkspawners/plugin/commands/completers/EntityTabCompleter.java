package de.corneliusmay.silkspawners.plugin.commands.completers;

import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.TabCompletion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EntityTabCompleter implements TabCompletion {

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender) {
        return Arrays.stream(EntityType.values()).filter(EntityType::isSpawnable).map(EntityType::getName).filter(Objects::nonNull).filter((entity) -> sender.hasPermission(command.getPermissionString() + "." + entity)).toList();
    }
}

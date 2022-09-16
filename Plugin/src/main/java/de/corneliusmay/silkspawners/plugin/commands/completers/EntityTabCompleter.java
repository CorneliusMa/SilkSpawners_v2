package de.corneliusmay.silkspawners.plugin.commands.completers;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.TabCompletion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EntityTabCompleter implements TabCompletion {

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender) {
        return Arrays.stream(EntityType.values()).filter(EntityType::isSpawnable).map(EntityType::getName).filter(Objects::nonNull).filter((entity) -> {
            if(sender.hasPermission(command.getPermissionString() + "." + entity)) return true;
            else return sender.hasPermission(command.getPermissionString() + ".*");
        }).toList();
    }
}

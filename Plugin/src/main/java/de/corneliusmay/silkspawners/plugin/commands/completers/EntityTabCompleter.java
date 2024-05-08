package de.corneliusmay.silkspawners.plugin.commands.completers;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.TabCompletion;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EntityTabCompleter implements TabCompletion {

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender) {
        List<EntityType> entityTypes = new ArrayList<>();
        entityTypes.add(null); // empty
        entityTypes.addAll(Arrays.stream(EntityType.values()).filter(EntityType::isSpawnable).toList());
        return entityTypes.stream().filter(entityType -> entityType == null || entityType.isSpawnable()).map(entityType -> entityType == null ? Spawner.EMPTY : entityType.getName()).filter(Objects::nonNull).filter((entity) -> {
            if(sender.hasPermission(command.getPermissionString() + "." + entity)) return true;
            else return sender.hasPermission(command.getPermissionString() + ".*");
        }).toList();
    }
}

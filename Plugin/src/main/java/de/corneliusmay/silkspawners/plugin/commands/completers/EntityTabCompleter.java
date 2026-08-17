package de.corneliusmay.silkspawners.plugin.commands.completers;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.TabCompletion;
import de.corneliusmay.silkspawners.plugin.entity.EntityNames;
import de.corneliusmay.silkspawners.plugin.entity.SpawnableEntities;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

public class EntityTabCompleter implements TabCompletion {

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        List<EntityType> entityTypes = new ArrayList<>();
        entityTypes.add(null); // empty
        entityTypes.addAll(SpawnableEntities.TYPES);
        return entityTypes.stream()
                .map(entityType -> entityType == null ? EntityNames.EMPTY : entityType.getName())
                .filter(Objects::nonNull)
                .filter((entity) -> {
                    if (sender.hasPermission(command.getPermissionString() + "." + entity)) return true;
                    else return sender.hasPermission(command.getPermissionString() + ".*");
                })
                .toList();
    }
}

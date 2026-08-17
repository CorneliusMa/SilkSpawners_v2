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

    private final String permissionInfix;

    public EntityTabCompleter() {
        this("");
    }

    public EntityTabCompleter(String permissionInfix) {
        this.permissionInfix = permissionInfix;
    }

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        List<EntityType> entityTypes = new ArrayList<>();
        entityTypes.add(null); // empty
        entityTypes.addAll(SpawnableEntities.TYPES);
        String permission = command.getPermissionString() + "." + permissionInfix;
        return entityTypes.stream()
                .map(entityType -> entityType == null ? EntityNames.EMPTY : entityType.getName())
                .filter(Objects::nonNull)
                .filter(entity -> sender.hasPermission(permission + entity) || sender.hasPermission(permission + "*"))
                .toList();
    }
}

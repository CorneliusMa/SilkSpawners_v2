package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.completers.EntityTabCompleter;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SetCommand extends SilkSpawnersCommand {

    public SetCommand(){
        super("set", true, new EntityTabCompleter());
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if(args.length != 1) return invalidSyntax(sender);
        if(!(sender instanceof Player player)) {
            sendMessage(sender, "PLAYERS_ONLY");
            return false;
        }

        EntityType entityType;
        if (args[0].equalsIgnoreCase(Spawner.EMPTY)) {
            entityType = null;
        } else {
            entityType = EntityType.fromName(args[0]);
            if(entityType == null) {
                sendMessage(sender, "ENTITY_NOT_FOUND", args[0]);
                return false;
            }
        }

        Spawner newSpawner = new Spawner(plugin, entityType);
        if(!newSpawner.isValid()) {
            sendMessage(sender, "ENTITY_NOT_FOUND", args[0]);
            return false;
        }

        if(!player.hasPermission(getPermissionString() + "." + newSpawner.serializedEntityType()) && !sender.hasPermission(getPermissionString() + ".*")) {
            sendMessage(sender, "INSUFFICIENT_ENTITY_PERMISSION", newSpawner.serializedName());
            return false;
        }

        Block block = plugin.getNmsHandler().getTargetBlock(player);
        Spawner spawner = new Spawner(plugin, block);
        if(!spawner.isValid()) {
            sendMessage(sender, "INVALID_TARGET");
            return false;
        }
        newSpawner.setSpawnerBlockType(block, new ArrayList<>());
        sendMessage(sender, "SUCCESS", newSpawner.serializedName());
        return true;
    }
}

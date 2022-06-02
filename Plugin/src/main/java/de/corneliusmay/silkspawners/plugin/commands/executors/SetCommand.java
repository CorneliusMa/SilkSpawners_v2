package de.corneliusmay.silkspawners.plugin.commands.executors;

import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.completers.EntityTabCompleter;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SetCommand extends SilkSpawnersCommand {

    public SetCommand() {
        super("set", true, new EntityTabCompleter());
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if(args.length != 1) return invalidSyntax(sender);
        if(!(sender instanceof Player player)) {
            sender.sendMessage(getMessage("PLAYERS_ONLY"));
            return false;
        }

        Spawner newSpawner = new Spawner(plugin, EntityType.fromName(args[0]));
        if(!newSpawner.isValid()) {
            sender.sendMessage(getMessage("ENTITY_NOT_FOUND", args[0]));
            return false;
        }

        if(!player.hasPermission(getPermissionString() + "." + newSpawner.getEntityType().getName())) {
            sender.sendMessage(getMessage("INSUFFICIENT_ENTITY_PERMISSION", newSpawner.serializedName()));
            return false;
        }

        Block block = player.getTargetBlockExact(5);
        Spawner spawner = new Spawner(plugin, block);
        if(!spawner.isValid()) {
            sender.sendMessage(getMessage("INVALID_TARGET"));
            return false;
        }

        newSpawner.setSpawnerBlockType(block);
        sender.sendMessage(getMessage("SUCCESS", newSpawner.serializedName()));
        return true;
    }
}

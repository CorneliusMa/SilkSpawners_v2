package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.api.events.SpawnerChangeEvent;
import de.corneliusmay.silkspawners.plugin.commands.completers.EntityTabCompleter;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerFactory;
import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@Wired
public class SetCommand extends SilkSpawnersCommand {

    private final SpawnerFactory spawnerFactory;

    private final VersionAdapter versionAdapter;

    public SetCommand(SpawnerFactory spawnerFactory, VersionAdapter versionAdapter) {
        super("set", true, new EntityTabCompleter());
        this.spawnerFactory = spawnerFactory;
        this.versionAdapter = versionAdapter;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return invalidSyntax(sender);
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "PLAYERS_ONLY");
            return false;
        }

        EntityType entityType;
        if (args[0].equalsIgnoreCase(Spawner.EMPTY)) {
            entityType = null;
        } else {
            entityType = EntityType.fromName(args[0]);
            if (entityType == null) {
                sendMessage(sender, "ENTITY_NOT_FOUND", args[0]);
                return false;
            }
        }

        Optional<Spawner> requestedSpawner = spawnerFactory.ofType(entityType);
        if (requestedSpawner.isEmpty()) {
            sendMessage(sender, "ENTITY_NOT_FOUND", args[0]);
            return false;
        }

        Spawner newSpawner = requestedSpawner.get();

        if (!player.hasPermission(getPermissionString() + "." + newSpawner.serializedEntityType())
                && !sender.hasPermission(getPermissionString() + ".*")) {
            sendMessage(sender, "INSUFFICIENT_ENTITY_PERMISSION", newSpawner.coloredName());
            return false;
        }

        Block block = versionAdapter.getTargetBlock(player);
        Optional<Spawner> targetSpawner = spawnerFactory.fromBlock(block);
        if (targetSpawner.isEmpty()) {
            sendMessage(sender, "INVALID_TARGET");
            return false;
        }

        Spawner spawner = targetSpawner.get();

        if (spawner.getEntityType() == newSpawner.getEntityType()) {
            sendMessage(sender, "UNCHANGED", newSpawner.coloredName());
            return true;
        }

        SpawnerChangeEvent event =
                new SpawnerChangeEvent(player, spawner, block.getLocation(), newSpawner, spawnerFactory::snapshot);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        Spawner result = spawnerFactory.of(event.getNewSpawner());
        spawnerFactory.applyToBlock(result, block);
        sendMessage(sender, "SUCCESS", result.coloredName());
        return true;
    }
}

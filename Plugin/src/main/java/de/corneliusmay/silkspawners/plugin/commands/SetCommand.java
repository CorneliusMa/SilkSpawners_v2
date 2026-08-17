package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import de.corneliusmay.silkspawners.api.events.SpawnerChangeEvent;
import de.corneliusmay.silkspawners.api.events.TrialSpawnerChangeEvent;
import de.corneliusmay.silkspawners.plugin.commands.completers.EntityTabCompleter;
import de.corneliusmay.silkspawners.plugin.commands.handler.CompositeTabCompletion;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.entity.EntityNameRenderer;
import de.corneliusmay.silkspawners.plugin.entity.EntityNames;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerFactory;
import de.corneliusmay.silkspawners.plugin.spawner.policy.SpawnerTypeProfile;
import de.corneliusmay.silkspawners.plugin.spawner.trial.TrialSpawner;
import de.corneliusmay.silkspawners.plugin.spawner.trial.TrialSpawnerFactory;
import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.weftkit.wiring.Qualified;
import org.weftkit.wiring.Wired;

@Wired
class SetCommand extends SilkSpawnersCommand {

    private final SpawnerFactory spawnerFactory;

    private final TrialSpawnerFactory trialSpawnerFactory;

    private final VersionAdapter versionAdapter;

    private final EntityNameRenderer entityNames;

    private final SpawnerTypeProfile trialSpawnerProfile;

    SetCommand(
            SpawnerFactory spawnerFactory,
            TrialSpawnerFactory trialSpawnerFactory,
            VersionAdapter versionAdapter,
            EntityNameRenderer entityNames,
            @Qualified("trialSpawner") SpawnerTypeProfile trialSpawnerProfile) {
        super(
                "set",
                true,
                new CompositeTabCompletion(
                        new EntityTabCompleter(),
                        new EntityTabCompleter(trialSpawnerProfile.commandPermissionInfix())));
        this.spawnerFactory = spawnerFactory;
        this.trialSpawnerFactory = trialSpawnerFactory;
        this.versionAdapter = versionAdapter;
        this.entityNames = entityNames;
        this.trialSpawnerProfile = trialSpawnerProfile;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return invalidSyntax(sender);
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "PLAYERS_ONLY");
            return false;
        }

        EntityType entityType;
        if (args[0].equalsIgnoreCase(EntityNames.EMPTY)) {
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

        Block block = versionAdapter.getTargetBlock(player);
        Optional<Spawner> targetSpawner = spawnerFactory.fromBlock(block);
        if (targetSpawner.isPresent()) return setSpawner(player, block, targetSpawner.get(), newSpawner);

        Optional<TrialSpawner> targetTrialSpawner = trialSpawnerFactory.fromBlock(block);
        if (targetTrialSpawner.isPresent())
            return setTrialSpawner(player, block, targetTrialSpawner.get(), entityType, newSpawner);

        if (trialSpawnerFactory.isTrialSpawner(block)) {
            sendMessage(sender, "TRIAL_DISABLED");
            return false;
        }

        sendMessage(sender, "INVALID_TARGET");
        return false;
    }

    private boolean setSpawner(Player player, Block block, Spawner spawner, Spawner newSpawner) {
        if (!hasEntityPermission(player, newSpawner.getEntityType())) return false;

        if (spawner.getEntityType() == newSpawner.getEntityType()) {
            sendMessage(player, "UNCHANGED", entityNames.colored(newSpawner.getEntityType()));
            return true;
        }

        SpawnerChangeEvent event =
                new SpawnerChangeEvent(player, spawner, block.getLocation(), newSpawner, spawnerFactory::snapshot);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        Spawner result = spawnerFactory.of(event.getNewSpawner());
        spawnerFactory.applyToBlock(result, block);
        sendMessage(player, "SUCCESS", entityNames.colored(result.getEntityType()));
        return true;
    }

    private boolean setTrialSpawner(
            Player player, Block block, TrialSpawner trialSpawner, EntityType entityType, Spawner newSpawner) {
        if (!hasTrialEntityPermission(player, newSpawner.getEntityType())) return false;

        if (trialSpawner.getState().spawns(entityType)) {
            sendMessage(player, "UNCHANGED", entityNames.colored(newSpawner.getEntityType()));
            return true;
        }

        TrialSpawnerChangeEvent event = new TrialSpawnerChangeEvent(
                player,
                block.getLocation(),
                trialSpawner.getState(),
                trialSpawner.getState().withEntityType(entityType));
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        TrialSpawnerState result = event.getNewState();
        trialSpawnerFactory.applyToBlock(result, block);
        sendMessage(player, "SUCCESS_TRIAL", entityNames.colored(TrialSpawner.entityType(result)));
        return true;
    }

    private boolean hasEntityPermission(Player player, EntityType entityType) {
        return hasPermissionBranch(player, getPermissionString() + ".", entityType);
    }

    private boolean hasTrialEntityPermission(Player player, EntityType entityType) {
        return hasPermissionBranch(
                player, getPermissionString() + "." + trialSpawnerProfile.commandPermissionInfix(), entityType);
    }

    private boolean hasPermissionBranch(Player player, String branch, EntityType entityType) {
        if (player.hasPermission(branch + EntityNames.serialized(entityType)) || player.hasPermission(branch + "*"))
            return true;
        sendMessage(player, "INSUFFICIENT_ENTITY_PERMISSION", entityNames.colored(entityType));
        return false;
    }
}

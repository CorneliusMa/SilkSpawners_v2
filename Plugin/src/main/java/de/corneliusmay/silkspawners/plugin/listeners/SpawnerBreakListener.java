package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.api.events.SpawnerDropEvent;
import de.corneliusmay.silkspawners.plugin.config.ConfigKey;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.explosion.Explosion;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionTier;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerFactory;
import de.corneliusmay.silkspawners.plugin.spawner.policy.SilkDropCheck;
import de.corneliusmay.silkspawners.plugin.spawner.policy.SpawnerTypeProfile;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import java.util.List;
import java.util.function.BooleanSupplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.weftkit.wiring.Qualified;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class SpawnerBreakListener implements Listener {

    private static final long EXPLOSION_DELAY = 1;

    private final PluginConfig config;

    @Qualified("spawner")
    private final SpawnerTypeProfile profile;

    private final SpawnerFactory spawnerFactory;

    @Qualified("spawner")
    private final SilkDropCheck silkDropCheck;

    private final DenyMessageHandler denyMessageHandler;

    private final ServerPlatform platform;

    private final DropHandler dropHandler;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCall(BlockBreakEvent e) {
        if (e.isCancelled()) return;

        spawnerFactory.fromBlock(e.getBlock()).ifPresent(spawner -> handleSpawnerBreak(e, spawner));
    }

    private void handleSpawnerBreak(BlockBreakEvent e, Spawner spawner) {
        Player p = e.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE) return;

        if (!silkDropCheck.canSilkDrop(p, spawner.serializedEntityType())) {
            destroySpawner(p, e, spawner);
            return;
        }

        int dropChance = profile.dropChance().get();
        SpawnerDropEvent dropEvent = new SpawnerDropEvent(
                p, spawner, e.getBlock().getLocation(), spawner.getItemStack(), dropChance, spawnerFactory::snapshot);
        Bukkit.getPluginManager().callEvent(dropEvent);

        if (dropEvent.isCancelled()) return;

        if (dropHandler.missesDropChance(dropEvent.getDropChance())) {
            destroySpawner(p, e, spawner);
            return;
        }

        SpawnerBreakEvent event =
                new SpawnerBreakEvent(p, spawner, e.getBlock().getLocation(), spawnerFactory::snapshot);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        ItemStack spawnerItem = !dropEvent.hasCustomDrop() && event.hasReplacedSpawner()
                ? event.getSpawner().getItemStack()
                : dropEvent.getDrop();
        boolean exploding =
                explode(config.SPAWNER_EXPLOSION_SILKTOUCH, p, e.getBlock().getLocation(), spawner, event::isCancelled);
        // One tick after the explosion, so the dropped item survives the blast
        dropHandler.drop(e, spawnerItem, exploding ? EXPLOSION_DELAY + 1 : 0);
    }

    private void destroySpawner(Player p, BlockBreakEvent e, Spawner spawner) {
        if (!profile.destroyable().get()) {
            e.setCancelled(true);
            denyMessageHandler.destroy(profile, p);
            return;
        }

        explode(config.SPAWNER_EXPLOSION_NORMAL, p, e.getBlock().getLocation(), spawner, e::isCancelled);
    }

    private boolean explode(
            ConfigKey<List<ExplosionTier>> tierList,
            Player p,
            Location location,
            Spawner spawner,
            BooleanSupplier cancelled) {
        Explosion explosion = new Explosion(tierList, config.SPAWNER_EXPLOSION_ALL);
        if (!explosion.applies(p)) return false;
        platform.runTaskLater(
                location,
                () -> {
                    if (cancelled.getAsBoolean()) return;
                    explosion.run(p, location.getWorld(), location, spawner);
                },
                EXPLOSION_DELAY);
        return true;
    }
}

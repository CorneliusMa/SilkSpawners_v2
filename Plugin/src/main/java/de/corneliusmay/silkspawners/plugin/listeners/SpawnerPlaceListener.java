package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.SpawnerPlaceEvent;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerFactory;
import de.corneliusmay.silkspawners.plugin.spawner.policy.SpawnerTypeProfile;
import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.weftkit.wiring.Qualified;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class SpawnerPlaceListener implements Listener {

    @Qualified("spawner")
    private final SpawnerTypeProfile profile;

    private final SpawnerFactory spawnerFactory;

    private final VersionAdapter versionAdapter;

    private final PlaceHandler placeHandler;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCall(BlockPlaceEvent e) {
        if (e.isCancelled()) return;
        if (e.getBlock().getType() != versionAdapter.getSpawnerMaterial()) return;

        ItemStack[] itemsInHand = versionAdapter.getItemsInHand(e.getPlayer());
        spawnerFactory.fromItem(itemIsSpawner(itemsInHand)).ifPresent(spawner -> handlePlace(e, spawner));
    }

    private void handlePlace(BlockPlaceEvent e, Spawner spawner) {
        placeHandler.handle(
                e,
                profile,
                spawner.serializedEntityType(),
                () -> new SpawnerPlaceEvent(
                        e.getPlayer(), spawner, e.getBlock().getLocation(), spawnerFactory::snapshot),
                event -> spawnerFactory.applyToBlock(spawnerFactory.of(event.getSpawner()), e.getBlock()));
    }

    private ItemStack itemIsSpawner(ItemStack[] items) {
        return itemIsSpawner(items, 0);
    }

    private ItemStack itemIsSpawner(ItemStack[] items, int i) {
        if (items.length == i) return null;

        if (items[i].getType() == versionAdapter.getSpawnerMaterial()) return items[i];
        else return itemIsSpawner(items, i + 1);
    }
}

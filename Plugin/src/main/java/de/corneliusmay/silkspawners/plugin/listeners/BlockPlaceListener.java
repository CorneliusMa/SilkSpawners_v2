package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.SpawnerPlaceEvent;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.spawner.EditedSpawners;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerFactory;
import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import de.corneliusmay.silkspawners.wiring.Wired;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

@Wired
@RequiredArgsConstructor
public class BlockPlaceListener implements Listener {

    private final SpawnerFactory spawnerFactory;

    private final VersionAdapter versionAdapter;

    private final LocaleHandler locale;

    private final EditedSpawners editedSpawners;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCall(BlockPlaceEvent e) {
        if (e.isCancelled()) return;

        ItemStack[] itemsInHand = versionAdapter.getItemsInHand(e.getPlayer());
        spawnerFactory.fromItem(itemIsSpawner(itemsInHand)).ifPresent(spawner -> handleSpawnerPlace(e, spawner));
    }

    private void handleSpawnerPlace(BlockPlaceEvent e, Spawner spawner) {
        Player p = e.getPlayer();

        if (!p.hasPermission("silkspawners.place." + spawner.serializedEntityType())
                && !p.hasPermission("silkspawners.place.*")
                && !PluginConfig.SPAWNER_PERMISSION_DISABLE_PLACE.get()) {
            e.setCancelled(true);
            if (PluginConfig.SPAWNER_MESSAGE_DENY_PLACE.get()) p.sendMessage(locale.getMessage("SPAWNER_PLACE_DENIED"));
            return;
        }

        SpawnerPlaceEvent event =
                new SpawnerPlaceEvent(p, spawner, e.getBlock().getLocation(), spawnerFactory::snapshot);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            e.setCancelled(true);
            return;
        }
        this.editedSpawners.beginEdit(e.getBlock().getLocation());
        Spawner placed = spawnerFactory.of(event.getSpawner());
        spawnerFactory.applyToBlock(placed, e.getBlock());
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

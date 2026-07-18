package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.SpawnerPlaceEvent;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerFactory;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    private final de.corneliusmay.silkspawners.spi.version.Bukkit bukkitHandler;

    private final LocaleHandler locale;

    private final Set<Location> editedSpawners;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCall(BlockPlaceEvent e) {
        if (e.isCancelled()) return;

        ItemStack[] itemsInHand = bukkitHandler.getItemsInHand(e.getPlayer());
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
        this.editedSpawners.add(e.getBlock().getLocation());
        Spawner placed = spawnerFactory.of(event.getSpawner());
        spawnerFactory.applyToBlock(placed, e.getBlock(), this.editedSpawners);
    }

    private ItemStack itemIsSpawner(ItemStack[] items) {
        return itemIsSpawner(items, 0);
    }

    private ItemStack itemIsSpawner(ItemStack[] items, int i) {
        if (items.length == i) return null;

        if (items[i].getType() == bukkitHandler.getSpawnerMaterial()) return items[i];
        else return itemIsSpawner(items, i + 1);
    }
}

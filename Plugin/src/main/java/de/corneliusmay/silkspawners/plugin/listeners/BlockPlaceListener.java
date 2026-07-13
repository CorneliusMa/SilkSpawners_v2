package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.SpawnerPlaceEvent;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener extends SilkSpawnersListener<BlockPlaceEvent> {

    private final Set<Location> editedSpawners;

    public BlockPlaceListener(Set<Location> editedSpawners) {
        this.editedSpawners = editedSpawners;
    }

    @Override
    @EventHandler(priority = EventPriority.HIGHEST)
    protected void onCall(BlockPlaceEvent e) {
        if (e.isCancelled()) return;

        ItemStack[] itemsInHand = plugin.getBukkitHandler().getItemsInHand(e.getPlayer());
        Spawner.fromItem(plugin, itemIsSpawner(itemsInHand)).ifPresent(spawner -> handleSpawnerPlace(e, spawner));
    }

    private void handleSpawnerPlace(BlockPlaceEvent e, Spawner spawner) {
        Player p = e.getPlayer();

        if (!p.hasPermission("silkspawners.place." + spawner.serializedEntityType())
                && !p.hasPermission("silkspawners.place.*")
                && !PluginConfig.SPAWNER_PERMISSION_DISABLE_PLACE.get()) {
            e.setCancelled(true);
            if (PluginConfig.SPAWNER_MESSAGE_DENY_PLACE.get())
                p.sendMessage(plugin.getLocale().getMessage("SPAWNER_PLACE_DENIED"));
            return;
        }

        SpawnerPlaceEvent event =
                new SpawnerPlaceEvent(p, spawner, e.getBlock().getLocation(), type -> new Spawner(plugin, type));
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            e.setCancelled(true);
            return;
        }
        this.editedSpawners.add(e.getBlock().getLocation());
        Spawner placed = Spawner.of(plugin, event.getSpawner());
        placed.setSpawnerBlockType(e.getBlock(), this.editedSpawners);
    }

    private ItemStack itemIsSpawner(ItemStack[] items) {
        return itemIsSpawner(items, 0);
    }

    private ItemStack itemIsSpawner(ItemStack[] items, int i) {
        if (items.length == i) return null;

        if (items[i].getType() == plugin.getBukkitHandler().getSpawnerMaterial()) return items[i];
        else return itemIsSpawner(items, i + 1);
    }
}

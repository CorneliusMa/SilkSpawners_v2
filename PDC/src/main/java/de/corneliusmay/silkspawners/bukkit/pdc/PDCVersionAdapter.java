package de.corneliusmay.silkspawners.bukkit.pdc;

import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public abstract class PDCVersionAdapter implements VersionAdapter {

    @Override
    public ItemStack writeTag(ItemStack itemStack, String tag, String value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.getPersistentDataContainer().set(key(tag), PersistentDataType.STRING, value);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public Map<String, String> readTags(ItemStack itemStack, String... tags) {
        Map<String, String> values = new HashMap<>();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return values;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        for (String tag : tags) {
            String value = container.get(key(tag), PersistentDataType.STRING);
            if (value != null) values.put(tag, value);
        }
        return values;
    }

    private static NamespacedKey key(String tag) {
        return Objects.requireNonNull(NamespacedKey.fromString(tag));
    }
}

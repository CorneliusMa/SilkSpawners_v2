package de.corneliusmay.silkspawners.bukkit.pdc;

import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    public String readTag(ItemStack itemStack, String tag) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(key(tag), PersistentDataType.STRING);
    }

    private static NamespacedKey key(String tag) {
        return Objects.requireNonNull(NamespacedKey.fromString(tag));
    }
}

package de.corneliusmay.silkspawners.bukkit.nbt;

import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public abstract class NBTVersionAdapter implements VersionAdapter {

    private final ItemTag itemTag = new ItemTag();

    @Override
    public ItemStack writeTag(ItemStack itemStack, String tag, String value) {
        return itemTag.write(itemStack, tag, value);
    }

    @Override
    public Map<String, String> readTags(ItemStack itemStack, String... tags) {
        return itemTag.read(itemStack, tags);
    }
}

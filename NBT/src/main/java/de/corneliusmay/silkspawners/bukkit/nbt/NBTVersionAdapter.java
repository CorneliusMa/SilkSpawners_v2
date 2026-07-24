package de.corneliusmay.silkspawners.bukkit.nbt;

import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import org.bukkit.inventory.ItemStack;

public abstract class NBTVersionAdapter implements VersionAdapter {

    private final ItemTag itemTag = new ItemTag();

    @Override
    public ItemStack writeTag(ItemStack itemStack, String tag, String value) {
        return itemTag.write(itemStack, tag, value);
    }

    @Override
    public String readTag(ItemStack itemStack, String tag) {
        return itemTag.read(itemStack, tag);
    }
}

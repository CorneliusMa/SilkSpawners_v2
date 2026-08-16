package de.corneliusmay.silkspawners.bukkit.nbt;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

// Writes to the compound the PersistentDataContainer of newer versions reads from, so items survive server upgrades
class ItemTag {

    private static final String CONTAINER_KEY = "PublicBukkitValues";

    private final MethodHandle asNmsCopy;
    private final MethodHandle asBukkitCopy;
    private final MethodHandle getTag;
    private final MethodHandle setTag;
    private final MethodHandle newCompound;
    private final MethodHandle getCompound;
    private final MethodHandle setCompound;
    private final MethodHandle getString;
    private final MethodHandle setString;

    ItemTag() {
        try {
            String craftPackage =
                    org.bukkit.Bukkit.getServer().getClass().getPackage().getName();
            String version = craftPackage.substring(craftPackage.lastIndexOf('.') + 1);
            Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
            Class<?> nmsItemStack = Class.forName("net.minecraft.server." + version + ".ItemStack");
            Class<?> nbtBase = Class.forName("net.minecraft.server." + version + ".NBTBase");
            Class<?> compound = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");

            // Resolved via getMethod because return types drifted (NBTTagCompound#set returns NBTBase since 1.14)
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            asNmsCopy = lookup.unreflect(craftItemStack.getMethod("asNMSCopy", ItemStack.class));
            asBukkitCopy = lookup.unreflect(craftItemStack.getMethod("asBukkitCopy", nmsItemStack));
            getTag = lookup.unreflect(nmsItemStack.getMethod("getTag"));
            setTag = lookup.unreflect(nmsItemStack.getMethod("setTag", compound));
            newCompound = lookup.unreflectConstructor(compound.getConstructor());
            getCompound = lookup.unreflect(compound.getMethod("getCompound", String.class));
            setCompound = lookup.unreflect(compound.getMethod("set", String.class, nbtBase));
            getString = lookup.unreflect(compound.getMethod("getString", String.class));
            setString = lookup.unreflect(compound.getMethod("setString", String.class, String.class));
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Unsupported server version for raw NBT access", ex);
        }
    }

    ItemStack write(ItemStack itemStack, String key, String value) {
        try {
            Object nmsItem = asNmsCopy.invoke(itemStack);
            if (nmsItem == null) return itemStack;
            Object tag = getTag.invoke(nmsItem);
            if (tag == null) tag = newCompound.invoke();
            Object container = getCompound.invoke(tag, CONTAINER_KEY);
            setString.invoke(container, key, value);
            setCompound.invoke(tag, CONTAINER_KEY, container);
            setTag.invoke(nmsItem, tag);
            return (ItemStack) asBukkitCopy.invoke(nmsItem);
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
    }

    Map<String, String> read(ItemStack itemStack, String... keys) {
        try {
            Map<String, String> values = new HashMap<>();
            Object nmsItem = asNmsCopy.invoke(itemStack);
            if (nmsItem == null) return values;
            Object tag = getTag.invoke(nmsItem);
            if (tag == null) return values;
            Object container = getCompound.invoke(tag, CONTAINER_KEY);
            for (String key : keys) {
                String value = (String) getString.invoke(container, key);
                if (!value.isEmpty()) values.put(key, value);
            }
            return values;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
    }
}

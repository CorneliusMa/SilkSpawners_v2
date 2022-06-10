package de.corneliusmay.silkspawners.plugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private final ItemStack stack;
    private final ItemMeta meta;

    public ItemBuilder(UUID skull) {
        this.stack = new ItemStack(Material.PLAYER_HEAD, 1);
        this.meta = this.stack.getItemMeta();

        SkullMeta skullMeta = (SkullMeta) this.meta;
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(skull));
        this.stack.setItemMeta(skullMeta);
    }

    public ItemBuilder(ItemStack stack) {
        this.stack = stack;
        this.meta = this.stack.getItemMeta();
    }

    public ItemBuilder(Material material, int amount) {
        this.stack = new ItemStack(material, amount);
        this.meta = this.stack.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this.stack = new ItemStack(material, 1);
        this.meta = this.stack.getItemMeta();
    }

    public ItemBuilder setDisplayName(String displayName) {
        this.meta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder addToLore(String lore) {
        ArrayList<String> loreList = this.meta.getLore() == null ? new ArrayList<>() : (ArrayList<String>) this.meta.getLore();
        loreList.add(lore);
        this.meta.setLore(loreList);
        return this;
    }

    public ItemBuilder addToLore(List<String> loreArray) {
        for(String lore : loreArray) addToLore(lore);
        return this;
    }

    public ItemStack build() {
        this.stack.setItemMeta(this.meta);
        return this.stack;
    }
}

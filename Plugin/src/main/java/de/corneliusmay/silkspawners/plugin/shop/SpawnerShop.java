package de.corneliusmay.silkspawners.plugin.shop;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommandHandler;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersEventHandler;
import de.corneliusmay.silkspawners.plugin.shop.commands.OpenShopCommand;
import de.corneliusmay.silkspawners.plugin.shop.config.ConfigHandler;
import de.corneliusmay.silkspawners.plugin.shop.listeners.InventoryClickListener;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class SpawnerShop {

    private final SilkSpawners plugin;

    @Getter
    private final Logger log;

    @Getter
    private Economy economy;

    @Getter
    private ConfigHandler configHandler;

    public SpawnerShop(SilkSpawners plugin) {
        this.plugin = plugin;
        this.log = plugin.getLog();
        this.log.info("Starting SilkSpawners shop");

        this.log.info("Registering economy plugin");
        if(!registerEconomy()) return;

        this.log.info("Registering config handler");
        this.configHandler = new ConfigHandler(plugin, this);

        this.log.info("Registering listeners");
        registerListeners();

        this.log.info("Registering commands");
        registerCommands();

        this.log.info("Started SilkSpawners shop");
    }

    private boolean registerEconomy() {
        if(plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            log.error("You need to have the Vault plugin installed to use the shop extension");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            log.error("No economy plugin was found");
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    private void registerListeners() {
        SilkSpawnersEventHandler eventHandler = new SilkSpawnersEventHandler(plugin);
        eventHandler.registerListener(new InventoryClickListener(this));
    }

    private void registerCommands() {
        SilkSpawnersCommandHandler shopCommandHandler = new SilkSpawnersCommandHandler(plugin, "spawnershop", new OpenShopCommand(this));
        shopCommandHandler.register();
    }

    public void disable() {

    }
}

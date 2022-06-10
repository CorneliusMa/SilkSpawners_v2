package de.corneliusmay.silkspawners.plugin.shop.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.shop.SpawnerShop;
import de.corneliusmay.silkspawners.plugin.shop.handler.ShopGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenShopCommand extends SilkSpawnersCommand {

    private final SpawnerShop spawnerShop;

    public OpenShopCommand(SpawnerShop spawnerShop) {
        super("open", true);
        this.spawnerShop = spawnerShop;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sendMessage(sender, "PLAYERS_ONLY");
            return false;
        }
        if(args.length > 0) return invalidSyntax(sender);

        new ShopGUI(plugin, player, spawnerShop.getConfigHandler().getShopItems(new ConfigValue<String>(PluginConfig.SHOP_CONFIG).get())).open();
        return true;
    }
}

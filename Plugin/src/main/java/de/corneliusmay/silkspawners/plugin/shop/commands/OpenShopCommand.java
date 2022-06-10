package de.corneliusmay.silkspawners.plugin.shop.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueArray;
import de.corneliusmay.silkspawners.plugin.shop.SpawnerShop;
import de.corneliusmay.silkspawners.plugin.shop.handler.ShopGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenShopCommand extends SilkSpawnersCommand {

    private final SpawnerShop spawnerShop;

    public OpenShopCommand(SpawnerShop spawnerShop) {
        super("open", true,
                (command, sender) -> new ConfigValueArray<String>(PluginConfig.SHOP_CONFIG).get().stream().filter((shop) -> sender.hasPermission("spawnershop.use." + shop.toLowerCase())).toList());
        this.spawnerShop = spawnerShop;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sendMessage(sender, "PLAYERS_ONLY");
            return false;
        }
        if(args.length > 1) return invalidSyntax(sender);

        if(args.length == 0) new ShopGUI(spawnerShop, plugin, player, new ConfigValueArray<String>(PluginConfig.SHOP_CONFIG).get().get(0)).open();
        else {
            String shop = spawnerShop.getConfigHandler().getShop(player, args[0]);
            if(shop == null) {
                sendMessage(sender, "SHOP_NOT_FOUND", args[0]);
                return false;
            }

            new ShopGUI(spawnerShop, plugin, player, shop).open();
        }

        return true;
    }
}

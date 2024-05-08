package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.completers.EntityTabCompleter;
import de.corneliusmay.silkspawners.plugin.commands.completers.OnlinePlayersTabCompleter;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand extends SilkSpawnersCommand {

    public GiveCommand() {
        super("give", true, new OnlinePlayersTabCompleter(), new EntityTabCompleter());
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if(args.length < 2 || args.length > 3) return invalidSyntax(sender);

        Player p = Bukkit.getPlayer(args[0]);
        if(p == null) {
            sendMessage(sender, "PLAYER_NOT_FOUND", args[0]);
            return false;
        }

        EntityType entityType;
        if (args[1].equalsIgnoreCase("none")) {
            entityType = null;
        } else {
            entityType = EntityType.fromName(args[1]);
            if(entityType == null) {
                sendMessage(sender, "ENTITY_NOT_FOUND", args[1]);
                return false;
            }
        }

        Spawner spawner = new Spawner(plugin, entityType);
        if(!spawner.isValid()) {
            sendMessage(sender, "ENTITY_NOT_FOUND", args[1]);
            return false;
        }

        if(!sender.hasPermission(getPermissionString() + "." + spawner.serializedEntityType()) && !sender.hasPermission(getPermissionString() + ".*")) {
            sendMessage(sender, "INSUFFICIENT_ENTITY_PERMISSION", spawner.serializedName());
            return false;
        }

        int amount = 1;
        if(args.length == 3) amount = parseAmount(args[2]);

        if(amount == -1) {
            sendMessage(sender, "INVALID_AMOUNT", args[2]);
            return false;
        }

        if(amount < 1) {
            sendMessage(sender, "TOO_SMALL_AMOUNT");
            return false;
        }

        ItemStack item = spawner.getItemStack();
        item.setAmount(amount);
        p.getInventory().addItem(item);
        sendMessage(sender, "SUCCESS", amount, spawner.serializedName(), amount > 1? "s" : "", p.getName());
        sendMessage(p, "SUCCESS_TARGET", amount, spawner.serializedName(), amount > 1? "s" : "", sender.getName());
        return true;
    }

    private Integer parseAmount(String amount) {
        try {
            return Integer.parseInt(amount);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}

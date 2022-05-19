package de.corneliusmay.silkspawners.plugin.commands.executors;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.completers.OnlinePlayersTabCompleter;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class GiveCommand extends SilkSpawnersCommand {

    public GiveCommand() {
        super("give", true, new OnlinePlayersTabCompleter(), () -> Arrays.stream(EntityType.values()).filter(EntityType::isSpawnable).map(EntityType::getName).filter(Objects::nonNull).toList());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length < 2 || args.length > 3) {
            sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §ePlease use /silkspawners give <Player> <Mob> [Amount]");
            return false;
        }

        Player p = Bukkit.getPlayer(args[0]);
        if(p == null) {
            sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §7The Player §c" + args[0] + "§7 is not online.");
            return false;
        }

        Spawner spawner = new Spawner(EntityType.fromName(args[1]));
        if(!spawner.isValid()) {
            sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §7The Entity §c" + args[1] + "§7 is no valid spawner mob.");
            return false;
        }

        int amount = 1;
        if(args.length == 3) amount = parseAmount(args[2]);

        if(amount == -1) {
            sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + "§7The amount §c" + args[2] + "§7 is no number.");
            return false;
        }

        if(amount < 1) {
            sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + "§7The amount must be at least 1.");
            return false;
        }

        ItemStack item = spawner.getItemStack();
        item.setAmount(amount);
        p.getInventory().addItem(item);
        sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §7Gave " + amount +  " " + args[1] +  " spawner" + (amount > 1? "s" : "") + " to " + p.getName() + ".");
        p.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §7You received " + amount +  " " + args[1] +  " spawner" + (amount > 1? "s" : "") + ".");
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

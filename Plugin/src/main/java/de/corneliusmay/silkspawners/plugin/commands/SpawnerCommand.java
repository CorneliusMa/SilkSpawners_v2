package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class SpawnerCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player && !commandSender.hasPermission("silkspawners.command")) {
            commandSender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + "§f" + SilkSpawners.getInstance().getPluginConfig().getInsufficientPermissionMessage());
            return false;
        }

        if(args.length < 2 || args.length > 3) {
            commandSender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + "§ePlease use /spawner <Player> <Mob> [Amount]");
            return false;
        }

        Player p = Bukkit.getPlayer(args[0]);
        if(p == null) {
            commandSender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + "§7The Player §c" + args[0] + "§7 is not online.");
            return false;
        }

        Spawner spawner = new Spawner(EntityType.fromName(args[1]));
        if(!spawner.isValid()) {
            commandSender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + "§7The Entity §c" + args[1] + "§7 is no valid spawner mob.");
            return false;
        }

        int amount = 1;
        if(args.length == 3) amount = parseAmount(args[2]);

        if(amount == -1) {
            commandSender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + "§7The amount §c" + args[2] + "§7 is no number.");
            return false;
        }

        if(amount < 1) {
            commandSender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + "§7The amount must be at least 1.");
            return false;
        }

        ItemStack item = spawner.getItemStack();
        item.setAmount(amount);
        p.getInventory().addItem(item);
        commandSender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + "§7Gave " + amount +  " " + args[1] +  " spawner" + (amount > 1? "s" : "") + " to " + p.getName() + ".");
        p.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + "§7You received " + amount +  " " + args[1] +  " spawner" + (amount > 1? "s" : "") + ".");
        return true;
    }

    private Integer parseAmount(String amount) {
        try {
            return Integer.parseInt(amount);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();
        switch (args.length) {
            case 0:
                completions = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
                break;
            case 1:
                StringUtil.copyPartialMatches(args[0], Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()), completions);
                break;
            case 2:
                StringUtil.copyPartialMatches(args[1], Arrays.stream(EntityType.values()).map(EntityType::getName).filter(Objects::nonNull).collect(Collectors.toList()), completions);
                break;
        }

        Collections.sort(completions);
        return completions;
    }
}

package de.corneliusmay.silkspawners.plugin.commands.executors;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.StaticTabCompletion;
import de.corneliusmay.silkspawners.plugin.commands.completers.OnlinePlayersTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class ExplosionCommand extends SilkSpawnersCommand {

    public ExplosionCommand() {
        super("explosion", true, new StaticTabCompletion("enable", "disable", "setting"), new OnlinePlayersTabCompleter());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length != 2) {
            sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §ePlease use /silkspawners explosion <enable/disable/setting> <Player>");
            return false;
        }

        Player p = Bukkit.getPlayer(args[1]);
        if(p == null) {
            sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §7The Player §c" + args[0] + "§7 is not online.");
            return false;
        }

        PermissionAttachment attachment = p.addAttachment(SilkSpawners.getInstance());
        switch (args[0]) {
            case "enable", "e" -> {
                attachment.setPermission("silkspawners.explosion", true);
                sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §cEnabled §7spawner explosion for " + p.getName() + ".");
            }
            case "disable", "d" -> {
                attachment.setPermission("silkspawners.explosion", false);
                sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §aDisabled §7spawner explosion for " + p.getName() + ".");
            }
            case "setting", "s" -> sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §7Explosions for " + p.getName() + " are " + (p.hasPermission("silkspawners.explosion")? "§cenabled" : "§adisabled" + "§7."));
            default -> {
                sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §ePlease use /silkspawners permissions <commands/spawners/all>");
                return false;
            }
        }
        return true;
    }
}

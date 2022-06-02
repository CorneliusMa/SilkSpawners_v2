package de.corneliusmay.silkspawners.plugin.commands.executors;

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
    protected boolean execute(CommandSender sender, String[] args) {
        if(args.length != 2) return invalidSyntax(sender);

        Player p = Bukkit.getPlayer(args[1]);
        if(p == null) {
            sender.sendMessage(getMessage("PLAYER_NOT_FOUND", args[1]));
            return false;
        }

        PermissionAttachment attachment = p.addAttachment(plugin);
        switch (args[0].toLowerCase()) {
            case "enable", "e" -> {
                attachment.setPermission("silkspawners.explosion", true);
                sender.sendMessage(getMessage("ENABLED", p.getName()));
            }
            case "disable", "d" -> {
                attachment.setPermission("silkspawners.explosion", false);
                sender.sendMessage(getMessage("DISABLED", p.getName()));
            }
            case "setting", "s" -> sender.sendMessage(getMessage("SETTING_" + (p.hasPermission("silkspawners.explosion")? "ENABLED" : "DISABLED"), p.getName()));
            default -> invalidSyntax(sender);
        }
        return true;
    }
}

package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.completers.OnlinePlayersTabCompleter;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.StaticTabCompletion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExplosionCommand extends SilkSpawnersCommand {

    public ExplosionCommand() {
        super(
                "explosion",
                true,
                new StaticTabCompletion("enable", "disable", "setting"),
                new OnlinePlayersTabCompleter());
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length != 2) return invalidSyntax(sender);

        Player p = Bukkit.getPlayer(args[1]);
        if (p == null) {
            sendMessage(sender, "PLAYER_NOT_FOUND", args[1]);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "enable", "e" -> setExplosionPermission(sender, p, true);
            case "disable", "d" -> setExplosionPermission(sender, p, false);
            case "setting", "s" -> sendExplosionSetting(sender, p);
            default -> {
                return invalidSyntax(sender);
            }
        }
        return true;
    }

    private void setExplosionPermission(CommandSender sender, Player p, boolean enabled) {
        plugin.getPlatform()
                .runOnEntity(
                        p,
                        () -> {
                            p.addAttachment(plugin).setPermission("silkspawners.explosion", enabled);
                            sendMessage(sender, settingName(enabled), p.getName());
                        },
                        () -> sendMessage(sender, "PLAYER_NOT_FOUND", p.getName()));
    }

    private void sendExplosionSetting(CommandSender sender, Player p) {
        plugin.getPlatform()
                .runOnEntity(
                        p,
                        () -> {
                            boolean enabled = p.hasPermission("silkspawners.explosion");
                            sendMessage(sender, "SETTING_" + settingName(enabled), p.getName());
                        },
                        () -> sendMessage(sender, "PLAYER_NOT_FOUND", p.getName()));
    }

    private String settingName(boolean enabled) {
        return enabled ? "ENABLED" : "DISABLED";
    }
}

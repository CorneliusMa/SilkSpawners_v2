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
            case "enable", "e" ->
                plugin.getPlatform()
                        .runOnEntity(
                                p,
                                () -> {
                                    p.addAttachment(plugin).setPermission("silkspawners.explosion", true);
                                    sendMessage(sender, "ENABLED", p.getName());
                                },
                                () -> sendMessage(sender, "PLAYER_NOT_FOUND", p.getName()));
            case "disable", "d" ->
                plugin.getPlatform()
                        .runOnEntity(
                                p,
                                () -> {
                                    p.addAttachment(plugin).setPermission("silkspawners.explosion", false);
                                    sendMessage(sender, "DISABLED", p.getName());
                                },
                                () -> sendMessage(sender, "PLAYER_NOT_FOUND", p.getName()));
            case "setting", "s" ->
                plugin.getPlatform()
                        .runOnEntity(
                                p,
                                () -> sendMessage(
                                        sender,
                                        "SETTING_"
                                                + (p.hasPermission("silkspawners.explosion") ? "ENABLED" : "DISABLED"),
                                        p.getName()),
                                () -> sendMessage(sender, "PLAYER_NOT_FOUND", p.getName()));
            default -> {
                return invalidSyntax(sender);
            }
        }
        return true;
    }
}

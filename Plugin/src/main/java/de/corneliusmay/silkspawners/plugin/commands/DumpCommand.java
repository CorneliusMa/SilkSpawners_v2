package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.dump.Dump;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.spi.message.ClickAction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.weftkit.wiring.Wired;

@Wired
class DumpCommand extends SilkSpawnersCommand {

    private final Logger logger;

    private final Dump dump;

    DumpCommand(Dump dump, Logger logger) {
        super("dump", true);
        this.logger = logger;
        this.dump = dump;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length != 0) return invalidSyntax(sender);

        sendMessage(sender, "CREATING");
        dump.create(url -> succeed(sender, url), path -> sendMessage(sender, "ERROR", path));
        return true;
    }

    private void succeed(CommandSender sender, String url) {
        sendInteractive(sender, ClickAction.openUrl(url), "SUCCESS", url);
        if (sender instanceof Player) logger.info("A dump was uploaded to " + url);
    }
}

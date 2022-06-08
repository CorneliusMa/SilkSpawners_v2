package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Objects;

public class EntitiesCommand extends SilkSpawnersCommand {

    public EntitiesCommand() {
        super("entities", true);
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if(args.length != 0) return invalidSyntax(sender);

        sender.sendMessage(getMessage("MESSAGE", Arrays.toString(Arrays.stream(EntityType.values()).filter(EntityType::isSpawnable).map(EntityType::getName).filter(Objects::nonNull).toArray())
                .replace("[", "").replace("]", "")));

        return true;
    }
}

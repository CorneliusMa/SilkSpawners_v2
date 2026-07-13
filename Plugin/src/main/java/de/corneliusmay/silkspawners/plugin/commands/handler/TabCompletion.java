package de.corneliusmay.silkspawners.plugin.commands.handler;

import java.util.List;
import org.bukkit.command.CommandSender;

public interface TabCompletion {

    List<String> update(SilkSpawnersCommand command, CommandSender sender);
}

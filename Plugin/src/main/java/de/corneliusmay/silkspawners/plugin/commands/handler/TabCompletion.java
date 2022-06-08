package de.corneliusmay.silkspawners.plugin.commands.handler;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface TabCompletion {

    List<String> update(SilkSpawnersCommand command, CommandSender sender);
}

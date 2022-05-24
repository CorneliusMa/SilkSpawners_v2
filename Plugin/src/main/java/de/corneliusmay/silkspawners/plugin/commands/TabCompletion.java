package de.corneliusmay.silkspawners.plugin.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface TabCompletion {

    List<String> update(SilkSpawnersCommand command, CommandSender sender);
}

package de.corneliusmay.silkspawners.plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SilkSpawnersTabCompleter implements TabCompleter {

    private final SilkSpawnersCommandHandler commandHandler;

    public SilkSpawnersTabCompleter(SilkSpawnersCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command c, String s, String[] args) {
        List<String> completions = new ArrayList<>();
        SilkSpawnersCommand command = commandHandler.getCommand(args[0]);

        if(args.length < 2) {
            StringUtil.copyPartialMatches(args[args.length - 1], commandHandler.getCommands(commandSender), completions);
        } else if(command.getCompletions().length >= args.length - 1 && commandSender.hasPermission("silkspawners.command." + command.getCommand())) {
            StringUtil.copyPartialMatches(args[args.length - 1], List.of(command.getCompletions()[args.length - 2].get()), completions);
        }

        Collections.sort(completions);
        return completions;
    }
}

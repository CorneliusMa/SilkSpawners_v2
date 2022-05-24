package de.corneliusmay.silkspawners.plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class SilkSpawnersTabCompleter implements TabCompleter {

    private final SilkSpawnersCommandHandler commandHandler;

    public SilkSpawnersTabCompleter(SilkSpawnersCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command c, String s, String[] args) {
        args = Arrays.stream(args).map(String::toLowerCase).toList().toArray(String[]::new);
        List<String> completions = new ArrayList<>();
        SilkSpawnersCommand command = commandHandler.getCommand(args[0]);

        if(args.length < 2) {
            StringUtil.copyPartialMatches(args[args.length - 1], commandHandler.getCommands(commandSender), completions);
        } else if(command != null && command.getCompletions().length >= args.length - 1 && command.hasPermission(commandSender)) {
            StringUtil.copyPartialMatches(args[args.length - 1], command.getCompletions()[args.length - 2].update(command, commandSender), completions);
        }

        Collections.sort(completions);
        return completions;
    }
}

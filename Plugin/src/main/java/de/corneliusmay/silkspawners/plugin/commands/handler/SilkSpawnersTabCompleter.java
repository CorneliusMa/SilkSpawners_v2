package de.corneliusmay.silkspawners.plugin.commands.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

class SilkSpawnersTabCompleter implements TabCompleter {

    private final SilkSpawnersCommandHandler commandHandler;

    SilkSpawnersTabCompleter(SilkSpawnersCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command c, String s, String[] args) {
        args = Arrays.stream(args).map(String::toLowerCase).toList().toArray(String[]::new);
        List<String> completions = new ArrayList<>();
        SilkSpawnersCommand command = commandHandler.getCommand(args[0]);

        if (args.length < 2) {
            StringUtil.copyPartialMatches(
                    args[args.length - 1], commandHandler.getCommands(commandSender), completions);
        } else if (command != null && command.hasPermission(commandSender)) {
            TabCompletion completion = completion(command, args.length - 2);
            if (completion != null)
                StringUtil.copyPartialMatches(
                        args[args.length - 1],
                        completion.update(command, commandSender, Arrays.copyOfRange(args, 1, args.length)),
                        completions);
        }

        Collections.sort(completions);
        return completions;
    }

    private TabCompletion completion(SilkSpawnersCommand command, int index) {
        TabCompletion[] completions = command.getCompletions();
        if (index < completions.length) return completions[index];
        if (completions.length == 0) return null;

        TabCompletion last = completions[completions.length - 1];
        return last instanceof RepeatingTabCompletion ? last : null;
    }
}

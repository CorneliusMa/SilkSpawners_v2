package de.corneliusmay.silkspawners.plugin.commands.completers;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.TabCompletion;
import de.corneliusmay.silkspawners.plugin.config.ConfigEditor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class ConfigKeyTabCompleter implements TabCompletion {

    private final ConfigEditor editor;

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        if (args.length == 0) return List.of();
        if (!args[0].equalsIgnoreCase("get") && !args[0].equalsIgnoreCase("set")) return List.of();
        if (args[0].equalsIgnoreCase("set") && !command.hasSubPermission(sender, "set")) return List.of();

        return editor.settablePaths();
    }
}

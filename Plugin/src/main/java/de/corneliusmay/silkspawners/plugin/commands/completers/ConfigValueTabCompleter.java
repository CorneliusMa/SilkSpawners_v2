package de.corneliusmay.silkspawners.plugin.commands.completers;

import de.corneliusmay.silkspawners.plugin.commands.ConfigCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.TabCompletion;
import de.corneliusmay.silkspawners.plugin.config.ConfigEditor;
import de.corneliusmay.silkspawners.plugin.config.ConfigKey;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class ConfigValueTabCompleter implements TabCompletion {

    private final ConfigEditor editor;

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        if (args.length < 2 || !args[0].equalsIgnoreCase("set")) return List.of();
        if (!ConfigCommand.canSet(command, sender)) return List.of();

        ConfigKey<?> key = editor.find(args[1]);
        if (key == null || !key.isSettable()) return List.of();

        List<String> allowed = editor.allowedValues(key);
        if (!allowed.isEmpty()) return allowed;

        String current = editor.currentValue(key);
        return current.isEmpty() ? List.of() : List.of(current);
    }
}

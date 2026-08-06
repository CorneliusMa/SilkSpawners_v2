package de.corneliusmay.silkspawners.plugin.commands.completers;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.TabCompletion;
import de.corneliusmay.silkspawners.plugin.config.ConfigEditor;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionTierEditor;
import java.util.List;
import org.bukkit.command.CommandSender;

public class ConfigArgumentTabCompleter implements TabCompletion {

    private final TabCompletion keys;

    private final TabCompletion values;

    private final TabCompletion explosion;

    public ConfigArgumentTabCompleter(ConfigEditor editor, ExplosionTierEditor tierEditor) {
        this.keys = new ConfigKeyTabCompleter(editor);
        this.values = new ConfigValueTabCompleter(editor);
        this.explosion = new ConfigExplosionTabCompleter(tierEditor);
    }

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        if (args[0].equals("explosion")) return explosion.update(command, sender, args);
        if (args.length == 2) return keys.update(command, sender, args);
        if (args.length == 3) return values.update(command, sender, args);
        return List.of();
    }
}

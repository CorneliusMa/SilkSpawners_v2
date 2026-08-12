package de.corneliusmay.silkspawners.plugin.commands.completers;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.TabCompletion;
import de.corneliusmay.silkspawners.plugin.config.ConfigKey;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionTier;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionTierEditor;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class ConfigExplosionTabCompleter implements TabCompletion {

    private final ExplosionTierEditor tierEditor;

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        if (!args[0].equals("explosion")) return List.of();
        if (args.length == 2)
            return command.hasSubPermission(sender, "set") ? List.of("list", "add", "remove") : List.of("list");

        return switch (args[1]) {
            case "list" -> args.length == 3 ? tierEditor.scopeNames() : List.of();
            case "add" -> add(command, sender, args);
            case "remove" -> remove(command, sender, args);
            default -> List.of();
        };
    }

    private List<String> add(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        if (!command.hasSubPermission(sender, "set")) return List.of();
        if (args.length == 3) return tierEditor.scopeNames();
        return args.length == 6 || args.length == 7 ? List.of("true", "false") : List.of();
    }

    private List<String> remove(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        if (!command.hasSubPermission(sender, "set")) return List.of();
        if (args.length == 3) return tierEditor.scopeNames();
        if (args.length != 4) return List.of();

        ConfigKey<List<ExplosionTier>> scope = tierEditor.scope(args[2]);
        if (scope == null) return List.of();
        return IntStream.rangeClosed(1, scope.get().size())
                .mapToObj(String::valueOf)
                .toList();
    }
}

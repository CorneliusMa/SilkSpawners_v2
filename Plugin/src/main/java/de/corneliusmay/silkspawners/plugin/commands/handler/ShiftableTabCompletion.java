package de.corneliusmay.silkspawners.plugin.commands.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class ShiftableTabCompletion implements TabCompletion {

    private final String token;

    private final int tokenPosition;

    private final TabCompletion delegate;

    @Override
    public List<String> update(SilkSpawnersCommand command, CommandSender sender, String[] args) {
        if (args.length <= tokenPosition || !token.equalsIgnoreCase(args[tokenPosition])) return List.of();
        return delegate.update(command, sender, args);
    }
}

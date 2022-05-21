package de.corneliusmay.silkspawners.plugin.commands.completers;

import de.corneliusmay.silkspawners.plugin.commands.TabCompletion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.List;

public class OnlinePlayersTabCompleter implements TabCompletion {

    @Override
    public List<String> update(CommandSender sender) {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList();
    }
}

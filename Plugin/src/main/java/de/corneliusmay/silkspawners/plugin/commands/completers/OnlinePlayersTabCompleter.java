package de.corneliusmay.silkspawners.plugin.commands.completers;

import de.corneliusmay.silkspawners.plugin.commands.TabCompletion;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;

import java.util.List;

public class OnlinePlayersTabCompleter implements TabCompletion {

    @Override
    public List<String> update() {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList();
    }
}

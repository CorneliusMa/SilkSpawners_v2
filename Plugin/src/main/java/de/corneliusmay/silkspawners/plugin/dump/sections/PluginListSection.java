package de.corneliusmay.silkspawners.plugin.dump.sections;

import de.corneliusmay.silkspawners.plugin.dump.DumpList;
import de.corneliusmay.silkspawners.plugin.dump.DumpObject;
import de.corneliusmay.silkspawners.plugin.dump.Dumpable;
import java.util.Arrays;
import java.util.Comparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.weftkit.wiring.Wired;

@Wired
class PluginListSection implements Dumpable {

    @Override
    public void describe(DumpObject<?> writer) {
        DumpList<?> plugins = writer.list("plugins");
        Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .sorted(Comparator.comparing(Plugin::getName, String.CASE_INSENSITIVE_ORDER))
                .forEach(plugin -> plugins.item()
                        .value("name", plugin.getName())
                        .value("version", plugin.getDescription().getVersion())
                        .value("enabled", plugin.isEnabled()));
    }
}

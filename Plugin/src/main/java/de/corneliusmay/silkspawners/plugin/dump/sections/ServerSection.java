package de.corneliusmay.silkspawners.plugin.dump.sections;

import de.corneliusmay.silkspawners.plugin.dump.DumpObject;
import de.corneliusmay.silkspawners.plugin.dump.Dumpable;
import org.bukkit.Bukkit;
import org.weftkit.wiring.Wired;

@Wired
class ServerSection implements Dumpable {

    @Override
    public void describe(DumpObject<?> writer) {
        writer.section("server")
                .value("brand", Bukkit.getName())
                .value("version", Bukkit.getVersion())
                .value("bukkit-version", Bukkit.getBukkitVersion());
    }
}

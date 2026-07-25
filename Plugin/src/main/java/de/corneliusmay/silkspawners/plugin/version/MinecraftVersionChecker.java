package de.corneliusmay.silkspawners.plugin.version;

import java.util.Comparator;
import java.util.Set;

class MinecraftVersionChecker {

    private static final Set<Baseline> SUPPORTED_BASELINES = Set.of(
            new Baseline(1, 21, 3),
            new Baseline(1, 20, 5),
            new Baseline(1, 16, 5),
            new Baseline(1, 16, 0),
            new Baseline(1, 13, 1),
            new Baseline(1, 12, 2),
            new Baseline(1, 9, 4),
            new Baseline(1, 8, 0));

    static String getBukkitVersion(MinecraftVersion version) {
        // As of Minecraft version 1.20.5, Paper ships with a Mojang-mapped runtime instead of reobfuscating the server
        // to Spigot mappings. This means that the package name of the server implementation is no longer a reliable
        // way to determine the server version. Instead, we can use the Bukkit version string.
        //
        // The following code also means that we don't have to update the plugin for every new Minecraft version
        // unless the Bukkit API changes in a way that explicitly breaks it.
        //
        // The module name is derived from the baseline, so a version can never dispatch to a mismatched module.
        return SUPPORTED_BASELINES.stream()
                .filter(baseline -> version.isNewerOrEqualTo(baseline.major(), baseline.minor(), baseline.patch()))
                .max(Comparator.comparingInt(Baseline::major)
                        .thenComparingInt(Baseline::minor)
                        .thenComparingInt(Baseline::patch))
                .map(MinecraftVersionChecker::moduleName)
                .orElse(null);
    }

    private static String moduleName(Baseline baseline) {
        String name = "v" + baseline.major() + "_" + baseline.minor();
        return baseline.patch() > 0 ? name + "_" + baseline.patch() : name;
    }

    private record Baseline(int major, int minor, int patch) {}
}

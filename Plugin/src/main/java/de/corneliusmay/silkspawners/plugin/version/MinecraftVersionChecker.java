package de.corneliusmay.silkspawners.plugin.version;

class MinecraftVersionChecker {

    static String getServerVersion() {
        // As of Minecraft version 1.20.5, Paper ships with a Mojang-mapped runtime instead of reobfuscating the server
        // to Spigot mappings. This means that the package name of the server implementation is no longer a reliable
        // way to determine the server version. Instead, we can use the Bukkit version string.
        //
        // The following code also means that we don't have to update the plugin for every new Minecraft version
        // unless the Bukkit API changes in a way that explicitly breaks it.
        if(MinecraftVersion.versionIsNewerOrEqualTo(1, 20, 5)) {
            return "v1_20_R4";
        }
        if(MinecraftVersion.versionIsNewerOrEqualTo(1, 13, 1)) {
            return "v1_13_R2";
        }
        if(MinecraftVersion.versionIsNewerOrEqualTo(1, 12, 0)) {
            return "v1_12_R1";
        }
        if(MinecraftVersion.versionIsNewerOrEqualTo(1, 9, 4)) {
            return "v1_9_R2";
        }
        if(MinecraftVersion.versionIsNewerOrEqualTo(1, 8, 4)) {
            return "v1_8_R3";
        }
        return null;
    }
}

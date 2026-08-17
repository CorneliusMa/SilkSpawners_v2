package de.corneliusmay.silkspawners.plugin.platform;

import de.corneliusmay.silkspawners.plugin.capability.Capabilities;

class Server {
    static boolean isFolia() {
        return Capabilities.classExists("io.papermc.paper.threadedregions.RegionizedServer");
    }
}

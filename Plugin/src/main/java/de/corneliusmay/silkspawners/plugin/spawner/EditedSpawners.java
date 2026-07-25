package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.wiring.Loader;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;

@Wired
public class EditedSpawners implements Loader {

    private static final long TTL_MILLIS = 5000;

    private final Map<Location, Long> pending = new ConcurrentHashMap<>();

    @Override
    public boolean load() {
        return true;
    }

    public boolean beginEdit(Location location) {
        long now = System.currentTimeMillis();
        boolean[] acquired = {false};
        pending.compute(location, (loc, since) -> {
            if (since == null || now - since > TTL_MILLIS) {
                acquired[0] = true;
                return now;
            }
            return since;
        });
        return acquired[0];
    }

    public void endEdit(Location location) {
        pending.remove(location);
    }
}

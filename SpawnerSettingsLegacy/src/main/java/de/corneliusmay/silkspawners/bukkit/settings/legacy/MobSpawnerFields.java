package de.corneliusmay.silkspawners.bukkit.settings.legacy;

import de.corneliusmay.silkspawners.spi.spawner.SpawnerSettings;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.block.CreatureSpawner;

// Pre-1.12 servers expose the spawner settings only through the NMS MobSpawnerAbstract fields
public class MobSpawnerFields {

    private final Field spawner;
    private final Method getSpawner;
    private final Field minSpawnDelay;
    private final Field maxSpawnDelay;
    private final Field spawnCount;
    private final Field maxNearbyEntities;
    private final Field requiredPlayerRange;
    private final Field spawnRange;

    public MobSpawnerFields() {
        try {
            String craftPackage =
                    org.bukkit.Bukkit.getServer().getClass().getPackage().getName();
            String version = craftPackage.substring(craftPackage.lastIndexOf('.') + 1);
            Class<?> craftCreatureSpawner =
                    Class.forName("org.bukkit.craftbukkit." + version + ".block.CraftCreatureSpawner");
            Class<?> tileEntity = Class.forName("net.minecraft.server." + version + ".TileEntityMobSpawner");
            Class<?> mobSpawner = Class.forName("net.minecraft.server." + version + ".MobSpawnerAbstract");
            spawner = field(craftCreatureSpawner, "spawner");
            getSpawner = tileEntity.getMethod("getSpawner");
            minSpawnDelay = field(mobSpawner, "minSpawnDelay");
            maxSpawnDelay = field(mobSpawner, "maxSpawnDelay");
            spawnCount = field(mobSpawner, "spawnCount");
            maxNearbyEntities = field(mobSpawner, "maxNearbyEntities");
            requiredPlayerRange = field(mobSpawner, "requiredPlayerRange");
            spawnRange = field(mobSpawner, "spawnRange");
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Unsupported server version for spawner settings access", ex);
        }
    }

    public SpawnerSettings read(CreatureSpawner creatureSpawner) {
        try {
            Object nmsSpawner = nmsSpawner(creatureSpawner);
            return new SpawnerSettings(
                    minSpawnDelay.getInt(nmsSpawner),
                    maxSpawnDelay.getInt(nmsSpawner),
                    spawnCount.getInt(nmsSpawner),
                    maxNearbyEntities.getInt(nmsSpawner),
                    requiredPlayerRange.getInt(nmsSpawner),
                    spawnRange.getInt(nmsSpawner));
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void apply(CreatureSpawner creatureSpawner, SpawnerSettings settings) {
        try {
            Object nmsSpawner = nmsSpawner(creatureSpawner);
            minSpawnDelay.setInt(nmsSpawner, settings.minSpawnDelay());
            maxSpawnDelay.setInt(nmsSpawner, settings.maxSpawnDelay());
            spawnCount.setInt(nmsSpawner, settings.spawnCount());
            maxNearbyEntities.setInt(nmsSpawner, settings.maxNearbyEntities());
            requiredPlayerRange.setInt(nmsSpawner, settings.requiredPlayerRange());
            spawnRange.setInt(nmsSpawner, settings.spawnRange());
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    // Pre-1.12 block states wrap the live tile entity, so writes take effect without an update
    private Object nmsSpawner(CreatureSpawner creatureSpawner) throws ReflectiveOperationException {
        return getSpawner.invoke(spawner.get(creatureSpawner));
    }

    private static Field field(Class<?> owner, String name) throws ReflectiveOperationException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}

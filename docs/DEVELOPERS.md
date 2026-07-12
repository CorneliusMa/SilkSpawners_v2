# Developer documentation

For plugin developers who want to integrate with SilkSpawners.

SilkSpawners exposes a single artifact, published via [JitPack](https://jitpack.io). It contains the `SilkSpawnersAPI` service, spawner snapshots and all custom events. Use the API release tag (e.g. `api-0.1.0`) as the version.

Gradle:

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.CorneliusMa.SilkSpawners_v2:silkspawners-api:api-0.1.0")
}
```

Maven:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.CorneliusMa.SilkSpawners_v2</groupId>
        <artifactId>silkspawners-api</artifactId>
        <version>api-0.1.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Alternatively, every [release](https://github.com/CorneliusMa/SilkSpawners_v2/releases) ships the prebuilt `SilkSpawners_v2-api-<version>.jar` to add to your build manually.

Declare SilkSpawners in your `plugin.yml` so it loads first:

```yaml
depend: [SilkSpawners_v2] # or softdepend if the integration is optional
```

## The API service

SilkSpawners registers a `SilkSpawnersAPI` service with the Bukkit `ServicesManager` on enable. `SilkSpawnersApiProvider` is the entry point:

```java
import de.corneliusmay.silkspawners.api.SilkSpawnersAPI;
import de.corneliusmay.silkspawners.api.SilkSpawnersApiProvider;

SilkSpawnersAPI api = SilkSpawnersApiProvider.get(); // throws IllegalStateException if SilkSpawners is not enabled

Optional<SilkSpawnersAPI> api = SilkSpawnersApiProvider.find(); // empty instead of throwing
```

| Method | Description |
| --- | --- |
| `getSpawnerItem(EntityType)` | Build the SilkSpawners spawner item for an entity type (`null` entity type = empty spawner, returns `null` for non-spawnable types) |
| `getEntityType(ItemStack)` | Read the entity type back out of a spawner item (`null` if empty or not a SilkSpawners item) |
| `isSpawnerItem(ItemStack)` | Whether the item is a spawner item |
| `getSpawner(Block)` | Read a placed spawner block as a snapshot (`null` if the block is no spawner) |
| `setSpawnerType(Block, EntityType)` | Change the entity type of a placed spawner block, returns `false` if the block is no spawner or the type is not spawnable |
| `getSupportedEntityTypes()` | All entity types a spawner can be set to |
| `canSilkDrop(Player, EntityType)` | Whether the player would receive a spawner drop right now (break permission, silk touch tool and config rules) |

## Spawner snapshots

Events and the API describe spawners through the `SpawnerSnapshot` interface instead of internal classes:

| Method | Description |
| --- | --- |
| `getEntityType()` | The spawner's entity type, `null` for an empty spawner |
| `getItemStack()` | The spawner item representing this spawner |
| `isEmpty()` | Whether the spawner has no entity type |

## Events

All events live in `de.corneliusmay.silkspawners.api.events` and extend the common base `SpawnerEvent`:

| Method | Description |
| --- | --- |
| `getPlayer()` | The player who triggered the event |
| `getSpawner()` | The `SpawnerSnapshot` involved |
| `getLocation()` | The spawner's location |
| `isCancelled()` / `setCancelled(boolean)` | Standard `Cancellable` methods |

- **`SpawnerPlaceEvent`** - called when a player places a spawner. `setSpawner(EntityType)` replaces the spawner before it is placed.
- **`SpawnerBreakEvent`** - called when a player breaks a spawner and a drop will happen. `setSpawner(EntityType)` replaces the dropped spawner.
- **`SpawnerChangeEvent`** - called when a spawner's type is changed with a spawn egg or `/silkspawners set`. `getNewSpawner()` returns the incoming spawner, `setNewSpawner(EntityType)` overrides it, cancelling reverts to the previous type.
- **`SpawnerDropEvent`** - called before the drop chance is rolled for a broken spawner. `setDropChance(double)` overrides the configured chance (0-100), `setDrop(ItemStack)` replaces the dropped item, cancelling breaks the spawner without a drop.
- **`SpawnerExplodeEvent`** - called before a spawner explosion (TNT feature). `setPower(float)`, `setFire(boolean)` and `setBreakBlocks(boolean)` tune the explosion, cancelling prevents it.
- **`SpawnerGiveEvent`** - called when `/silkspawners give` hands out spawners. `getSender()` returns the command sender, `setAmount(int)` overrides the amount.

Example listener:

```java
import de.corneliusmay.silkspawners.api.events.SpawnerPlaceEvent;
import de.corneliusmay.silkspawners.api.events.SpawnerDropEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MyListener implements Listener {

    @EventHandler
    public void onSpawnerPlace(SpawnerPlaceEvent event) {
        // e.g. force every placed spawner to be a pig spawner
        event.setSpawner(EntityType.PIG);
    }

    @EventHandler
    public void onSpawnerDrop(SpawnerDropEvent event) {
        // e.g. guarantee the drop for VIPs
        if (event.getPlayer().hasPermission("myplugin.vip")) {
            event.setDropChance(100);
        }
    }
}
```

Register the listener with Bukkit as usual: `getServer().getPluginManager().registerEvents(new MyListener(), this);`

A runnable example plugin lives in the [`ApiExample`](../ApiExample) module - build it with `./gradlew :ApiExample:jar` and drop it into a test server next to SilkSpawners.

## Shop integrations

There is no SilkSpawners-side registration for shop integrations: `getSpawnerItem` and `getEntityType` are the integration surface. Wire them into your shop plugin from your own `onEnable` (with `depend: [SilkSpawners_v2]`), the same way the bundled ShopGUI+ integration does it internally.

Pull requests for new bundled hooks are welcome - see [Adding a plugin hook](../CONTRIBUTING.md#adding-a-plugin-hook) in the contributing guide.

## Folia

SilkSpawners supports [Folia](https://papermc.io/software/folia). API methods must be called from the thread owning the affected region (the main server thread on Bukkit/Spigot/Paper), and events fire on the thread owning the spawner's region. Block changes made through the API are dispatched via the platform scheduler, so they are safe on both platforms.

## Stability

The API artifact follows semantic versioning. Interfaces annotated with `@ApiStatus.NonExtendable` must not be implemented by consumers, and event constructors are `@ApiStatus.Internal` - events are meant to be listened to, not fired. Parameters and return values that may be `null` are annotated with `@Nullable`; everything else is non-null.

# Developer documentation

For plugin developers who want to integrate with SilkSpawners.

Declare SilkSpawners in your `plugin.yml` so it loads first:

```yaml
depend: [SilkSpawners_v2] # or softdepend if the integration is optional
```

## Events

SilkSpawners fires two custom Bukkit events that other plugins can listen to. Both are `Cancellable` - cancelling them prevents the spawner from being placed or broken.

- **`SpawnerPlaceEvent`** - called when a player places a spawner.
- **`SpawnerBreakEvent`** - called when a player breaks a spawner.

Both events expose the same API:

| Method | Description |
| --- | --- |
| `getPlayer()` | The player who placed / broke the spawner |
| `getSpawner()` | The `Spawner` involved |
| `getLocation()` | The spawner's location |
| `setSpawner(EntityType)` | Replace the spawner's entity type before the action completes |
| `isCancelled()` / `setCancelled(boolean)` | Standard `Cancellable` methods |

Example listener:

```java
import de.corneliusmay.silkspawners.plugin.events.SpawnerPlaceEvent;
import de.corneliusmay.silkspawners.plugin.events.SpawnerBreakEvent;
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
    public void onSpawnerBreak(SpawnerBreakEvent event) {
        // e.g. stop this player from breaking the spawner
        if (!event.getPlayer().hasPermission("myplugin.breakspawner")) {
            event.setCancelled(true);
        }
    }
}
```

Register the listener with Bukkit as usual: `getServer().getPluginManager().registerEvents(new MyListener(), this);`

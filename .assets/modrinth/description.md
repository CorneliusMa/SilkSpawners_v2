<p align="center">
    <a href="https://github.com/CorneliusMa/SilkSpawners_v2" target="_blank"><img alt="SilkSpawners - A lightweight plugin to make spawners mineable" src="https://raw.githubusercontent.com/CorneliusMa/SilkSpawners_v2/master/.assets/title.png">
    </a>
</p>

**If you use a pickaxe with the silk touch enchantment while breaking a spawner, you will receive the spawner you broke. When placing the spawner again, the spawner will spawn the mob it spawned when breaking it.**

Compatible with Paper, Spigot, Purpur, Bukkit and Folia servers.

## Commands
- `/silkspawners help [command]`
- `/silkspawners give <Player> <Type> [Amount]`
- `/silkspawners set <Type>`
- `/silkspawners explosion <enable/disable/setting> <Player>`
- `/silkspawners locale <setting/reload/update>`
- `/silkspawners config <reload>`
- `/silkspawners entities`
- `/silkspawners version`

## Permissions

### Commands
- **silkspawners.command.give** - Use this command to give spawners to players.
> In addition, set **silkspawners.command.give.*** to allow all entities or replace the star with an entity name.
- **silkspawners.command.set** - Use this command to change already placed spawners.
> In addition, set **silkspawners.command.set.*** to allow all entities or replace the star with an entity name.
- **silkspawners.command.explosion** - Use this command to temporarily enable or disable spawner explosions for a specific player.
- **silkspawners.command.locale** - Use this command to reload and update locale files.
- **silkspawners.command.config** - Use this command to reload the configuration.
- **silkspawners.command.entities** - Use this command to see the entities you can use in permissions and commands.
- **silkspawners.command.version** - Use this command to see if updates are available.

### Spawners
- **silkspawners.break.*** - Permission to set if players will receive destroyed spawners.
- **silkspawners.place.*** - Permission to set if players can place silk spawners.
- **silkspawners.change.*** - Permission to set if players can change spawners with eggs.
- **silkspawners.explosion** - If set, spawner explosion is enabled.

*If you want to allow only specific types of spawners you can replace the **\*** with an entity name. Use the entities command to see spawnable entities.*

## Configuration
**Default configuration:**

```yaml
messages:
  prefix: $8[$bSilkSpawners$8] # The plugin prefix used in all messages
  locale: en # The locale file to be used
spawner:
  dropChance: 100 # Probability that the spawner will drop when mined (0-100)
  destroyable: true  # If set to false, it won't be possible to destroy spawners without SilkTouch or without the permission
  pickaxeRequired: true # If set to false, spawners will always drop regardless of what the player is holding in his hand
  silktouchRequired: true # If set to false, spawners will drop even if the used pickaxe does not have SilkTouch
  silktouchLevel: 1 # The minimum SilkTouch level the pickaxe needs to mine spawners (useful for custom pickaxes with higher enchantment levels)
  item:
    name: $dSpawner # The name of the spawner item dropped
    prefix: $e # The text before the spawner name in the lore
    prefixOld: '' # If you change your prefix, set this value to your old prefix to keep existing spawners functional
    lore: [] # Set an array for this value to set a custom lore
  explosion:
    all: [] # Explosion tiers rolled whenever spawners are mined, with or without SilkTouch (see below)
    normal: [] # Explosion tiers rolled when spawners are mined without SilkTouch (see below)
    silktouch: [] # Explosion tiers rolled when spawners are mined with SilkTouch (see below)
  message:
    denyDestroy: true # If set to true, a message will be sent to the player if the spawner cannot be destroyed
    denyPlace: true # If set to true, a message will be sent to the player if the spawner cannot be placed
    denyChange: true # If set to true, a message will be sent to the player if the spawner cannot be changed
  permission:
    disableDestroy: false # If set to true, no permission is required to receive destroyed spawners
    disablePlace: false # If set to true, no permission is required to place spawners
    disableChange: false # If set to true, no permission is required to change spawners with eggs
update:
  configVersion: 3 # Do not change this value manually! It is automatically managed by the plugin
  check:
    enabled: true # If set to true, the plugin will check for updates
    interval: 24 # The interval in hours at which to check for updates
hooks:
  shopguiplus: true # If set to true, SilkSpawners will hook into ShopGUI+ if it is installed
```

*If you want to use a dollar sign in a value, you can escape it by putting a backslash in front of it.*

All messages (in the configuration and in locale files) can be formatted with either legacy color codes or [MiniMessage](https://docs.advntr.dev/minimessage/format.html) tags. Mixing both formats within the same message is **not** supported.

**Explosion tiers:**

Mined spawners can explode for players with the `silkspawners.explosion` permission: `explosion.normal` tiers apply when mining without SilkTouch, `explosion.silktouch` with it, and `explosion.all` to both. Each tier needs a `chance` in percent and an explosion `power` (TNT is 4.0). The example below gives a 50% chance of a small explosion, 30% of a large one and 10% of a massive one that also ignites fires:

```yaml
spawner:
  explosion:
    normal:
    - chance: 50
      power: 2.0
    - chance: 30
      power: 4.0
    - chance: 10
      power: 8.0
      setFire: true
```

All tier options are described in the [README](https://github.com/CorneliusMa/SilkSpawners_v2#configuration). Changes to the tiers take effect after `/silkspawners config reload` or a server restart.

## Custom messages
![Crowdin Localization](https://badges.crowdin.net/silkspawners/localized.svg)

> **To protect your locale files from unwanted overwriting, you must manually update the locale files with the /silkspawners locale command after an update.**

If you want to create your own locale file, you should create a new file to prevent your changes from being overwritten when you update the locale files.
Locale files must be named accordingly to the messages_myfile.properties naming schema and can be used by setting myfile as locale.

**If you have created your own translation, it would be awesome if you could submit it at our [translation program](https://crowdin.com/project/silkspawners), so other people can use it too.**

<details>
<summary>Default messages file</summary>

```properties
SPAWNER_DESTROY_DENIED = $7You can't break this spawner.
SPAWNER_PLACE_DENIED = $7You can't place this spawner.
SPAWNER_CHANGE_DENIED = $7You are not allowed to change this spawner.

COMMAND_NOT_FOUND = $eCommand not found.$7\nAvailable commands\: \n{0}
COMMAND_INSUFFICIENT_PERMISSIONS = $cYou do not have the permission to execute this command.

COMMAND_SILKSPAWNERS_HELP_USAGE = $ePlease use /silkspawners help [command]
COMMAND_SILKSPAWNERS_HELP_MESSAGE = $7You can execute the following commands\: \n{0}\nUse /silkspawners help <command> for detailed info
COMMAND_SILKSPAWNERS_HELP_MESSAGE_HELP = $7Use this command to show information about the commands you can use. \nUsage\: /silkspawners help [command]
COMMAND_SILKSPAWNERS_HELP_COMMAND_NOT_FOUND = $7The command $c/silkspawners {0}$7 does not exist.
COMMAND_SILKSPAWNERS_HELP_MESSAGE_EXPLOSION = $7Use this command to temporarily enable or disable spawner explosions for a specific player. \nUsage\: /silkspawners explosion <enable/disable/setting> <Player>
COMMAND_SILKSPAWNERS_HELP_MESSAGE_GIVE = $7Use this command to give spawners to players. \nUsage\: /silkspawners give <Player> <Mob> [Amount]
COMMAND_SILKSPAWNERS_HELP_MESSAGE_SET = $7Use this command to change already placed spawners. \nUsage\: /silkspawners set <Mob>
COMMAND_SILKSPAWNERS_HELP_MESSAGE_ENTITIES = $7Use this command to see entities that can be used in commands and permissions. \nUsage\: /silkspawners entities
COMMAND_SILKSPAWNERS_HELP_MESSAGE_VERSION = $7Use this command to see, if updates are available. \nUsage\: /silkspawners version
COMMAND_SILKSPAWNERS_HELP_MESSAGE_LOCALE = $7Use this command to see the currently used locale, to reload the locale files and to update them from the .jar file. Updating may be necessary if new messages have been added in an update. \n$eWarning\! All custom changes will be lost if not previously saved\!$7\nUsage\: /silkspawners locale [setting/reload/update]

COMMAND_SILKSPAWNERS_GIVE_USAGE = $ePlease use /silkspawners give <Player> <Mob> [Amount]
COMMAND_SILKSPAWNERS_GIVE_PLAYER_NOT_FOUND = $7The player $c{0}$7 is not online.
COMMAND_SILKSPAWNERS_GIVE_ENTITY_NOT_FOUND = $7The entity $c{0}$7 is no valid spawner mob.
COMMAND_SILKSPAWNERS_GIVE_INSUFFICIENT_ENTITY_PERMISSION = $7You do not have the permission to give $c{0}$7 spawners to players.
COMMAND_SILKSPAWNERS_GIVE_INVALID_AMOUNT = $7The amount $c{0}$7 is no number.
COMMAND_SILKSPAWNERS_GIVE_TOO_SMALL_AMOUNT = $7The amount must be at least 1.
COMMAND_SILKSPAWNERS_GIVE_SUCCESS = $7Gave {0} {1}$7 spawner{2} to {3}.
COMMAND_SILKSPAWNERS_GIVE_SUCCESS_TARGET = $7You received {0} {1}$7 spawner{2} by {3}.
COMMAND_SILKSPAWNERS_GIVE_SUCCESS_SELF = $7Gave {0} {1}$7 spawner{2} to yourself.

COMMAND_SILKSPAWNERS_SET_USAGE = $ePlease use /silkspawners set <Mob>
COMMAND_SILKSPAWNERS_SET_PLAYERS_ONLY = $7This command can only be used by players.
COMMAND_SILKSPAWNERS_SET_ENTITY_NOT_FOUND = $7The entity $c{0}$7 is no valid spawner mob.
COMMAND_SILKSPAWNERS_SET_INSUFFICIENT_ENTITY_PERMISSION = $7You do not have the permission to set spawners to $c{0}$7.
COMMAND_SILKSPAWNERS_SET_INVALID_TARGET = $7You must look at a spawner to change.
COMMAND_SILKSPAWNERS_SET_SUCCESS = $7Successfully set spawner to {0}$7.

COMMAND_SILKSPAWNERS_EXPLOSION_USAGE = $ePlease use /silkspawners explosion <enable/disable/setting> <Player>
COMMAND_SILKSPAWNERS_EXPLOSION_PLAYER_NOT_FOUND = $7The Player $c{0}$7 is not online.
COMMAND_SILKSPAWNERS_EXPLOSION_ENABLED = $cEnabled $7spawner explosion for {0}.
COMMAND_SILKSPAWNERS_EXPLOSION_DISABLED = $aDisabled $7spawner explosion for {0}.
COMMAND_SILKSPAWNERS_EXPLOSION_SETTING_ENABLED = $7Explosions for {0} are currently $cenabled$7.
COMMAND_SILKSPAWNERS_EXPLOSION_SETTING_DISABLED = $7Explosions for {0} are currently $adisabled$7.

COMMAND_SILKSPAWNERS_ENTITIES_USAGE = $ePlease use /silkspawners entities
COMMAND_SILKSPAWNERS_ENTITIES_MESSAGE = $7You can use the following entities in commands and permissions\: {0}

COMMAND_SILKSPAWNERS_VERSION_USAGE = $ePlease use /silkspawners version
COMMAND_SILKSPAWNERS_VERSION_INFO = $aYou are up to date.$7\nThe currently installed version is v{0}
COMMAND_SILKSPAWNERS_VERSION_UPDATE_AVAILABLE = $eAn update is available\!$7\nThe currently installed version is v{0}\nThe latest version is v{1}
COMMAND_SILKSPAWNERS_VERSION_ERROR = $cUpdate checking is disabled. $7\nThe currently installed version is v{0}

COMMAND_SILKSPAWNERS_LOCALE_USAGE = $ePlease use /silkspawners locale [setting/reload/update]
COMMAND_SILKSPAWNERS_LOCALE_SETTING = $7The currently used locale is {0}. Available locales are\: {1}
COMMAND_SILKSPAWNERS_LOCALE_RELOAD_SUCCESSFUL = $7The locale was reloaded successfully.
COMMAND_SILKSPAWNERS_LOCALE_RELOAD_ERROR = $cAn error occurred reloading the locale.
COMMAND_SILKSPAWNERS_LOCALE_UPDATE_WARNING = $eWarning\!$7 Updating the locale files will $coverwrite all changes$7.\n If you want to proceed, run /silkspawners locale update confirm.
COMMAND_SILKSPAWNERS_LOCALE_UPDATE_SUCCESSFUL = $7The locale files were updated and reloaded successfully.
COMMAND_SILKSPAWNERS_LOCALE_UPDATE_ERROR = $cAn error occurred.$7 Please contact the developer if this problem persists.

COMMAND_SILKSPAWNERS_CONFIG_USAGE = $ePlease use /silkspawners config reload
COMMAND_SILKSPAWNERS_CONFIG_RELOAD_SUCCESSFUL = $7The config was reloaded $asuccessfully$7.
COMMAND_SILKSPAWNERS_CONFIG_RELOAD_ERROR = $7An $cerror$7 occurred reloading the configuration. Please check the server logs.
```
</details>

## Integrations

### ShopGUI+
If [ShopGUI+](https://www.spigotmc.org/resources/shopgui-1-8-1-21.6515/) is installed, SilkSpawners automatically registers itself as its spawner provider, so spawners bought and sold in shops are SilkSpawners items. The hook can be disabled by setting `hooks.shopguiplus` to `false` in the configuration.

## For developers

SilkSpawners fires custom Bukkit events (`SpawnerPlaceEvent`, `SpawnerBreakEvent`) that other plugins can listen to. See [Developer documentation](https://github.com/CorneliusMa/SilkSpawners_v2/blob/master/docs/DEVELOPERS.md) for details.

## Tutorial

<iframe width="560" height="315" src="https://www.youtube-nocookie.com/embed/LbK3AEXt_5o" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>

Thanks to [KasaiSora](https://www.youtube.com/@KasaiSora) for creating this great tutorial!

## Statistics
<img src="https://bstats.org/signatures/bukkit/Silk%20Spawners.svg" alt="Statistics">
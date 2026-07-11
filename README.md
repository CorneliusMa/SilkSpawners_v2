<p align="center" style="background-color: white; color: black; border-radius: 10px; padding-top: 10px; padding-bottom: 5px">
    <a href="https://github.com/CorneliusMa/SilkSpawners_v2"><img alt="SilkSpawners - A lightweight plugin to make spawners mineable" src=".assests/img/title.png"></a>
    <br>
    <a href="https://github.com/CorneliusMa/SilkSpawners_v2/releases/latest" target="_blank"><img src="https://img.shields.io/github/v/release/CorneliusMa/SilkSpawners_v2?logo=github" alt="Latest Release"></a>
    <a href="https://github.com/CorneliusMa/SilkSpawners_v2/actions/workflows/release.yml" target="_blank"><img src="https://github.com/CorneliusMa/SilkSpawners_v2/actions/workflows/release.yml/badge.svg" alt="Release Status"></a>
    <a href="https://github.com/CorneliusMa/SilkSpawners_v2/actions/workflows/build.yml" target="_blank"><img src="https://github.com/CorneliusMa/SilkSpawners_v2/actions/workflows/build.yml/badge.svg" alt="Build Status"></a>
    <img src="https://img.shields.io/badge/Tested%20versions-%20%3C%3D%201.21.11-green.svg" alt="Tested Versions">
    <a href="https://www.codefactor.io/repository/github/corneliusma/silkspawners_v2/overview/master" target="_blank"><img src="https://www.codefactor.io/repository/github/corneliusma/silkspawners_v2/badge/master" alt="CodeFactor"></a>
    <a href="https://crowdin.com/project/silkspawners" target="_blank"><img src="https://badges.crowdin.net/silkspawners/localized.svg" alt="Crowdin Localization"></a>
    <br>
    This plugin makes spawners minable using SilkTouch tools.
    <br>
    <br>
    <a href="https://www.spigotmc.org/resources/silkspawners.60063/"><img alt="Visit on Spigot" src=".assests/img/spigot.png"></a>
    <a href="https://hangar.papermc.io/SilkSpawners/SilkSpawners"><img alt="Visit on Hangar" src=".assests/img/hangar.png"></a>
</p>

## Contributing

Contributions and [translations](https://crowdin.com/project/silkspawners) are welcome. See [Contributing](CONTRIBUTING.md) for more.

## Building

Requirements: JDK 17 (any distribution)

Simply download the source and build it using Gradle.

```bash
./gradlew :Plugin:assemble --no-daemon
```

Your build of SilkSpawners will be available at `build/libs/SilkSpawners_v2.jar`.

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
```

*If you want to use a dollar sign in a value, you can escape it by putting a backslash in front of it.*

**Explosion tiers:**

`explosion.normal` and `explosion.silktouch` each take a list of tiers. When a spawner is broken by a player with the `silkspawners.explosion` permission, the tiers are checked strongest first and the first tier whose `chance` roll passes explodes — so at most one explosion fires per break. With the example below, 10% of breaks cause a massive explosion, 36% a large one, 37.8% a small one and 16.2% none at all:

```yaml
spawner:
  explosion:
    normal:
    - chance: 70 # Probability of this tier in percent (decimals allowed)
      power: 2.0 # Explosion strength (TNT is 4.0)
    - chance: 40
      power: 4.0
    - chance: 10
      power: 8.0
      setFire: true # Optional: the explosion ignites fires (default false)
      breakBlocks: false # Optional: the explosion damages surrounding blocks (default true)
```

Numeric values from older configs (e.g. `normal: 3`) are migrated automatically to a single always-exploding tier of that power. Changes to the tiers take effect after `/silkspawners config reload` or a server restart.

## Custom messages
> **To protect your locale files from unwanted overwriting, you must manually update the locale files with the /silkspawners locale command after an update.**

If you want to create your own locale file, you should create a new file to prevent your changes from being overwritten when you update the locale files.
Locale files must be named accordingly to the messages_myfile.properties naming schema and can be used by setting myfile as locale.

*If you have created your own translation, it would be awesome if you could submit it at our [translation program](https://crowdin.com/project/silkspawners), so other people can use it too.*

## Statistics
![Statistics](https://bstats.org/signatures/bukkit/Silk%20Spawners.svg)
Statistics from bStats

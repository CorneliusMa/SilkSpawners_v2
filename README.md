<p align="center" style="background-color: white; color: black; border-radius: 10px; padding-top: 10px; padding-bottom: 5px">
    <a href="https://github.com/CorneliusMa/SilkSpawners_v2"><img alt="SilkSpawners - A lightweight plugin to make spawners mineable" src=".assets/title.png"></a>
    <br>
    <a href="https://github.com/CorneliusMa/SilkSpawners_v2/releases/latest" target="_blank"><img src="https://img.shields.io/github/v/release/CorneliusMa/SilkSpawners_v2?logo=github" alt="Latest Release"></a>
    <a href="https://github.com/CorneliusMa/SilkSpawners_v2/actions/workflows/release.yml" target="_blank"><img src="https://github.com/CorneliusMa/SilkSpawners_v2/actions/workflows/release.yml/badge.svg" alt="Release Status"></a>
    <a href="https://github.com/CorneliusMa/SilkSpawners_v2/actions/workflows/build.yml" target="_blank"><img src="https://github.com/CorneliusMa/SilkSpawners_v2/actions/workflows/build.yml/badge.svg" alt="Build Status"></a>
    <img src="https://img.shields.io/badge/Tested%20versions-1.8%20--%2026.2-green.svg" alt="Tested Versions">
    <a href="https://www.codefactor.io/repository/github/corneliusma/silkspawners_v2/overview/master" target="_blank"><img src="https://www.codefactor.io/repository/github/corneliusma/silkspawners_v2/badge/master" alt="CodeFactor"></a>
    <a href="https://crowdin.com/project/silkspawners" target="_blank"><img src="https://badges.crowdin.net/silkspawners/localized.svg" alt="Crowdin Localization"></a>
    <br>
    This plugin makes spawners minable using SilkTouch tools.
    <br>
    <sub>Compatible with Paper, Spigot, Purpur, Bukkit and Folia servers.</sub>
    <br>
    <br>
    <a href="https://modrinth.com/plugin/silkspawners"><img alt="Visit on Modrinth" src=".assets/modrinth/modrinth.png"></a>
    <a href="https://hangar.papermc.io/SilkSpawners/SilkSpawners"><img alt="Visit on Hangar" src=".assets/hangar/hangar.png"></a>
    <a href="https://www.spigotmc.org/resources/silkspawners.60063/"><img alt="Visit on Spigot" src=".assets/spigot/spigot.png"></a>
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

The plugin is wired by [weftkit](https://weftkit.org), the compile-time lifecycle framework for Bukkit plugins that grew out of this project.

## QuickStart

Want to use SilkSpawners without configuring permissions or anything else? Install the plugin and run this command as an admin or from the console, then confirm as prompted:

```
/silkspawners setup
```

## Commands
- `/silkspawners help [command]`
- `/silkspawners give <player> [trial] <entity> [amount]`
- `/silkspawners set <entity>`
- `/silkspawners explosion <enable/disable/setting> <player>`
- `/silkspawners locale <setting/reload/restore>`
- `/silkspawners config <reload/get/set> [setting] [value]`
- `/silkspawners config explosion <list/add/remove> [all/normal/silktouch] [values]`
- `/silkspawners setup [confirm/revert]`
- `/silkspawners entities`
- `/silkspawners version`
- `/silkspawners dump`

## Permissions

### Commands
- **silkspawners.command.give** - Use this command to give spawners to players.
> In addition, set **silkspawners.command.give.*** to allow all entities or replace the star with an entity name. Trial spawners use the **silkspawners.command.give.trial.*** branch.
- **silkspawners.command.set** - Use this command to change already placed spawners.
> In addition, set **silkspawners.command.set.*** to allow all entities or replace the star with an entity name. Trial spawners use the **silkspawners.command.set.trial.*** branch.
- **silkspawners.command.explosion** - Use this command to temporarily enable or disable spawner explosions for a specific player.
- **silkspawners.command.locale** - Use this command to reload and restore locale files.
- **silkspawners.command.config** - Use this command to view and reload the configuration, including the explosion tiers.
> In addition, set **silkspawners.command.config.set** to allow changing settings and explosion tiers.
- **silkspawners.command.setup** - Use this command to disable or restore the permissions needed to use spawners.
- **silkspawners.command.entities** - Use this command to see the entities you can use in permissions and commands.
- **silkspawners.command.version** - Use this command to see if updates are available.
- **silkspawners.command.dump** - Use this command to create a diagnostic report for support requests.

### Spawners
- **silkspawners.break.*** - Permission to set if players will receive destroyed spawners.
- **silkspawners.place.*** - Permission to set if players can place silk spawners.
- **silkspawners.change.*** - Permission to set if players can change spawners with eggs.
- **silkspawners.explosion** - If set, spawner explosion is enabled.

### Trial spawners
Trial spawner support is disabled by default - the [trial spawners page](docs/TRIAL_SPAWNERS.md) explains its impact on game balance and how the plugin handles it.
- **silkspawners.trial.break.*** - Permission to set if players will receive destroyed trial spawners.
- **silkspawners.trial.place.*** - Permission to set if players can place trial spawners.
- **silkspawners.trial.change.*** - Permission to set if players can change trial spawners with eggs.

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
  silktouchLevel: 1 # The minimum SilkTouch level the pickaxe needs to mine spawners, at least 1 (useful for custom pickaxes with higher enchantment levels)
  item:
    name: $d{entity} Spawner # The name of the spawner item dropped, {entity} is replaced with the mob name
    color: $e # The color of the mob name in chat messages
    prefixOld: [] # Lore prefixes used by spawner items from older plugin versions, kept so those items stay recognized
    lore: ['$7Spawns $e{entity}'] # The lore of the spawner item, {entity} is replaced with the mob name
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
trialspawner:
  enabled: false # If set to true, trial spawners can be mined with SilkTouch (Paper 1.21.4 or newer)
  carryCooldown: true # If set to true, a trial spawner that was mined while cooling down resumes its remaining cooldown after being placed
  dropChance: 100 # Probability that the trial spawner will drop when mined (0-100)
  destroyable: true # If set to false, it won't be possible to destroy trial spawners without SilkTouch or without the permission
  pickaxeRequired: true # If set to false, trial spawners will always drop regardless of what the player is holding in his hand
  silktouchRequired: true # If set to false, trial spawners will drop even if the used pickaxe does not have SilkTouch
  silktouchLevel: 1 # The minimum SilkTouch level the pickaxe needs to mine trial spawners
  breakSpeedMultiplier: 1.0 # Multiplies how fast trial spawners break for players who would receive the drop (vanilla trial spawners take about ten times as long as regular spawners)
  item:
    name: $b{entity} Trial Spawner # The name of the trial spawner item dropped, {entity} is replaced with the mob name
    ominousName: $5Ominous {entity} Trial Spawner # The name used when the trial spawner was ominous
    lore: ['$7Spawns $e{entity}'] # The lore of the trial spawner item, {entity} is replaced with the mob name
    cooldownLore: $eReward cooldown pending # Lore line added when the trial spawner carries a remaining cooldown (see carryCooldown), empty to disable
  message:
    denyDestroy: true # If set to true, a message will be sent to the player if the trial spawner cannot be destroyed
    denyPlace: true # If set to true, a message will be sent to the player if the trial spawner cannot be placed
    denyChange: true # If set to true, a message will be sent to the player if the trial spawner cannot be changed
  permission:
    disableDestroy: false # If set to true, no permission is required to receive destroyed trial spawners
    disablePlace: false # If set to true, no permission is required to place trial spawners
    disableChange: false # If set to true, no permission is required to change trial spawners with eggs
update:
  configVersion: 4 # Do not change this value manually! It is automatically managed by the plugin
  check:
    enabled: true # If set to true, the plugin will check for updates
    interval: 24 # The interval in hours at which to check for updates, at least 1
hooks:
  shopguiplus: true # If set to true, SilkSpawners will hook into ShopGUI+ if it is installed
```

*If you want to use a dollar sign in a value, you can escape it by putting a backslash in front of it.*

All messages (in the configuration and in locale files) can be formatted with either legacy color codes or [MiniMessage](https://docs.advntr.dev/minimessage/format.html) tags. Mixing both formats within the same message is **not** supported.

**Changing settings in game:**

- `/silkspawners config get <setting>` shows the current value and what the setting does
- `/silkspawners config set <setting> <value>` changes it in the `config.yml` file

Settings and values are tab completed. Changing settings requires the `silkspawners.command.config.set` permission. Changes take effect immediately, except for `hooks.shopguiplus`, which needs a server restart. Lists can only be changed in the file, and explosion tiers are managed with `/silkspawners config explosion` (see below).

**Explosion tiers:**

`explosion.normal` and `explosion.silktouch` each take a list of tiers, and `explosion.all` tiers apply on top of both. On each break (by a player with `silkspawners.explosion`), one roll picks at most one tier: each tier's `chance` is its percentage share and must have a `power`. Shares under 100% leave the remainder as no explosion (`power: 0` is an explicit no-explosion share); shares over 100% are scaled down proportionally. A `silktouch` explosion fires before the item drops, so it never destroys the drop. The example below gives 50% a small explosion, 30% a large one and 10% a massive one that ignites fires:

```yaml
spawner:
  explosion:
    normal:
    - chance: 50 # This tier's share of a single roll, in percent (decimals allowed, default 100)
      power: 2.0 # Required: the explosion strength (TNT is 4.0); 0 means no explosion
    - chance: 30
      power: 4.0
    - chance: 10
      power: 8.0
      setFire: true # Optional: the explosion ignites fires (default false)
      breakBlocks: true # Optional: the explosion damages surrounding blocks (default true)
```

**Changing tiers in game:**

- `/silkspawners config explosion list [all/normal/silktouch]` shows the configured tiers
- `/silkspawners config explosion add <all/normal/silktouch> <power> [chance] [setFire] [breakBlocks]` adds a tier
- `/silkspawners config explosion remove <all/normal/silktouch> <tier>` removes a tier by its number in the list

Adding and removing tiers requires the `silkspawners.command.config.set` permission. Changes made with the command take effect immediately. Changes made in the file take effect after `/silkspawners config reload` or a server restart.

## Custom messages
> **New and reworded messages are applied to your locale files automatically on startup. Messages you edited yourself are never touched, so a customized message keeps its wording across updates.**

Use `/silkspawners locale restore confirm` only to discard your own edits and return every message to the bundled default.
If you want to create your own locale file, you should create a new file to prevent your changes from being overwritten when you restore the locale files.
Locale files must be named accordingly to the messages_myfile.properties naming schema and can be used by setting myfile as locale.

*If you have created your own translation, it would be awesome if you could submit it at our [translation program](https://crowdin.com/project/silkspawners), so other people can use it too.*

## FAQ

Common questions and problems are answered in the [FAQ](docs/FAQ.md).

## Support

If the FAQ does not cover your problem, [open an issue](https://github.com/CorneliusMa/SilkSpawners_v2/issues). Running `/silkspawners dump` creates a diagnostic report - plugin, server and Java versions, the installed plugins and your configuration - on [pastes.dev](https://pastes.dev) and replies with a link to attach to the issue. If it cannot be shared, the report is written to the plugin folder instead.

## Integrations

### EconomyShopGUI
[EconomyShopGUI](https://www.spigotmc.org/resources/economyshopgui.69927/) supports SilkSpawners out of the box, so spawners bought and sold in its shops are SilkSpawners items. Its `spawner-provider` option detects SilkSpawners automatically when set to `AUTO` (the default), or can be pinned with `spawner-provider: SILKSPAWNERSV2`.

### ShopGUI+
If [ShopGUI+](https://www.spigotmc.org/resources/shopgui-1-8-1-21.6515/) is installed, SilkSpawners automatically registers itself as its spawner provider, so spawners bought and sold in shops are SilkSpawners items. The hook can be disabled by setting `hooks.shopguiplus` to `false` in the configuration.

## For developers

SilkSpawners provides a developer API with a `SilkSpawnersAPI` service and custom Bukkit events. See the [Developer documentation](docs/DEVELOPERS.md) for details.

## Tutorial

[![SilkSpawners Tutorial](https://img.youtube.com/vi/LbK3AEXt_5o/0.jpg)](https://www.youtube.com/watch?v=LbK3AEXt_5o)

Thanks to [KasaiSora](https://www.youtube.com/@KasaiSora) for creating this great tutorial!

## Statistics
![Statistics](https://bstats.org/signatures/bukkit/Silk%20Spawners.svg)
Statistics from bStats

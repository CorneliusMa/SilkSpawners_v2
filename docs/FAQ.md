# Frequently asked questions

Click a question to expand its answer. If yours is missing, feel free to [open an issue](https://github.com/CorneliusMa/SilkSpawners_v2/issues).

## General

<details>
<summary><b>How do I install the plugin?</b></summary>

Download the jar from the [latest release](https://github.com/CorneliusMa/SilkSpawners_v2/releases/latest) (also available on Modrinth, Hangar and Spigot), drop it into your server's `plugins` folder and restart. There is nothing else to set up - the config is created on first start.

</details>

<details>
<summary><b>Which server software and Minecraft versions are supported?</b></summary>

Paper, Spigot, Purpur, Bukkit and Folia, tested with Minecraft 1.8 through 26.2.

</details>

<details>
<summary><b>Can I use this plugin without a permission system?</b></summary>

Yes. Run `/silkspawners setup`, then confirm with `/silkspawners setup confirm`, to let everyone mine, place and change spawners without permission checks. `/silkspawners setup revert` undoes it. The command just sets `spawner.permission.disableDestroy`, `disablePlace` and `disableChange` to `true`, which you can also edit in the config. Admin commands stay available to operators. Spawner explosions can be enabled per player with `/silkspawners explosion enable <player>` even without a permission system, but only until the player logs out. For permanent per-player or per-mob rules you need a permissions plugin such as LuckPerms.

</details>

<details>
<summary><b>Where is the configuration and how do I apply changes?</b></summary>

The config is created at `plugins/SilkSpawners_v2/config.yml` on first start. After editing it, run `/silkspawners config reload` or restart the server.

</details>

<details>
<summary><b>Is it safe to update the plugin?</b></summary>

Yes. Replace the jar and restart - the configuration is migrated automatically and your settings are kept. Locale files are merged automatically as well, keeping every message you customized; see the question about missing messages below.

</details>

## Mining and placing spawners

<details>
<summary><b>Players without OP don't receive mined spawners</b></summary>

All `silkspawners.*` permissions are granted to server operators only by default. Either grant `silkspawners.break.*` (or `silkspawners.break.<entity>` for specific mobs) with your permissions plugin, or set `spawner.permission.disableDestroy: true` in the config to drop the permission check entirely. The same pattern applies to placing spawners (`silkspawners.place.*` / `disablePlace`) and changing them with spawn eggs (`silkspawners.change.*` / `disableChange`).

</details>

<details>
<summary><b>A spawner was mined with the correct permission, but nothing dropped</b></summary>

Check the following config values:

- `spawner.pickaxeRequired` and `spawner.silktouchRequired` - by default the player must mine with a pickaxe that has SilkTouch.
- `spawner.silktouchLevel` - if raised above 1, regular SilkTouch pickaxes no longer qualify.
- `spawner.dropChance` - below 100, spawners only drop that percentage of the time.

Spawners also never drop in creative mode.

</details>

<details>
<summary><b>How do I make spawners unbreakable for players without SilkTouch or permission?</b></summary>

Set `spawner.destroyable: false`. Spawners can then only be broken by players who actually receive the drop; for everyone else the break is blocked with a message (configurable via `spawner.message.denyDestroy`). Players in creative mode bypass this and break spawners without a drop.

</details>

<details>
<summary><b>A placed spawner spawns pigs / nothing instead of the mob on the item</b></summary>

Only spawner items created by SilkSpawners - mined spawners or ones from `/silkspawners give` - carry the mob information. Spawners from the creative inventory, vanilla `/give` or other plugins are placed with the server default.

</details>

<details>
<summary><b>Do spawner items from older plugin versions keep working after updating?</b></summary>

Yes, automatically. The mob is stored invisibly in the item data, so the name and lore are purely cosmetic - items created before this was added are still identified by their lore prefix, and the update carries your previous `spawner.item.prefix` value over to the `spawner.item.prefixOld` list on its own.

</details>

<details>
<summary><b>How do I give players spawners of a specific mob?</b></summary>

Use `/silkspawners give <player> <entity> [amount]`, for example `/silkspawners give Steve zombie 3`. Run `/silkspawners entities` to list all valid mob names.

</details>

<details>
<summary><b>Can I change what an already placed spawner spawns?</b></summary>

Yes. Look at the spawner and run `/silkspawners set <entity>`, or click the spawner with a spawn egg.

</details>

<details>
<summary><b>How do I stop players from changing spawners with spawn eggs?</b></summary>

That is the default: changing requires `silkspawners.change.<entity>` or `silkspawners.change.*`, which only operators have. If it works for everyone, either the players have the permission or `spawner.permission.disableChange` is set to `true`.

</details>

## Trial spawners

The [trial spawners page](TRIAL_SPAWNERS.md) covers the feature, its impact on game balance and the safeguards in one place.

<details>
<summary><b>Why is trial spawner support disabled by default?</b></summary>

Trial spawners are deliberately neither mineable nor obtainable in vanilla survival, and their reward renews on a cooldown. Making them portable is a balance decision, so `trialspawner.enabled` is `false` until you turn it on.

</details>

<details>
<summary><b>Which Minecraft versions support it?</b></summary>

Paper 1.21.4 and newer, including Paper forks such as Purpur and Folia. The reward cooldown of a trial spawner can only be read and restored through the Paper API, which gained that ability in 1.21.4 - Spigot does not expose it at all. On other servers the feature disables itself with a log message.

</details>

<details>
<summary><b>Can players farm rewards by breaking and replacing a trial spawner?</b></summary>

Not with `trialspawner.carryCooldown` enabled, which is the default. Placing a trial spawner creates a fresh block, so its cooldown would otherwise start over and the 30 minute wait between rewards could be skipped. Instead, the exact remaining cooldown travels on the item and the spawner is placed already cooling down for that long. Mob waves themselves are not restricted - that is the same as relocating a regular spawner.

</details>

<details>
<summary><b>What is lost when a trial spawner is mined?</b></summary>

The entity type, the ominous flag, both configurations including their loot tables, the cooldown length, the exact remaining cooldown and the spawn ranges are preserved. The display entity and any custom list of potential spawns are not readable and writable through the API and fall back to the vanilla defaults.

</details>

<details>
<summary><b>Do spawn eggs work on trial spawners?</b></summary>

Yes, with `silkspawners.trial.change.<entity>` or `silkspawners.trial.change.*`. Vanilla does not configure trial spawners from eggs, so SilkSpawners cancels the interaction and applies the change itself, to both the normal and the ominous configuration. The egg is consumed outside of creative mode.

</details>

## Explosions

<details>
<summary><b>Why do configured explosions never trigger?</b></summary>

Three things must be true:

1. At least one tier is configured under `spawner.explosion.normal`, `.silktouch` or `.all` - all lists are empty by default. Tiers can be added in game with `/silkspawners config explosion add <all/normal/silktouch> <power> [chance]`.
2. The mining player has the `silkspawners.explosion` permission. Explosions can also be toggled per player with `/silkspawners explosion <enable/disable> <player>`.
3. Changes made in the file were applied with `/silkspawners config reload` or a server restart. Changes made with `/silkspawners config explosion` apply immediately.

</details>

<details>
<summary><b>Can an explosion destroy the dropped spawner item?</b></summary>

No. Explosions detonate before the spawner item drops, so drops are never destroyed.

</details>

## Messages

<details>
<summary><b>My color codes don't work</b></summary>

The plugin uses `$` instead of `§` or `&` for legacy color codes (`$7`, `$c`, ...). Alternatively use [MiniMessage](https://docs.advntr.dev/minimessage/format.html) tags - but never mix both formats within the same message. A literal dollar sign can be escaped with a backslash.

</details>

<details>
<summary><b>Messages are missing or outdated after a plugin update</b></summary>

New and reworded messages are merged into your locale files automatically on startup. Messages you edited yourself are recognised as customizations and left untouched, so nothing you wrote is overwritten and no command is needed.

If a message you customized is stale on purpose and you want the bundled wording back, run `/silkspawners locale restore confirm` - this discards **all** custom changes. To keep edits permanently isolated from the bundled files, put them in a separate `messages_<name>.properties` file and select it with `messages.locale`.

</details>

## Integrations and developers

<details>
<summary><b>Are spawners sold via ShopGUI+ compatible?</b></summary>

Yes. If ShopGUI+ is installed, spawners bought and sold in its shops are automatically regular SilkSpawners items. Set `hooks.shopguiplus: false` to disable this.

</details>

<details>
<summary><b>Can my plugin react to spawner places and breaks?</b></summary>

Yes, SilkSpawners provides a developer API with a service interface and events for spawner places, breaks, drops, changes, explosions and gives. See the [Developer documentation](DEVELOPERS.md).

</details>

## Support

<details>
<summary><b>How do I report a problem?</b></summary>

[Open an issue](https://github.com/CorneliusMa/SilkSpawners_v2/issues) and attach a diagnostic dump. Running `/silkspawners dump` (permission `silkspawners.command.dump`, operators by default) creates the report and replies with a link to it.

</details>

<details>
<summary><b>What does a dump contain and where does it go?</b></summary>

The plugin version and update status, the server brand and version, the version and platform implementations in use, the Java and operating system versions, the locale in use, the active hooks, your configuration and the list of installed plugins with their versions. No player data, IP addresses or world data are included.

The report is created on [pastes.dev](https://pastes.dev) and the command replies with its link. If that is not possible - for example on a server without internet access - the report is saved as `plugins/SilkSpawners_v2/dump-<timestamp>.json` instead, and the command replies with that path so you can attach the file by hand.

</details>

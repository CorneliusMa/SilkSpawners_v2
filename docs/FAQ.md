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

Yes. Set `spawner.permission.disableDestroy`, `disablePlace` and `disableChange` to `true` and mining, placing and changing spawners works for everyone without any permission checks. The admin commands stay available to server operators. Spawner explosions can be switched on per player with `/silkspawners explosion enable <Player>` even without a permission system, but only until the player logs out. Only for permanent per-player or per-mob rules do you need a permissions plugin such as LuckPerms.

</details>

<details>
<summary><b>Where is the configuration and how do I apply changes?</b></summary>

The config is created at `plugins/SilkSpawners_v2/config.yml` on first start. After editing it, run `/silkspawners config reload` or restart the server.

</details>

<details>
<summary><b>Is it safe to update the plugin?</b></summary>

Yes. Replace the jar and restart - the configuration is migrated automatically and your settings are kept. Only locale files are never touched automatically; see the question about missing messages below.

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

</details>

<details>
<summary><b>How do I make spawners unbreakable for players without SilkTouch or permission?</b></summary>

Set `spawner.destroyable: false`. Spawners can then only be broken by players who actually receive the drop; for everyone else the break is blocked with a message (configurable via `spawner.message.denyDestroy`).

</details>

<details>
<summary><b>A placed spawner spawns pigs / nothing instead of the mob on the item</b></summary>

Only spawner items created by SilkSpawners - mined spawners or ones from `/silkspawners give` - carry the mob information. Spawners from the creative inventory, vanilla `/give` or other plugins are placed with the server default. This also happens to previously mined spawners if `spawner.item.prefix` was changed - see the next question.

</details>

<details>
<summary><b>After changing <code>spawner.item.prefix</code>, previously mined spawners no longer work</b></summary>

Add your previous prefix to the `spawner.item.prefixOld` list - items created before the change then stay recognized. The list must hold all former prefixes, so items survive repeated changes.

</details>

<details>
<summary><b>How do I give players spawners of a specific mob?</b></summary>

Use `/silkspawners give <Player> <Mob> [Amount]`, for example `/silkspawners give Steve zombie 3`. Run `/silkspawners entities` to list all valid mob names.

</details>

<details>
<summary><b>Can I change what an already placed spawner spawns?</b></summary>

Yes. Look at the spawner and run `/silkspawners set <Mob>`, or click the spawner with a spawn egg.

</details>

<details>
<summary><b>How do I stop players from changing spawners with spawn eggs?</b></summary>

That is the default: changing requires `silkspawners.change.<entity>` or `silkspawners.change.*`, which only operators have. If it works for everyone, either the players have the permission or `spawner.permission.disableChange` is set to `true`.

</details>

## Explosions

<details>
<summary><b>Why do configured explosions never trigger?</b></summary>

Three things must be true:

1. At least one tier is configured under `spawner.explosion.normal`, `.silktouch` or `.all` - all lists are empty by default.
2. The mining player has the `silkspawners.explosion` permission. Explosions can also be toggled per player with `/silkspawners explosion <enable/disable> <Player>`.
3. Changes to the tiers were applied with `/silkspawners config reload` or a server restart.

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

Locale files are not overwritten automatically to protect your edits. Run `/silkspawners locale update` to pull in the latest messages - this overwrites custom changes, so keep your own edits in a separate `messages_<name>.properties` file and select it with `messages.locale`.

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

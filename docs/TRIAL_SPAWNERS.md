# Trial spawners

<sub>Requires Minecraft 1.21.3 or newer - disabled by default</sub>

When `trialspawner.enabled` is set, trial spawners can be mined with SilkTouch and keep their full state as an item: `/silkspawners give <player> trial <entity> [amount]` hands them out, `/silkspawners set <entity>` retargets the trial spawner you are looking at, and spawn eggs change the spawned entity. On servers older than 1.21.3 the feature disables itself with a log message.

## Impact on game balance

Vanilla deliberately keeps trial spawners where world generation placed them: they cannot be mined, cannot be obtained in survival, and their reward renews on a cooldown of half an hour. Every trial chamber is a fixed location players have to return to.

Making trial spawners portable changes that:

- A renewable reward source becomes relocatable, so players can concentrate many trial spawners into a single farm.
- Trial spawners enter the economy and can be traded or sold like any other item.
- An ominous trial spawner, with its better loot, becomes a valuable item in itself.

This is the same trade servers already make when they let players silk-mine regular spawners, but the renewing rewards raise the stakes. That is why the feature ships disabled and enabling it is a deliberate decision.

## How the plugin handles it

- **The reward cooldown survives the round trip.** A freshly placed block starts with no cooldown, so mining and replacing a trial spawner would skip the wait between rewards. With `trialspawner.carryCooldown` (enabled by default), a spawner that was ejecting its reward or cooling down when mined withholds its rewards for one full cooldown after it is placed again.
- **Owed cooldowns outlast the feature flag.** The guard that withholds those rewards is installed even while `trialspawner.enabled` is off, so disabling the feature does not release cooldowns that placed spawners still owe.
- **Mining is gated like regular spawners.** `pickaxeRequired`, `silktouchRequired` and `silktouchLevel` control the required tool, `dropChance` makes drops probabilistic, and `destroyable: false` prevents destroying trial spawners without the permission to pick them up.
- **Permissions are a separate branch.** Blocks are governed by `silkspawners.trial.break/place/change.<entity>`, the commands by `silkspawners.command.give.trial.<entity>` and `silkspawners.command.set.trial.<entity>`. The regular spawner permissions do not include them, so enabling the feature grants nothing by itself.
- **Mob waves are not restricted.** Fighting waves at a relocated spawner is no different from fighting at its original location - the balance-relevant part is the renewing reward, which is what the cooldown carry protects.

## What survives the round trip

The dropped item carries the entity type, the ominous flag, both configurations - normal and ominous, including their loot tables - the cooldown length and all spawn counts and ranges. The state lives on the item itself, so it survives chests, restarts and trades.

Two things cannot be carried because the Bukkit API does not expose them: the display entity and a custom list of potential spawns fall back to the vanilla defaults, and the exact remaining cooldown is reduced to whether one was pending, which the cooldown carry then re-applies in full.

## Configuration

All settings live in the `trialspawner` section of the config, documented in the [README](../README.md#configuration).

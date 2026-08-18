# Trial spawners

<sub>Requires Paper 1.21.4 or newer - disabled by default</sub>

When `trialspawner.enabled` is set, trial spawners can be mined with SilkTouch and keep their full state as an item: `/silkspawners give <player> trial <entity> [amount]` hands them out, `/silkspawners set <entity>` retargets the trial spawner you are looking at, and spawn eggs change the spawned entity. The feature needs the Paper API on Minecraft 1.21.4 or newer - this includes Paper forks such as Purpur and Folia. On other servers it disables itself with a log message.

## Impact on game balance

Vanilla deliberately keeps trial spawners where world generation placed them: they cannot be mined, cannot be obtained in survival, and their reward renews on a cooldown of half an hour. Every trial chamber is a fixed location players have to return to.

Making trial spawners portable changes that:

- A renewable reward source becomes relocatable, so players can concentrate many trial spawners into a single farm.
- Trial spawners enter the economy and can be traded or sold like any other item.
- An ominous trial spawner stays ominous as an item, though only as a head start - vanilla reverts a placed spawner to normal once its cooldown ends. Since any trial spawner can be made ominous with an ominous bottle, the lasting change is that relocated spawners offer ominous trials, with their better loot, anywhere.

This is the same trade servers already make when they let players silk-mine regular spawners, but the renewing rewards raise the stakes. That is why the feature ships disabled and enabling it is a deliberate decision.

## How the plugin handles it

- **The reward cooldown survives the round trip.** A freshly placed block starts with no cooldown, so mining and replacing a trial spawner would skip the wait between rewards. With `trialspawner.carryCooldown` (enabled by default), the exact remaining cooldown travels on the item, and the spawner is placed already cooling down for that long. The cooldown is real vanilla state on the placed block, so it keeps counting down on its own - independent of the feature flag or even the plugin. Items with a remaining cooldown carry an extra lore line (`trialspawner.item.cooldownLore`), so the cooldown is visible before the item is placed or traded.
- **Mining is gated like regular spawners.** `pickaxeRequired`, `silktouchRequired` and `silktouchLevel` control the required tool, `dropChance` makes drops probabilistic, and `destroyable: false` prevents destroying trial spawners without the permission to pick them up.
- **Permissions are a separate branch.** Blocks are governed by `silkspawners.trial.break/place/change.<entity>`, the commands by `silkspawners.command.give.trial.<entity>` and `silkspawners.command.set.trial.<entity>`. The regular spawner permissions do not include them, so enabling the feature grants nothing by itself.
- **Mob waves are not restricted.** Fighting waves at a relocated spawner is no different from fighting at its original location - the balance-relevant part is the renewing reward, which is what the cooldown carry protects.

## What survives the round trip

The dropped item carries the entity type, the ominous flag, both configurations - normal and ominous, including their loot tables - the cooldown length, the exact remaining cooldown and all spawn counts and ranges. The state lives on the item itself, so it survives chests, restarts and trades.

Two things cannot be carried because the API does not expose them: the display entity and a custom list of potential spawns fall back to the vanilla defaults.

## Configuration

All settings live in the `trialspawner` section of the config, documented in the [README](../README.md#configuration).

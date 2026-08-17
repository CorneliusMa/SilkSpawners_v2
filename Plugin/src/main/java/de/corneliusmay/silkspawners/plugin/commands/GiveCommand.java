package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.api.events.SpawnerGiveEvent;
import de.corneliusmay.silkspawners.api.events.TrialSpawnerGiveEvent;
import de.corneliusmay.silkspawners.plugin.commands.completers.EntityTabCompleter;
import de.corneliusmay.silkspawners.plugin.commands.completers.OnlinePlayersTabCompleter;
import de.corneliusmay.silkspawners.plugin.commands.completers.TrialTokenTabCompleter;
import de.corneliusmay.silkspawners.plugin.commands.handler.CompositeTabCompletion;
import de.corneliusmay.silkspawners.plugin.commands.handler.ShiftableTabCompletion;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.TabCompletion;
import de.corneliusmay.silkspawners.plugin.entity.EntityNameRenderer;
import de.corneliusmay.silkspawners.plugin.entity.EntityNames;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerFactory;
import de.corneliusmay.silkspawners.plugin.spawner.policy.SpawnerTypeProfile;
import de.corneliusmay.silkspawners.plugin.spawner.trial.TrialSpawner;
import de.corneliusmay.silkspawners.plugin.spawner.trial.TrialSpawnerFactory;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import java.util.Optional;
import java.util.OptionalInt;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.weftkit.wiring.Qualified;
import org.weftkit.wiring.Wired;

@Wired
class GiveCommand extends SilkSpawnersCommand {

    private static final String TRIAL_TOKEN = "trial";

    // Position of the optional token within the argument list the tab completer sees, which
    // excludes the subcommand
    private static final int TRIAL_TOKEN_POSITION = 1;

    private final SpawnerFactory spawnerFactory;

    private final TrialSpawnerFactory trialSpawnerFactory;

    private final ServerPlatform platform;

    private final EntityNameRenderer entityNames;

    private final SpawnerTypeProfile trialSpawnerProfile;

    GiveCommand(
            SpawnerFactory spawnerFactory,
            TrialSpawnerFactory trialSpawnerFactory,
            ServerPlatform platform,
            EntityNameRenderer entityNames,
            @Qualified("trialSpawner") SpawnerTypeProfile trialSpawnerProfile) {
        super(
                "give",
                true,
                new OnlinePlayersTabCompleter(),
                entityOrTrialToken(trialSpawnerFactory, trialSpawnerProfile),
                entitiesAfterTrialToken(trialSpawnerProfile));
        this.spawnerFactory = spawnerFactory;
        this.trialSpawnerFactory = trialSpawnerFactory;
        this.platform = platform;
        this.entityNames = entityNames;
        this.trialSpawnerProfile = trialSpawnerProfile;
    }

    private static TabCompletion entityOrTrialToken(
            TrialSpawnerFactory trialSpawnerFactory, SpawnerTypeProfile trialSpawnerProfile) {
        return new CompositeTabCompletion(
                new EntityTabCompleter(),
                new TrialTokenTabCompleter(
                        TRIAL_TOKEN, trialSpawnerFactory::isEnabled, trialEntities(trialSpawnerProfile)));
    }

    private static TabCompletion entitiesAfterTrialToken(SpawnerTypeProfile trialSpawnerProfile) {
        return new ShiftableTabCompletion(TRIAL_TOKEN, TRIAL_TOKEN_POSITION, trialEntities(trialSpawnerProfile));
    }

    private static EntityTabCompleter trialEntities(SpawnerTypeProfile trialSpawnerProfile) {
        return new EntityTabCompleter(trialSpawnerProfile.commandPermissionInfix());
    }

    @Override
    public boolean invalidSyntax(CommandSender sender) {
        sendMessage(sender, trialSpawnerFactory.isEnabled() ? "USAGE_TRIAL" : "USAGE");
        return false;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) return invalidSyntax(sender);

        boolean trial = TRIAL_TOKEN.equalsIgnoreCase(args[1]);
        int typeIndex = trial ? 2 : 1;
        if (args.length <= typeIndex || args.length > typeIndex + 2) return invalidSyntax(sender);
        String type = args[typeIndex];
        String requestedAmount = args.length == typeIndex + 2 ? args[typeIndex + 1] : null;

        Player p = Bukkit.getPlayer(args[0]);
        if (p == null) {
            sendMessage(sender, "PLAYER_NOT_FOUND", args[0]);
            return false;
        }

        if (trial && !trialSpawnerFactory.isEnabled()) {
            sendMessage(sender, "TRIAL_DISABLED");
            return false;
        }

        EntityType entityType;
        if (type.equalsIgnoreCase(EntityNames.EMPTY)) {
            entityType = null;
        } else {
            entityType = EntityType.fromName(type);
            if (entityType == null) {
                sendMessage(sender, "ENTITY_NOT_FOUND", type);
                return false;
            }
        }

        OptionalInt parsedAmount = requestedAmount == null ? OptionalInt.of(1) : parseAmount(requestedAmount);
        if (parsedAmount.isEmpty()) {
            sendMessage(sender, "INVALID_AMOUNT", requestedAmount);
            return false;
        }

        int amount = parsedAmount.getAsInt();
        if (amount < 1) {
            sendMessage(sender, "TOO_SMALL_AMOUNT");
            return false;
        }

        return trial
                ? giveTrialSpawner(sender, p, entityType, type, amount)
                : giveSpawner(sender, p, entityType, type, amount);
    }

    private boolean giveSpawner(CommandSender sender, Player receiver, EntityType entityType, String type, int amount) {
        Optional<Spawner> requested = spawnerFactory.ofType(entityType);
        if (requested.isEmpty()) {
            sendMessage(sender, "ENTITY_NOT_FOUND", type);
            return false;
        }

        Spawner spawner = requested.get();
        if (!hasEntityPermission(sender, spawner.getEntityType())) return false;

        platform.runOnEntity(
                receiver,
                () -> deliverSpawner(sender, receiver, spawner, amount),
                () -> sendMessage(sender, "PLAYER_NOT_FOUND", receiver.getName()));
        return true;
    }

    private void deliverSpawner(CommandSender sender, Player receiver, Spawner spawner, int requestedAmount) {
        SpawnerGiveEvent event = new SpawnerGiveEvent(sender, receiver, spawner, requestedAmount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        int amount = event.getAmount();
        ItemStack item = spawner.getItemStack();
        item.setAmount(amount);
        receiver.getInventory().addItem(item);

        String coloredName = entityNames.colored(spawner.getEntityType());
        String trailingLetter = amount > 1 ? "s" : "";
        if (receiver != sender) {
            sendMessage(sender, "SUCCESS", amount, coloredName, trailingLetter, receiver.getName());
            sendMessage(receiver, "SUCCESS_TARGET", amount, coloredName, trailingLetter, sender.getName());
        } else {
            sendMessage(sender, "SUCCESS_SELF", amount, coloredName, trailingLetter);
        }
    }

    private boolean giveTrialSpawner(
            CommandSender sender, Player receiver, EntityType entityType, String type, int amount) {
        Optional<TrialSpawner> requested = trialSpawnerFactory.ofType(entityType);
        if (requested.isEmpty()) {
            sendMessage(sender, "ENTITY_NOT_FOUND", type);
            return false;
        }

        TrialSpawner trialSpawner = requested.get();
        if (!hasTrialEntityPermission(sender, trialSpawner.getEntityType())) return false;

        platform.runOnEntity(
                receiver,
                () -> deliverTrialSpawner(sender, receiver, trialSpawner, amount),
                () -> sendMessage(sender, "PLAYER_NOT_FOUND", receiver.getName()));
        return true;
    }

    private void deliverTrialSpawner(
            CommandSender sender, Player receiver, TrialSpawner trialSpawner, int requestedAmount) {
        TrialSpawnerGiveEvent event =
                new TrialSpawnerGiveEvent(sender, receiver, trialSpawner.getState(), requestedAmount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        int amount = event.getAmount();
        ItemStack item = trialSpawner.getItemStack();
        item.setAmount(amount);
        receiver.getInventory().addItem(item);

        String coloredName = entityNames.colored(trialSpawner.getEntityType());
        String trailingLetter = amount > 1 ? "s" : "";
        if (receiver != sender) {
            sendMessage(sender, "SUCCESS_TRIAL", amount, coloredName, trailingLetter, receiver.getName());
            sendMessage(receiver, "SUCCESS_TARGET_TRIAL", amount, coloredName, trailingLetter, sender.getName());
        } else {
            sendMessage(sender, "SUCCESS_SELF_TRIAL", amount, coloredName, trailingLetter);
        }
    }

    private boolean hasEntityPermission(CommandSender sender, EntityType entityType) {
        return hasPermissionBranch(sender, getPermissionString() + ".", entityType);
    }

    private boolean hasTrialEntityPermission(CommandSender sender, EntityType entityType) {
        return hasPermissionBranch(
                sender, getPermissionString() + "." + trialSpawnerProfile.commandPermissionInfix(), entityType);
    }

    private boolean hasPermissionBranch(CommandSender sender, String branch, EntityType entityType) {
        if (sender.hasPermission(branch + EntityNames.serialized(entityType)) || sender.hasPermission(branch + "*"))
            return true;
        sendMessage(sender, "INSUFFICIENT_ENTITY_PERMISSION", entityNames.colored(entityType));
        return false;
    }

    private OptionalInt parseAmount(String amount) {
        try {
            return OptionalInt.of(Integer.parseInt(amount));
        } catch (NumberFormatException ex) {
            return OptionalInt.empty();
        }
    }
}

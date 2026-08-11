package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.completers.ConfigArgumentTabCompleter;
import de.corneliusmay.silkspawners.plugin.commands.handler.RepeatingTabCompletion;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.StaticTabCompletion;
import de.corneliusmay.silkspawners.plugin.config.ConfigApply;
import de.corneliusmay.silkspawners.plugin.config.ConfigEditor;
import de.corneliusmay.silkspawners.plugin.config.ConfigKey;
import de.corneliusmay.silkspawners.plugin.config.ConfigReloader;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueException;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionTier;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionTierEditor;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.CommandSender;
import org.weftkit.wiring.Wired;

@Wired
public class ConfigCommand extends SilkSpawnersCommand {

    private final ConfigEditor editor;

    private final ConfigReloader configReloader;

    private final ExplosionTierEditor tierEditor;

    public ConfigCommand(ConfigEditor editor, ConfigReloader configReloader, ExplosionTierEditor tierEditor) {
        super(
                "config",
                true,
                new StaticTabCompletion("reload", "get", "set", "explosion"),
                new RepeatingTabCompletion(new ConfigArgumentTabCompleter(editor, tierEditor)));
        this.editor = editor;
        this.configReloader = configReloader;
        this.tierEditor = tierEditor;
    }

    public static boolean canSet(SilkSpawnersCommand command, CommandSender sender) {
        return sender.hasPermission(command.getPermissionString() + ".set");
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) return invalidSyntax(sender);

        return switch (args[0].toLowerCase()) {
            case "reload" -> args.length == 1 ? reload(sender) : invalidSyntax(sender);
            case "get" -> args.length == 2 ? get(sender, args[1]) : fail(sender, "GET_USAGE");
            case "set" ->
                args.length >= 3
                        ? set(sender, args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)))
                        : fail(sender, "SET_USAGE");
            case "explosion" -> explosion(sender, args);
            default -> fail(sender, "COMMAND_NOT_FOUND", args[0]);
        };
    }

    private boolean reload(CommandSender sender) {
        if (configReloader.reload()) sendMessage(sender, "RELOAD_SUCCESSFUL");
        else sendMessage(sender, "RELOAD_ERROR");
        return true;
    }

    private boolean get(CommandSender sender, String path) {
        ConfigKey<?> key = key(sender, path);
        if (key == null) return false;

        sendMessage(sender, "GET_VALUE", key.getPath(), editor.currentValue(key), editor.description(key));
        return true;
    }

    private boolean set(CommandSender sender, String path, String input) {
        if (!canSet(this, sender)) return insufficientPermission(sender);

        ConfigKey<?> key = key(sender, path);
        if (key == null) return false;

        String value = editor.allowedValue(key, input);
        if (value == null) return fail(sender, "SET_UNKNOWN_VALUE", key.getPath(), input, values(key));

        try {
            editor.set(key, value);
        } catch (IOException | ConfigValueException ex) {
            return fail(sender, "SET_ERROR", key.getPath(), value, ex.getMessage());
        }

        if (key.getApply() == ConfigApply.AFTER_RELOAD && !configReloader.reload())
            return fail(sender, "SET_RELOAD_ERROR", key.getPath(), value);

        sendMessage(sender, "SET_SUCCESSFUL", key.getPath(), value);
        if (key.getApply() == ConfigApply.AFTER_RESTART) sendMessage(sender, "SET_RESTART_REQUIRED", key.getPath());
        return true;
    }

    private boolean explosion(CommandSender sender, String[] args) {
        if (args.length < 2) return fail(sender, "EXPLOSION_USAGE");

        return switch (args[1].toLowerCase()) {
            case "list" -> explosionList(sender, args);
            case "add" -> explosionAdd(sender, args);
            case "remove" -> explosionRemove(sender, args);
            default -> fail(sender, "EXPLOSION_USAGE");
        };
    }

    private boolean explosionList(CommandSender sender, String[] args) {
        if (args.length > 3) return fail(sender, "EXPLOSION_LIST_USAGE");
        if (args.length == 2) {
            tierEditor.scopeNames().forEach(name -> sendTiers(sender, name));
            return true;
        }

        String name = args[2].toLowerCase(Locale.ROOT);
        if (scope(sender, name) == null) return false;
        sendTiers(sender, name);
        return true;
    }

    private void sendTiers(CommandSender sender, String name) {
        List<ExplosionTier> tiers = tierEditor.scope(name).get();
        if (tiers.isEmpty()) {
            sendMessage(sender, "EXPLOSION_LIST_EMPTY", name);
            return;
        }

        sendMessage(sender, "EXPLOSION_LIST_HEADER", name);
        for (int i = 0; i < tiers.size(); i++)
            sendMessage(sender, "EXPLOSION_LIST_TIER", tierValues(i + 1, tiers.get(i)));
    }

    private boolean explosionAdd(CommandSender sender, String[] args) {
        if (!canSet(this, sender)) return insufficientPermission(sender);
        if (args.length < 4 || args.length > 7) return fail(sender, "EXPLOSION_ADD_USAGE");

        String name = args[2].toLowerCase(Locale.ROOT);
        ConfigKey<List<ExplosionTier>> scope = scope(sender, name);
        if (scope == null) return false;

        Double power = number(sender, args[3], Float.MAX_VALUE, "EXPLOSION_INVALID_POWER");
        if (power == null) return false;
        Double chance =
                args.length > 4 ? number(sender, args[4], 100, "EXPLOSION_INVALID_CHANCE") : Double.valueOf(100);
        if (chance == null) return false;
        Boolean setFire = args.length > 5 ? flag(sender, args[5]) : Boolean.FALSE;
        if (setFire == null) return false;
        Boolean breakBlocks = args.length > 6 ? flag(sender, args[6]) : Boolean.TRUE;
        if (breakBlocks == null) return false;

        ExplosionTier tier = new ExplosionTier(chance, power.floatValue(), setFire, breakBlocks);
        return save(sender, () -> tierEditor.add(scope, tier), "EXPLOSION_ADD_SUCCESSFUL", tierValues(name, tier));
    }

    private boolean explosionRemove(CommandSender sender, String[] args) {
        if (!canSet(this, sender)) return insufficientPermission(sender);
        if (args.length != 4) return fail(sender, "EXPLOSION_REMOVE_USAGE");

        String name = args[2].toLowerCase(Locale.ROOT);
        ConfigKey<List<ExplosionTier>> scope = scope(sender, name);
        if (scope == null) return false;

        int tier = tierNumber(args[3], scope.get().size());
        if (tier == 0) return fail(sender, "EXPLOSION_TIER_NOT_FOUND", name, args[3]);

        return save(sender, () -> tierEditor.remove(scope, tier - 1), "EXPLOSION_REMOVE_SUCCESSFUL", name, tier);
    }

    private ConfigKey<List<ExplosionTier>> scope(CommandSender sender, String name) {
        ConfigKey<List<ExplosionTier>> scope = tierEditor.scope(name);
        if (scope == null)
            sendMessage(sender, "EXPLOSION_LIST_NOT_FOUND", name, String.join(", ", tierEditor.scopeNames()));
        return scope;
    }

    private Object[] tierValues(Object first, ExplosionTier tier) {
        return new Object[] {
            first,
            String.valueOf(tier.chanceValue()),
            String.valueOf(tier.powerValue()),
            tier.setFire(),
            tier.breakBlocks()
        };
    }

    private boolean save(CommandSender sender, TierWrite write, String successKey, Object... args) {
        try {
            write.run();
        } catch (IOException ex) {
            return fail(sender, "EXPLOSION_SAVE_ERROR", ex.getMessage());
        }
        sendMessage(sender, successKey, args);
        return true;
    }

    private Double number(CommandSender sender, String input, double max, String errorKey) {
        Double value = parse(input);
        if (value != null && value >= 0 && value <= max) return value;

        sendMessage(sender, errorKey, input);
        return null;
    }

    private Double parse(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Boolean flag(CommandSender sender, String input) {
        if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) return Boolean.parseBoolean(input);

        sendMessage(sender, "EXPLOSION_INVALID_FLAG", input);
        return null;
    }

    private int tierNumber(String input, int size) {
        try {
            int number = Integer.parseInt(input);
            return number >= 1 && number <= size ? number : 0;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private ConfigKey<?> key(CommandSender sender, String path) {
        ConfigKey<?> key = editor.find(path);
        if (key == null) {
            sendMessage(sender, "KEY_NOT_FOUND", path);
            return null;
        }
        if (!key.isSettable()) {
            sendMessage(sender, "NOT_SETTABLE", key.getPath());
            return null;
        }
        return key;
    }

    private String values(ConfigKey<?> key) {
        return String.join(", ", editor.allowedValues(key));
    }

    private boolean fail(CommandSender sender, String key, Object... args) {
        sendMessage(sender, key, args);
        return false;
    }

    private interface TierWrite {
        void run() throws IOException;
    }
}

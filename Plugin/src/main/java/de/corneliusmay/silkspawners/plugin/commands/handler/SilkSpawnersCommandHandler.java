package de.corneliusmay.silkspawners.plugin.commands.handler;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SilkSpawnersCommandHandler implements CommandExecutor {

    private final SilkSpawners plugin;

    @Getter(AccessLevel.PACKAGE)
    private final String mainCommand;

    private final SilkSpawnersCommand helpCommand;

    private SilkSpawnersCommand defaultCommand;

    private final List<SilkSpawnersCommand> commands;

    private final SilkSpawnersTabCompleter tabCompleter;

    public SilkSpawnersCommandHandler(SilkSpawners plugin, String command) {
        this.plugin = plugin;
        this.mainCommand = command;
        this.commands = new ArrayList<>();
        this.tabCompleter = new SilkSpawnersTabCompleter(this);
        this.helpCommand = addHelpCommand();
    }

    public SilkSpawnersCommandHandler(SilkSpawners plugin, String command, SilkSpawnersCommand defaultCommand) {
        this.plugin = plugin;
        this.mainCommand = command;
        this.commands = new ArrayList<>();
        this.tabCompleter = new SilkSpawnersTabCompleter(this);
        this.helpCommand = addHelpCommand();
        this.defaultCommand = addDefaultCommand(defaultCommand);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command c, String s, String[] args) {
        if(args.length < 1) {
            if(defaultCommand == null || !defaultCommand.hasPermission(commandSender)) return helpCommand.execute(commandSender, new String[0]);
            else return defaultCommand.execute(commandSender, new String[0]);
        }

        SilkSpawnersCommand command = getCommand(args[0]);
        if(command == null) {
            commandSender.sendMessage(plugin.getLocale().getMessage("COMMAND_NOT_FOUND", getAvailableCommandsString(commandSender)));
            return false;
        }

        if(!command.hasPermission(commandSender)) return command.insufficientPermission(commandSender);

        return command.execute(commandSender, Arrays.copyOfRange(args, 1, args.length));
    }

    public void addCommand(SilkSpawnersCommand command) {
        if(getCommand(command.getCommand()) != null) return;
        command.setCommandHandler(this);
        command.setPlugin(plugin);
        commands.add(command);
    }

    private SilkSpawnersCommand addHelpCommand() {
        HelpCommand help = new HelpCommand(this);
        addCommand(help);
        return help;
    }

    private SilkSpawnersCommand addDefaultCommand(SilkSpawnersCommand command) {
        addCommand(command);
        return command;
    }

    public void register() {
        PluginCommand command = plugin.getCommand(mainCommand);
        if(command == null) throw new RuntimeException("Command is not registered in plugin.yml");
        command.setExecutor(this);
        command.setTabCompleter(tabCompleter);
    }

    public SilkSpawnersCommand getCommand(String command) {
        return commands.stream().filter(c -> c.getCommand().equalsIgnoreCase(command)).findFirst().orElse(null);
    }

    public List<String> getCommands(CommandSender cs) {
        return commands.stream().filter(c -> c.hasPermission(cs)).map(SilkSpawnersCommand::getCommand).collect(Collectors.toList());
    }

    public String getAvailableCommandsString(CommandSender cs) {
        return " - /" + mainCommand + " " + Arrays.toString(getCommands(cs).toArray(String[]::new)).replace("[", "").replace("]", "").replace(", ", "\n - /" + mainCommand + " ");
    }
}

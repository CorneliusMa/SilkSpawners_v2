package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SilkSpawnersCommandHandler implements CommandExecutor {

    @Getter
    private final List<SilkSpawnersCommand> commands;

    @Getter
    private final SilkSpawnersTabCompleter tabCompleter;

    public SilkSpawnersCommandHandler() {
        this.commands = new ArrayList<>();
        this.tabCompleter = new SilkSpawnersTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command c, String s, String[] args) {
        args = Arrays.stream(args).map(String::toLowerCase).toList().toArray(String[]::new);
        if(args.length < 1) {
            commandSender.sendMessage(SilkSpawners.getInstance().getLocale().getMessage("COMMAND_NOT_FOUND", getAvailableCommandsString(commandSender)));
            return false;
        }

        SilkSpawnersCommand command = getCommand(args[0]);
        if(command == null) {
            commandSender.sendMessage(SilkSpawners.getInstance().getLocale().getMessage("COMMAND_NOT_FOUND", getAvailableCommandsString(commandSender)));
            return false;
        }

        if(!command.hasPermission(commandSender)) return command.insufficientPermission(commandSender);

        return command.execute(commandSender, Arrays.copyOfRange(args, 1, args.length));
    }

    public void registerCommand(SilkSpawnersCommand command) {
        if(getCommand(command.getCommand()) != null) return;
        commands.add(command);
    }

    public SilkSpawnersCommand getCommand(String command) {
        return commands.stream().filter(c -> c.getCommand().equals(command)).findFirst().orElse(null);
    }

    public List<String> getCommands(CommandSender cs) {
        return commands.stream().filter(c -> c.hasPermission(cs)).map(SilkSpawnersCommand::getCommand).collect(Collectors.toList());
    }

    public String getAvailableCommandsString(CommandSender cs) {
        return " - /silkspawners " + Arrays.toString(getCommands(cs).toArray(String[]::new)).replace("[", "").replace("]", "").replace(", ", "\n - /silkspawners ");
    }
}

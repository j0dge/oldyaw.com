package opm.luftwaffe.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import opm.luftwaffe.Luftwaffe;
import opm.luftwaffe.features.command.Command;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("Commands: ");
        for (Command command : Luftwaffe.commandManager.getCommands()) {
            HelpCommand.sendMessage(ChatFormatting.GRAY + Luftwaffe.commandManager.getPrefix() + command.getName());
        }
    }
}


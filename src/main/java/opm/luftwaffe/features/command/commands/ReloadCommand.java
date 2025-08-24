package opm.luftwaffe.features.command.commands;

import opm.luftwaffe.Luftwaffe;
import opm.luftwaffe.features.command.Command;

public class ReloadCommand
        extends Command {
    public ReloadCommand() {
        super("reload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        Luftwaffe.reload();
    }
}


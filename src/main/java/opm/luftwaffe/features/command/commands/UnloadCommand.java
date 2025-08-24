package opm.luftwaffe.features.command.commands;

import opm.luftwaffe.Luftwaffe;
import opm.luftwaffe.features.command.Command;

public class UnloadCommand
        extends Command {
    public UnloadCommand() {
        super("unload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        Luftwaffe.unload(true);
    }
}


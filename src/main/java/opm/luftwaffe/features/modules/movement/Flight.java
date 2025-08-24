package opm.luftwaffe.features.modules.movement;

import opm.luftwaffe.features.modules.Module;

public class Flight
        extends Module {
    public Flight() {
        super("Flight", "Flight.", Module.Category.MOVEMENT, true, false, false);
    }
}


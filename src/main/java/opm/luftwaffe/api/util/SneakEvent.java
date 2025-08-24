package opm.luftwaffe.api.util;

import net.minecraftforge.fml.common.eventhandler.Event;

public class SneakEvent
        extends Event {
    boolean sneaking;

    public SneakEvent(boolean sneaking) {
        this.sneaking = sneaking;
    }

    public boolean isSneaking() {
        return this.sneaking;
    }

    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }
}
package opm.luftwaffe.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventStage
        extends Event {
    private int stage;

    public EventStage() {
    }

    public EventStage(int stage) {
        this.stage = stage;
    }

    public int getStage() {
        return this.stage;
    }

    public enum Stage {
        PREP,
        PRE,
        POST
    }

    public void setStage(int stage) {
        this.stage = stage;
    }
}
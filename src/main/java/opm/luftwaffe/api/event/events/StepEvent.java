package opm.luftwaffe.api.event.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import opm.luftwaffe.api.event.EventStage;

@Cancelable
public class StepEvent extends EventStage {
    private final Entity entity;
    private float height;

    public StepEvent(int stage, Entity entity) {
        super(stage);
        this.entity = entity;
        this.height = entity.stepHeight;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
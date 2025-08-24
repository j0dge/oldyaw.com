package opm.luftwaffe.features.modules.movement;

import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.setting.Setting;
import opm.luftwaffe.api.event.events.MoveEvent;
import opm.luftwaffe.api.util.EntityUtil;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoAccel extends Module {
    public final Setting<Boolean> noBedrock = this.register(new Setting("OnlyBedrock", false));
    public final Setting<Boolean> noSlide = this.register(new Setting("NoSlide", true, "Stops immediately when no input"));
    private static NoAccel INSTANCE = new NoAccel();

    public NoAccel() {
        super("NoAccel", "remove acceleration", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }

    public static NoAccel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoAccel();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onMoveEvent(MoveEvent event) {
        // Check if player is above Y=7 or NoBedrock setting is disabled
        if (!(mc.player.posY < 7.0) || !this.noBedrock.getValue()) {
            // Only process movement when:
            // - Event is in pre-movement phase (stage 0)
            // - Player is not sneaking
            // - Player is not in water
            // - Player is not in lava
            // - Player is not riding anything
            if (event.getStage() == 0
                    && !mc.player.isSneaking()
                    && !mc.player.isInWater()
                    && !mc.player.isInLava()
                    && !mc.player.isRiding()) {

                MovementInput movementInput = mc.player.movementInput;
                float moveForward = movementInput.moveForward;
                float moveStrafe = movementInput.moveStrafe;
                float rotationYaw = mc.player.rotationYaw;

                // Handle no input with NoSlide setting
                if (moveForward == 0.0F && moveStrafe == 0.0F) {
                    if (noSlide.getValue()) {
                        event.setX(0.0D);
                        event.setZ(0.0D);
                        return; // Completely stop movement
                    }
                    // If NoSlide is false, let normal movement continue
                    return;
                }

                // Adjust rotation based on movement direction
                if (moveForward != 0.0F) {
                    if (moveStrafe > 0.0F) {
                        rotationYaw += (moveForward > 0.0F ? -45 : 45);
                    } else if (moveStrafe < 0.0F) {
                        rotationYaw += (moveForward > 0.0F ? 45 : -45);
                    }
                    moveStrafe = 0.0F;
                    if (moveForward != 0.0F) {
                        moveForward = (moveForward > 0.0F) ? 1.0F : -1.0F;
                    }
                }

                // Normalize strafe input
                moveStrafe = (moveStrafe == 0.0F) ? moveStrafe : ((moveStrafe > 0.0F) ? 1.0F : -1.0F);

                // Calculate new movement vectors
                double radians = Math.toRadians(rotationYaw + 90.0F);
                double cos = Math.cos(radians);
                double sin = Math.sin(radians);
                double maxSpeed = EntityUtil.getMaxSpeed();

                event.setX(moveForward * maxSpeed * cos + moveStrafe * maxSpeed * sin);
                event.setZ(moveForward * maxSpeed * sin - moveStrafe * maxSpeed * cos);
            }
        }
    }
}
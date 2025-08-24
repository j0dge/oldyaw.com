package opm.luftwaffe.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import opm.luftwaffe.api.event.events.MoveEvent;

public class MoveUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static double[] directionSpeed(double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

        if (forward != 0.0F) {
            if (side > 0.0F) {
                yaw += (float)(forward > 0.0F ? -45 : 45);
            } else if (side < 0.0F) {
                yaw += (float)(forward > 0.0F ? 45 : -45);
            }
            side = 0.0F;
            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        double sin = Math.sin(Math.toRadians(yaw + 90.0F));
        double cos = Math.cos(Math.toRadians(yaw + 90.0F));
        double posX = forward * speed * cos + side * speed * sin;
        double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static boolean anyMovementKeys() {
        return mc.player.movementInput.jump || mc.player.movementInput.sneak ||
                mc.player.movementInput.moveForward != 0 || mc.player.movementInput.moveStrafe != 0;
    }

    public static boolean anyMovementKeysNoSneak() {
        return mc.player.movementInput.jump ||
                mc.player.movementInput.moveForward != 0 || mc.player.movementInput.moveStrafe != 0;
    }

    public static boolean isFullMoving() {
        return isMoving() || isMovingVertically();
    }

    public static boolean isMovingVertically(EntityLivingBase entity) {
        return entity.posY - entity.prevPosY != 0.0D;
    }

    public static boolean isMovingVertically() {
        return isMovingVertically(mc.player);
    }

    public static boolean isMoving(EntityLivingBase entity) {
        return entity.moveForward != 0.0F || entity.moveStrafing != 0.0F;
    }

    public static boolean isMoving() {
        return isMoving(mc.player);
    }

    public static double getDistance2D() {
        double xDist = mc.player.posX - mc.player.prevPosX;
        double zDist = mc.player.posZ - mc.player.prevPosZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }

    public static void strafe(MoveEvent event, double speed) {
        if (isMoving()) {
            double[] strafe = strafe(speed);
            event.setX(strafe[0]);
            event.setZ(strafe[1]);
        } else {
            event.setX(0.0D);
            event.setZ(0.0D);
        }
    }

    public static double[] strafe(double speed) {
        return strafe(mc.player, speed);
    }

    public static double[] strafe(Entity entity, double speed) {
        return strafe(entity, mc.player.movementInput, speed);
    }

    public static double[] strafe(Entity entity, MovementInput movementInput, double speed) {
        float moveForward = movementInput.moveForward;
        float moveStrafe = movementInput.moveStrafe;
        float rotationYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * mc.getRenderPartialTicks();

        if (moveForward != 0.0F) {
            if (moveStrafe > 0.0F) {
                rotationYaw += (float)(moveForward > 0.0F ? -45 : 45);
            } else if (moveStrafe < 0.0F) {
                rotationYaw += (float)(moveForward > 0.0F ? 45 : -45);
            }
            moveStrafe = 0.0F;
            if (moveForward > 0.0F) {
                moveForward = 1.0F;
            } else if (moveForward < 0.0F) {
                moveForward = -1.0F;
            }
        }

        double posX = moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }

    public static double getSpeed() {
        return getSpeed(false);
    }

    public static double getSpeed(boolean slowness) {
        double defaultSpeed = 0.2873D;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        if (slowness && mc.player.isPotionActive(MobEffects.SLOWNESS)) {
            int amplifier = mc.player.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (amplifier + 1);
        }
        return defaultSpeed;
    }

    public static double getJumpSpeed() {
        double defaultSpeed = 0.0D;
        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
            int amplifier = mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier();
            defaultSpeed += (amplifier + 1) * 0.1D;
        }
        return defaultSpeed;
    }

    public static void setMotion(double x, double y, double z) {
        if (mc.player != null && mc.player.getRidingEntity() != null) {
            Entity riding = mc.player.getRidingEntity();
            riding.motionX = x;
            riding.motionY = y;
            riding.motionZ = z;
        } else {
            mc.player.motionX = x;
            mc.player.motionY = y;
            mc.player.motionZ = z;
        }
    }

    public static double calcEffects(double speed) {
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
            speed *= 1.0 + 0.2 * (amplifier + 1);
        }
        if (mc.player.isPotionActive(MobEffects.SLOWNESS)) {
            int amplifier = mc.player.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier();
            speed /= 1.0 + 0.2 * (amplifier + 1);
        }
        return speed;
    }

    public static boolean isInDirection(BlockPos pos) {
        if (mc.player.motionX == 0.0 && mc.player.motionZ == 0.0) {
            return true;
        }
        BlockPos movingPos = new BlockPos(mc.player).add(mc.player.motionX * 10000, 0, mc.player.motionZ * 10000);
        BlockPos antiPos = new BlockPos(mc.player).add(mc.player.motionX * -10000, 0, mc.player.motionY * -10000);
        return movingPos.distanceSq(pos) < antiPos.distanceSq(pos);
    }

    public static double direction(float rotationYaw, double moveForward, double moveStrafing) {
        if (moveForward < 0.0) {
            rotationYaw += 180.0F;
        }
        float forward = 1.0F;
        if (moveForward < 0.0) {
            forward = -0.5F;
        } else if (moveForward > 0.0) {
            forward = 0.5F;
        }
        if (moveStrafing > 0.0) {
            rotationYaw -= 90.0F * forward;
        }
        if (moveStrafing < 0.0) {
            rotationYaw += 90.0F * forward;
        }
        return Math.toRadians(rotationYaw);
    }
}

package opm.luftwaffe.api.manager;

import java.text.DecimalFormat;
import java.util.HashMap;

import opm.luftwaffe.features.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class PositionManager extends Feature {
    // Constants
    public static final double LAST_JUMP_INFO_DURATION_DEFAULT = 3.0D;
    private static final int DISTANCE_THRESHOLD = 20; // 20 blocks radius
    private static final double KMH_CONVERSION_FACTOR = 71.2729367892D; // Converts blocks/tick to km/h
    private double x;
    private double y;
    private double z;
    private boolean onground;


    // Jump tracking
    public static boolean didJumpThisTick = false;
    public static boolean isJumping = false;
    public boolean didJumpLastTick = false;
    public boolean wasFirstJump = true;
    public long jumpInfoStartTime = 0L;

    // Speed metrics
    public double firstJumpSpeed = 0.0D;
    public double lastJumpSpeed = 0.0D;
    public double percentJumpSpeedChanged = 0.0D;
    public double jumpSpeedChanged = 0.0D;
    public double speedometerCurrentSpeed = 0.0D;

    // Player speed tracking
    public HashMap<EntityPlayer, Double> playerSpeeds = new HashMap<>();

    public static void setDidJumpThisTick(boolean val) {
        didJumpThisTick = val;
    }

    public static void setIsJumping(boolean val) {
        isJumping = val;
    }

    public float getLastJumpInfoTimeRemaining() {
        return (float) (Minecraft.getSystemTime() - this.jumpInfoStartTime) / 1000.0F;
    }

    public void updateValues() {
        // Calculate distance moved last tick (squared)
        double xDist = mc.player.posX - mc.player.prevPosX;
        double zDist = mc.player.posZ - mc.player.prevPosZ;
        this.speedometerCurrentSpeed = xDist * xDist + zDist * zDist;

        // Handle jump calculations
        if (didJumpThisTick && (!mc.player.onGround || isJumping)) {
            if (didJumpThisTick && !this.didJumpLastTick) {
                this.wasFirstJump = (this.lastJumpSpeed == 0.0D);
                this.percentJumpSpeedChanged = (this.speedometerCurrentSpeed != 0.0D)
                        ? (this.speedometerCurrentSpeed / this.lastJumpSpeed - 1.0D)
                        : -1.0D;
                this.jumpSpeedChanged = this.speedometerCurrentSpeed - this.lastJumpSpeed;
                this.jumpInfoStartTime = Minecraft.getSystemTime();
                this.lastJumpSpeed = this.speedometerCurrentSpeed;
                this.firstJumpSpeed = this.wasFirstJump ? this.lastJumpSpeed : 0.0D;
            }
            this.didJumpLastTick = true;
        } else {
            this.didJumpLastTick = false;
            this.lastJumpSpeed = 0.0D;
        }

        updateNearbyPlayerSpeeds();
    }

    private void updateNearbyPlayerSpeeds() {
        for (EntityPlayer player : mc.world.playerEntities) {
            double distanceSq = mc.player.getDistanceSq(player);
            if (distanceSq < (DISTANCE_THRESHOLD * DISTANCE_THRESHOLD)) {
                double xDist = player.posX - player.prevPosX;
                double zDist = player.posZ - player.prevPosZ;
                double speedSq = xDist * xDist + zDist * zDist;
                playerSpeeds.put(player, speedSq);
            }
        }
    }

    public double getPlayerSpeed(EntityPlayer player) {
        Double speed = playerSpeeds.get(player);
        return speed != null ? convertToKmph(speed) : 0.0D;
    }

    private double convertToKmph(double speedSquared) {
        return MathHelper.sqrt(speedSquared) * KMH_CONVERSION_FACTOR;
    }

    public double getSpeedKph() {
        double speedKph = convertToKmph(this.speedometerCurrentSpeed);
        return Double.parseDouble(new DecimalFormat("##.00").format(speedKph));
    }

    public double getSpeedMps() {
        double speedMps = convertToKmph(this.speedometerCurrentSpeed) / 3.6D;
        return Double.parseDouble(new DecimalFormat("##.00").format(speedMps));
    }

    public void restorePosition() {
        PositionManager.mc.player.posX = this.x;
        PositionManager.mc.player.posY = this.y;
        PositionManager.mc.player.posZ = this.z;
        PositionManager.mc.player.onGround = this.onground;
    }

    public void updatePosition() {
        this.x = PositionManager.mc.player.posX;
        this.y = PositionManager.mc.player.posY;
        this.z = PositionManager.mc.player.posZ;
        this.onground = PositionManager.mc.player.onGround;
    }

}
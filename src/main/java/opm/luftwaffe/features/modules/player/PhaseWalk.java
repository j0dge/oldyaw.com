package opm.luftwaffe.features.modules.player;

import opm.luftwaffe.api.event.events.MoveEvent;
import opm.luftwaffe.api.event.events.PacketEvent;
import opm.luftwaffe.features.setting.Setting;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.mixin.mixins.access.INetworkManager;
import opm.luftwaffe.api.util.EntityUtil;
import opm.luftwaffe.api.util.NullUtils;
import opm.luftwaffe.api.util.Timer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static opm.luftwaffe.features.modules.player.PhaseWalk.HandleTeleport.Above;
import static opm.luftwaffe.features.modules.player.PhaseWalk.HandleTeleport.All;
import static opm.luftwaffe.api.util.MathUtil.random;

public class PhaseWalk extends Module {
    Timer timer = new Timer();


    Setting<Boolean> edgeEnable = register(new Setting<Boolean>("EdgeEnable",true));
    public enum modes {Clip, Smooth}
    Setting<PhaseWalk.modes> Modes = register(new Setting("Modes", PhaseWalk.modes.Clip));
    Setting<Integer> delay = register(new Setting<Integer>("Delay",213,0,1000));
    Setting<Integer> attempts = register(new Setting<Integer>("Attempts",3,0,10));

    public enum HandleTeleport {All, Above, Cancel, None}
    Setting<PhaseWalk.HandleTeleport> handleTeleport = register(new Setting("HandleTeleport", Above));

    Setting<Boolean> onlyInBlock = register(new Setting<Boolean>("Only in block",true));
    Setting<Boolean> down = register(new Setting<Boolean>("Down",true));
    Setting<Boolean> noAccel = register(new Setting<Boolean>("NoAccel",false));
    Setting<Boolean> hyperAccel = register (new Setting<Boolean>("HyperAccel",true));



    public PhaseWalk() {super("PhaseWalk", "shit", Module.Category.PLAYER, true, false, false);}


    public static PhaseWalk INSTANCE;
    boolean cancel = false;
    int teleportID = 0;


    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof CPacketConfirmTeleport && this.handleTeleport.getValue().equals(HandleTeleport.Cancel)) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.teleportID = ((SPacketPlayerPosLook)event.getPacket()).getTeleportId();
            if (this.handleTeleport.getValue().equals(All)) {
                mc.getConnection().sendPacket((Packet)new CPacketConfirmTeleport(this.teleportID - 1));
                mc.getConnection().sendPacket((Packet)new CPacketConfirmTeleport(this.teleportID));
                mc.getConnection().sendPacket((Packet)new CPacketConfirmTeleport(this.teleportID + 1));
            }
            if (this.handleTeleport.getValue().equals(Above)) {
                mc.getConnection().sendPacket((Packet)new CPacketConfirmTeleport(this.teleportID + 1));
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        if (this.Modes.getValue().equals(modes.Clip)) {
            if (this.shouldPacket()) {
                if (this.timer.passedMs(delay.getValue())) {
                    double[] forward = EntityUtil.forward(this.getSpeed());
                    for (int i = 0; i < this.attempts.getValue().intValue(); ++i) {
                        this.sendPackets(PhaseWalk.mc.player.posX + forward[0], PhaseWalk.mc.player.posY + this.getUpMovement(), PhaseWalk.mc.player.posZ + forward[1]);
                    }
                    this.timer.reset();
                }
            } else {
                this.cancel = false;
            }
        }
    }

    double getUpMovement() {
        boolean isAtHeight1 = PhaseWalk.mc.player.posY <= 1.0;
        // Если на высоте 1, запрещаем движение вниз
        if (isAtHeight1 && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
            return 0;
        }
        return (double)(PhaseWalk.mc.gameSettings.keyBindJump.isKeyDown() ? 1 : (PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown() ? -1 : 0)) * this.getSpeed();
    }

    public void sendPackets(double x, double y, double z) {
        this.cancel = false;
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(x, y, z, PhaseWalk.mc.player.onGround));
        send(new Vec3d(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY + randomBounds(), PhaseWalk.mc.player.posZ));
        this.cancel = true;
    }

    public void sendFiveBPackets(double x, double y, double z) {
        double sin = -Math.sin(Math.toRadians(PhaseWalk.mc.player.rotationYaw));
        double cos = Math.cos(Math.toRadians(PhaseWalk.mc.player.rotationYaw));
        PhaseWalk.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(PhaseWalk.mc.player.posX + sin, PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + cos, true));
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }

        // Проверяем, находится ли игрок на высоте 1
        boolean isAtHeight1 = PhaseWalk.mc.player.posY <= 1.0;

        if (this.shouldPacket() || (!isAtHeight1 && this.down.getValue().booleanValue() && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown() && this.isInBlock())) {
            double[] forward = EntityUtil.forward(this.getSpeed());
            if (this.timer.passedMs(delay.getValue())) {
                for (int i = 0; i < this.attempts.getValue().intValue(); ++i) {
                    this.sendPackets(PhaseWalk.mc.player.posX + forward[0], PhaseWalk.mc.player.posY + this.getUpMovement(), PhaseWalk.mc.player.posZ + forward[1]);
                }
                this.timer.reset();
            }
        }

        // Отключаем спуск вниз, если игрок на высоте 1
        if (!isAtHeight1 && this.down.getValue().booleanValue() && this.shouldPacket() && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
            event.setY(event.getY() - 0.05);
        }

        if (this.noAccel.getValue().booleanValue()) {
            if (!(PhaseWalk.mc.player.isSneaking() || PhaseWalk.mc.player.isInWater() || PhaseWalk.mc.player.isInLava() || PhaseWalk.mc.player.movementInput.moveForward == 0.0f && PhaseWalk.mc.player.movementInput.moveStrafe == 0.0f)) {
                if (this.hyperAccel.getValue().booleanValue()) {
                    EntityUtil.phaseSpeed(event, EntityUtil.getStrictBaseSpeed(0.2873));
                } else {
                    EntityUtil.strafe(event, EntityUtil.getStrictBaseSpeed(0.2873));
                }
            } else if (PhaseWalk.mc.player.movementInput.moveForward == 0.0f && PhaseWalk.mc.player.movementInput.moveStrafe == 0.0f) {
                PhaseWalk.mc.player.motionZ = 0.0;
                PhaseWalk.mc.player.motionX = 0.0;
                event.setX(0.0);
                event.setZ(0.0);
            }
        }
    }

    double getSpeed() {
        return EntityUtil.getDefaultMoveSpeed() / 10.0;
    }

    boolean shouldPacket() {
        return !(this.edgeEnable.getValue() != false && !PhaseWalk.mc.player.collidedHorizontally || this.onlyInBlock.getValue() != false && !this.isPhasing());
    }

    boolean isInBlock() {
        return this.onlyInBlock.getValue() == false || this.isPhasing();
    }

    public boolean isPhasing() {
        AxisAlignedBB bb = PhaseWalk.mc.player.getEntityBoundingBox();
        for (int x = MathHelper.floor((double)bb.minX); x < MathHelper.floor((double)bb.maxX) + 1; ++x) {
            for (int y = MathHelper.floor((double)bb.minY); y < MathHelper.floor((double)bb.maxY) + 1; ++y) {
                for (int z = MathHelper.floor((double)bb.minZ); z < MathHelper.floor((double)bb.maxZ) + 1; ++z) {
                    if (!PhaseWalk.mc.world.getBlockState(new BlockPos(x, y, z)).getMaterial().blocksMovement() || !bb.intersects(new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1)))) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public void send(Vec3d vec) {
        ((INetworkManager)mc.player.connection.getNetworkManager()).distpatchNow((Packet<?>)new CPacketPlayer.Position(vec.x, vec.y, vec.z, true), null);
    }

    public static double randomBounds() {
        int randomValue = random.nextInt(22) + 70;
        if (random.nextBoolean()) {
            return randomValue;
        }
        return -randomValue;
    }
}

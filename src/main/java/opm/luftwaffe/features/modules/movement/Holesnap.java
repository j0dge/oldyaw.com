package opm.luftwaffe.features.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.text.DecimalFormat;

import opm.luftwaffe.api.util.HoleUtils;
import opm.luftwaffe.api.util.NullUtils;
import opm.luftwaffe.api.util.PlayerUtil;
import opm.luftwaffe.features.command.Command;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.setting.Setting;
import opm.luftwaffe.api.util.*;
import opm.luftwaffe.mixin.mixins.access.ITimer;
import opm.luftwaffe.mixin.mixins.access.IMinecraft;
import opm.luftwaffe.api.event.events.HolesnapUtil;
import opm.luftwaffe.api.util.TargetUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Holesnap
        extends Module {
    public boolean firsttime = false;
    public boolean cancel = false;
    private static Holesnap INSTANCE = new Holesnap();
    public Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(3.0f), Float.valueOf(1.0f), Float.valueOf(7.0f)));
    public Setting<Boolean> postTimer = this.register(new Setting<Boolean>("Use Post Timer", true));
    public Setting<Boolean> useTimer = this.register(new Setting<Boolean>("Use TickSpeed", true));
    public Setting<Float> timerAmount = this.register(new Setting<Float>("Tick Amount", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(10.0f)));
    public Setting<Float> BoostTime = this.register(new Setting<Float>("Boost Time", Float.valueOf(25.0f), Float.valueOf(10.0f), Float.valueOf(100.0f)));
    public Setting<Boolean> doubles = this.register(new Setting<Boolean>("Doubles", true));
    public Setting<Boolean> selfHole = this.register(new Setting<Boolean>("Swapping", true));
    public Setting<Boolean> vertFactor = this.register(new Setting<Boolean>("Vertical Factor", true));
    public Setting<Boolean> physics = this.register(new Setting<Boolean>("Physics", true));
    public Setting<Float> physicsTicks = this.register(new Setting<Float>("Physics Ticks", Float.valueOf(3.0f), Float.valueOf(3.0f), Float.valueOf(20.0f)));

    public enum Mode {Normal, Pathfinding}

    Setting<Holesnap.Mode> mode = register(new Setting("Mode", Holesnap.Mode.Normal));

    public enum StepMode {Vanilla, NCP, NONE}

    Setting<Holesnap.StepMode> stepMode = register(new Setting("Mode", Holesnap.StepMode.NCP));
    float oldTickLength = 50.0f;
    ITimer timer;
    int boostTime = 0;
    DecimalFormat df = new DecimalFormat("#.##");
    private int ticks = 0;
    BlockPos startPos = null;
    public Vec3d lastTarget;
    HoleUtils.Hole targetHole;
    boolean disablenexttick = false;
    int stuckTicks;
    int posttimerticks;
    boolean ranPhysics = false;

    public Holesnap() {
        super("Holesnap", "Robotic ahh shit", Category.MOVEMENT, false, false, false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (NullUtils.nullCheck()) {
            return;
        }
        this.lastTarget = null;
        this.boostTime = 0;
        this.timer = (ITimer) ((IMinecraft) mc).getTimer();
        this.oldTickLength = 50.0f;
        this.stuckTicks = 0;
        this.targetHole = null;
        this.startPos = PlayerUtil.getPlayerPos();
        this.targetHole = TargetUtils.getTargetHoleVec3D(this.range.getValue().doubleValue(), this.selfHole.getValue(), this.doubles.getValue());
        if (this.targetHole == null) {
            this.cancel = false;
            if (this.useTimer.getValue().booleanValue() && Holesnap.mc.player.ticksExisted > 10) {
                this.timer.setTickLength(this.oldTickLength);
            }
            Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" + " " + ChatFormatting.RESET + ChatFormatting.GRAY + "[Holesnap] " + ChatFormatting.RESET + "Unable to find hole. Disabling");
            this.lastTarget = null;
            this.toggle();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (NullUtils.nullCheck()) {
            return;
        }
        this.ranPhysics = false;
        if (Holesnap.mc.player.ticksExisted > 10) {
            this.timer.setTickLength(this.oldTickLength);
        }
        this.lastTarget = null;
        Holesnap.mc.player.stepHeight = 0.5f;
        this.posttimerticks = 0;

    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        ++this.boostTime;
        if (this.boostTime >= this.BoostTime.getValue().intValue() && Holesnap.mc.player.ticksExisted > 10) {
            this.timer.setTickLength(this.oldTickLength);
        }
        if (this.stepMode.getValue().equals("NCP")) {
            if (this.targetHole == null || this.lastTarget == null) {
                return;
            }
            float yawRad = this.getRotationTo((Vec3d) this.lastTarget, (Vec3d) Holesnap.mc.player.getPositionVector()).x;
            double[] dir = PlayerUtil.forwardYaw(0.1, yawRad);
            Step.INSTANCE.doNCPStep(1);
        }
    }

    @SubscribeEvent
    public void onMove(HolesnapUtil event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        ++this.boostTime;
        if (this.disablenexttick && this.postTimer.getValue().booleanValue()) {
            this.posttimerticks = 0;
            if (Holesnap.mc.player.ticksExisted > 10) {
                this.timer.setTickLength(this.oldTickLength);
            }
            Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" + " " + ChatFormatting.RESET + ChatFormatting.GRAY + "[Holesnap] " + ChatFormatting.RESET + "Snapped into hole, disabling");
            this.disablenexttick = false;
            this.toggle();
            this.lastTarget = null;
            return;
        }
        HoleUtils.Hole doubleHole = HoleUtils.isDoubleHoleFr(PlayerUtil.getPlayerPos());
        if ((HoleUtils.isObbyHole(PlayerUtil.getPlayerPos()) || HoleUtils.isBedrockHoles(PlayerUtil.getPlayerPos()) || this.doubles.getValue().booleanValue() && doubleHole != null && (this.lastTarget == null || Holesnap.mc.player.getPositionVector().distanceTo(this.lastTarget) < 0.1 || this.targetHole.toTarget != null)) && (!this.selfHole.getValue().booleanValue() || !PlayerUtil.getPlayerPos().equals(this.startPos))) {
            if (this.postTimer.getValue().booleanValue() && !this.disablenexttick) {
                if (Holesnap.mc.player.ticksExisted > 10) {
                    this.timer.setTickLength(this.oldTickLength / 0.2f);
                }
                ++this.posttimerticks;
                if (this.posttimerticks >= 4) {
                    this.disablenexttick = true;
                }
                return;
            }
            this.posttimerticks = 0;
            if (Holesnap.mc.player.ticksExisted > 10) {
                this.timer.setTickLength(this.oldTickLength);
            }
            Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" + " " + ChatFormatting.RESET + ChatFormatting.GRAY + "[Holesnap] " + ChatFormatting.RESET + "Snapped into hole, disabling");
            this.posttimerticks = 0;
            this.disablenexttick = false;
            this.toggle();
            return;
        }
        if (this.targetHole != null && Holesnap.mc.world.getBlockState(this.targetHole.pos1).getBlock() == Blocks.AIR) {
            Vec3d targetPos;
            if (this.useTimer.getValue().booleanValue()) {
                if (this.vertFactor.getValue().booleanValue()) {
                    if (Holesnap.mc.player.ticksExisted > 10) {
                        this.timer.setTickLength((float) (50.0 / ((double) this.timerAmount.getValue().floatValue() / Holesnap.mc.player.getDistanceSqToCenter(this.targetHole.pos1))));
                    }
                } else if (Holesnap.mc.player.ticksExisted > 10) {
                    this.timer.setTickLength(this.oldTickLength / this.timerAmount.getValue().floatValue());
                }
            } else if (Holesnap.mc.player.ticksExisted > 10) {
                this.timer.setTickLength(this.oldTickLength);
            }
            if (this.physics.getValue().booleanValue() && !this.ranPhysics) {
                this.ranPhysics = true;
                for (int i = 0; i < this.physicsTicks.getValue().intValue(); ++i) {
                    HoleUtils.invokeMovementTick();
                }
            }
            this.cancel = true;
            Vec3d playerPos = Holesnap.mc.player.getPositionVector();
            if (this.targetHole.doubleHole) {
                if (this.targetHole.toTarget != null) {
                    targetPos = new Vec3d((double) this.targetHole.toTarget.getX() + 0.5, Holesnap.mc.player.posY, (double) this.targetHole.toTarget.getZ() + 0.5);
                } else {
                    BlockPos pos1 = this.targetHole.pos1;
                    BlockPos pos2 = this.targetHole.pos2;
                    double centerX = ((double) pos1.getX() + 0.5 + ((double) pos2.getX() + 0.5)) / 2.0;
                    double centerZ = ((double) pos1.getZ() + 0.5 + ((double) pos2.getZ() + 0.5)) / 2.0;
                    targetPos = new Vec3d(centerX, Holesnap.mc.player.posY, centerZ);
                }
            } else {
                targetPos = new Vec3d((double) this.targetHole.pos1.getX() + 0.5, Holesnap.mc.player.posY, (double) this.targetHole.pos1.getZ() + 0.5);
            }
            this.lastTarget = targetPos;
            switch (this.mode.getValue()) {
                case Normal: {
                    double yawRad = Math.toRadians(this.getRotationTo((Vec3d) playerPos, (Vec3d) targetPos).x);
                    double dist = playerPos.distanceTo(targetPos);
                    double speed = Holesnap.mc.player.onGround ? -Math.min(0.2805, dist / 2.0) : -PlayerUtil.getDefaultMoveSpeed() + 0.02;
                    event.x = -Math.sin(yawRad) * speed;
                    event.z = Math.cos(yawRad) * speed;
                    if (Holesnap.mc.player.collidedHorizontally && Holesnap.mc.player.onGround) {
                        ++this.stuckTicks;
                        if (!this.stepMode.getValue().equals("Vanilla")) break;
                        Holesnap.mc.player.stepHeight = Step.INSTANCE.stepHeight.getValue().floatValue();
                        break;
                    }
                    this.stuckTicks = 0;
                    Holesnap.mc.player.stepHeight = 0.5f;
                }
            }
        } else {
            if (this.useTimer.getValue().booleanValue() && Holesnap.mc.player.ticksExisted > 10) {
                this.timer.setTickLength(this.oldTickLength);
            }
            this.cancel = false;
            this.lastTarget = null;
            Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" + " " + ChatFormatting.RESET + ChatFormatting.GRAY + "[Holesnap] " + ChatFormatting.RESET + "Hole no longer exists, disabling");
            this.toggle();
        }
    }


    public double normalizeAngle(Double angleIn) {
        double angle = angleIn;
        if ((angle %= 360.0) >= 180.0) {
            angle -= 360.0;
        }
        if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }

    public Vec2f getRotationTo(Vec3d posTo, Vec3d posFrom) {
        return this.getRotationFromVec(posTo.subtract(posFrom));
    }

    public Vec2f getRotationFromVec(Vec3d vec) {
        double xz = Math.hypot(vec.x, vec.z);
        float yaw = (float) this.normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        float pitch = (float) this.normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, xz)));
        return new Vec2f(yaw, pitch);
    }

    public static boolean isObbyorBedrock(Block block) {
        return block == Blocks.OBSIDIAN || block == Blocks.BEDROCK;
    }
}
package opm.luftwaffe.api.util;

import java.util.ArrayList;
import java.util.List;

import opm.luftwaffe.mixin.mixins.access.IEntityLivingBase;
import opm.luftwaffe.mixin.mixins.access.IEntityPlayerSP;
import opm.luftwaffe.api.wrapper.IMinecraft;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class HoleUtils
        implements IMinecraft {

    public static void invokeMovementTick() {
        int lastSwing = ((IEntityLivingBase)HoleUtils.mc.player).getTicksSinceLastSwing();
        int useCount = ((IEntityLivingBase)HoleUtils.mc.player).getActiveItemStackUseCount();
        int hurtTime = HoleUtils.mc.player.hurtTime;
        float prevSwingProgress = HoleUtils.mc.player.prevSwingProgress;
        float swingProgress = HoleUtils.mc.player.swingProgress;
        int swingProgressInt = HoleUtils.mc.player.swingProgressInt;
        boolean isSwingInProgress = HoleUtils.mc.player.isSwingInProgress;
        float rotationYaw = HoleUtils.mc.player.rotationYaw;
        float prevRotationYaw = HoleUtils.mc.player.prevRotationYaw;
        float renderYawOffset = HoleUtils.mc.player.renderYawOffset;
        float prevRenderYawOffset = HoleUtils.mc.player.prevRenderYawOffset;
        float rotationYawHead = HoleUtils.mc.player.rotationYawHead;
        float prevRotationYawHead = HoleUtils.mc.player.prevRotationYawHead;
        float cameraYaw = HoleUtils.mc.player.cameraYaw;
        float prevCameraYaw = HoleUtils.mc.player.prevCameraYaw;
        float renderArmYaw = HoleUtils.mc.player.renderArmYaw;
        float prevRenderArmYaw = HoleUtils.mc.player.prevRenderArmYaw;
        float renderArmPitch = HoleUtils.mc.player.renderArmPitch;
        float prevRenderArmPitch = HoleUtils.mc.player.prevRenderArmPitch;
        float walk = HoleUtils.mc.player.distanceWalkedModified;
        float prevWalk = HoleUtils.mc.player.prevDistanceWalkedModified;
        double chasingPosX = HoleUtils.mc.player.chasingPosX;
        double prevChasingPosX = HoleUtils.mc.player.prevChasingPosX;
        double chasingPosY = HoleUtils.mc.player.chasingPosY;
        double prevChasingPosY = HoleUtils.mc.player.prevChasingPosY;
        double chasingPosZ = HoleUtils.mc.player.chasingPosZ;
        double prevChasingPosZ = HoleUtils.mc.player.prevChasingPosZ;
        float limbSwingAmount = HoleUtils.mc.player.limbSwingAmount;
        float prevLimbSwingAmount = HoleUtils.mc.player.prevLimbSwingAmount;
        float limbSwing = HoleUtils.mc.player.limbSwing;
        ((IEntityPlayerSP)HoleUtils.mc.player).superUpdate();
        ((IEntityLivingBase)HoleUtils.mc.player).setTicksSinceLastSwing(lastSwing);
        ((IEntityLivingBase)HoleUtils.mc.player).setActiveItemStackUseCount(useCount);
        HoleUtils.mc.player.hurtTime = hurtTime;
        HoleUtils.mc.player.prevSwingProgress = prevSwingProgress;
        HoleUtils.mc.player.swingProgress = swingProgress;
        HoleUtils.mc.player.swingProgressInt = swingProgressInt;
        HoleUtils.mc.player.isSwingInProgress = isSwingInProgress;
        HoleUtils.mc.player.rotationYaw = rotationYaw;
        HoleUtils.mc.player.prevRotationYaw = prevRotationYaw;
        HoleUtils.mc.player.renderYawOffset = renderYawOffset;
        HoleUtils.mc.player.prevRenderYawOffset = prevRenderYawOffset;
        HoleUtils.mc.player.rotationYawHead = rotationYawHead;
        HoleUtils.mc.player.prevRotationYawHead = prevRotationYawHead;
        HoleUtils.mc.player.cameraYaw = cameraYaw;
        HoleUtils.mc.player.prevCameraYaw = prevCameraYaw;
        HoleUtils.mc.player.renderArmYaw = renderArmYaw;
        HoleUtils.mc.player.prevRenderArmYaw = prevRenderArmYaw;
        HoleUtils.mc.player.renderArmPitch = renderArmPitch;
        HoleUtils.mc.player.prevRenderArmPitch = prevRenderArmPitch;
        HoleUtils.mc.player.distanceWalkedModified = walk;
        HoleUtils.mc.player.prevDistanceWalkedModified = prevWalk;
        HoleUtils.mc.player.chasingPosX = chasingPosX;
        HoleUtils.mc.player.prevChasingPosX = prevChasingPosX;
        HoleUtils.mc.player.chasingPosY = chasingPosY;
        HoleUtils.mc.player.prevChasingPosY = prevChasingPosY;
        HoleUtils.mc.player.chasingPosZ = chasingPosZ;
        HoleUtils.mc.player.prevChasingPosZ = prevChasingPosZ;
        HoleUtils.mc.player.limbSwingAmount = limbSwingAmount;
        HoleUtils.mc.player.prevLimbSwingAmount = prevLimbSwingAmount;
        HoleUtils.mc.player.limbSwing = limbSwing;
        ((IEntityPlayerSP)HoleUtils.mc.player).invokeOnUpdateWalkingPlayer();
    }

    public static BlockPos[] holeOffsets = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};

    public static boolean isHole(BlockPos pos) {
        boolean isHole = false;
        int amount = 0;
        for (BlockPos p : holeOffsets) {
            if (HoleUtils.mc.world.getBlockState(pos.add((Vec3i) p)).getMaterial().isReplaceable()) continue;
            ++amount;
        }
        if (amount == 5) {
            isHole = true;
        }
        return isHole;
    }

    public static boolean isObbyHole(BlockPos pos) {
        boolean isHole = true;
        int bedrock = 0;
        for (BlockPos off : holeOffsets) {
            Block b = HoleUtils.mc.world.getBlockState(pos.add((Vec3i) off)).getBlock();
            if (!HoleUtils.isSafeBlock(pos.add((Vec3i) off))) {
                isHole = false;
                continue;
            }
            if (b != Blocks.OBSIDIAN && b != Blocks.ENDER_CHEST && b != Blocks.ANVIL) continue;
            ++bedrock;
        }
        if (HoleUtils.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock() != Blocks.AIR || HoleUtils.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock() != Blocks.AIR) {
            isHole = false;
        }
        if (bedrock < 1) {
            isHole = false;
        }
        return isHole;
    }

    public static boolean isBedrockHoles(BlockPos pos) {
        boolean isHole = true;
        for (BlockPos off : holeOffsets) {
            Block b = HoleUtils.mc.world.getBlockState(pos.add((Vec3i) off)).getBlock();
            if (b == Blocks.BEDROCK) continue;
            isHole = false;
        }
        if (HoleUtils.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock() != Blocks.AIR || HoleUtils.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock() != Blocks.AIR) {
            isHole = false;
        }
        return isHole;
    }

    public static Hole isDoubleHole(BlockPos pos) {
        if (HoleUtils.checkOffset(pos, 1, 0)) {
            return new Hole(false, true, pos, pos.add(1, 0, 0));
        }
        if (HoleUtils.checkOffset(pos, 0, 1)) {
            return new Hole(false, true, pos, pos.add(0, 0, 1));
        }
        return null;
    }

    public static Hole isDoubleHoleFr(BlockPos pos) {
        if (HoleUtils.checkOffset(pos, 1, 0)) {
            return new Hole(false, true, pos, pos.add(1, 0, 0));
        }
        if (HoleUtils.checkOffset(pos, 0, 1)) {
            return new Hole(false, true, pos, pos.add(0, 0, 1));
        }
        if (HoleUtils.checkOffset(pos, -1, 0)) {
            return new Hole(false, true, pos, pos.add(-1, 0, 0));
        }
        if (HoleUtils.checkOffset(pos, 0, -1)) {
            return new Hole(false, true, pos, pos.add(0, 0, -1));
        }
        return null;
    }

    public static boolean checkOffset(BlockPos pos, int offX, int offZ) {
        return HoleUtils.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && HoleUtils.mc.world.getBlockState(pos.add(offX, 0, offZ)).getBlock() == Blocks.AIR && HoleUtils.isSafeBlock(pos.add(0, -1, 0)) && HoleUtils.isSafeBlock(pos.add(offX, -1, offZ)) && HoleUtils.isSafeBlock(pos.add(offX * 2, 0, offZ * 2)) && HoleUtils.isSafeBlock(pos.add(-offX, 0, -offZ)) && HoleUtils.isSafeBlock(pos.add(offZ, 0, offX)) && HoleUtils.isSafeBlock(pos.add(-offZ, 0, -offX)) && HoleUtils.isSafeBlock(pos.add(offX, 0, offZ).add(offZ, 0, offX)) && HoleUtils.isSafeBlock(pos.add(offX, 0, offZ).add(-offZ, 0, -offX));
    }

    static boolean isSafeBlock(BlockPos pos) {
        return HoleUtils.mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || HoleUtils.mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK || HoleUtils.mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST;
    }

    public static List<Hole> getHoles(double range, BlockPos playerPos, boolean doubles) {
        ArrayList<Hole> holes = new ArrayList<Hole>();
        List<BlockPos> circle = BlockUtils.getSphere(range, playerPos, true, false);
        for (BlockPos pos : circle) {
            Hole dh;
            if (HoleUtils.mc.world.getBlockState(pos).getBlock() != Blocks.AIR) continue;
            if (HoleUtils.isObbyHole(pos)) {
                holes.add(new Hole(false, false, pos));
                continue;
            }
            if (HoleUtils.isBedrockHoles(pos)) {
                holes.add(new Hole(true, false, pos));
                continue;
            }
            if (!doubles || (dh = HoleUtils.isDoubleHole(pos)) == null || HoleUtils.mc.world.getBlockState(dh.pos1.add(0, 1, 0)).getBlock() != Blocks.AIR && HoleUtils.mc.world.getBlockState(dh.pos2.add(0, 1, 0)).getBlock() != Blocks.AIR)
                continue;
            holes.add(dh);
        }
        return holes;
    }

    public static List<Hole> getHoles(double range, BlockPos playerPos, boolean doubles, boolean self) {
        ArrayList<Hole> holes = new ArrayList<Hole>();
        List<BlockPos> circle = BlockUtils.getSphere(range, playerPos, true, false);
        for (BlockPos pos : circle) {
            Hole dh;
            if (pos.equals(playerPos) && self || HoleUtils.mc.world.getBlockState(pos).getBlock() != Blocks.AIR)
                continue;
            if (HoleUtils.isObbyHole(pos)) {
                holes.add(new Hole(false, false, pos));
                continue;
            }
            if (HoleUtils.isBedrockHoles(pos)) {
                holes.add(new Hole(true, false, pos));
                continue;
            }
            if (!doubles || (dh = HoleUtils.isDoubleHole(pos)) == null || HoleUtils.mc.world.getBlockState(dh.pos1.add(0, 1, 0)).getBlock() != Blocks.AIR && HoleUtils.mc.world.getBlockState(dh.pos2.add(0, 1, 0)).getBlock() != Blocks.AIR)
                continue;
            holes.add(dh);
        }
        return holes;
    }

    public static List<Hole> getHolesHolesnap(double range, BlockPos playerPos, boolean doubles, boolean self) {
        ArrayList<Hole> holes = new ArrayList<Hole>();
        List<BlockPos> circle = BlockUtils.getSphere(range, playerPos, true, false);
        for (BlockPos pos : circle) {
            boolean flag2;
            Hole dh;
            if (pos.equals(playerPos) && self || HoleUtils.mc.world.getBlockState(pos).getBlock() != Blocks.AIR)
                continue;
            if (HoleUtils.isObbyHole(pos)) {
                holes.add(new Hole(false, false, pos));
                continue;
            }
            if (HoleUtils.isBedrockHoles(pos)) {
                holes.add(new Hole(true, false, pos));
                continue;
            }
            if (!doubles || (dh = HoleUtils.isDoubleHole(pos)) == null || dh.pos2.equals(playerPos) && self) continue;
            boolean flag = HoleUtils.mc.world.getBlockState(dh.pos1.add(0, 1, 0)).getBlock() == Blocks.AIR;
            boolean bl = flag2 = HoleUtils.mc.world.getBlockState(dh.pos2.add(0, 1, 0)).getBlock() == Blocks.AIR;
            if (!flag && !flag2) continue;
            if (!flag) {
                dh.toTarget = dh.pos2;
            } else if (!flag2) {
                dh.toTarget = dh.pos1;
            }
            holes.add(dh);
        }
        return holes;
    }

    public static double distanceTo(Hole hole) {
        Vec3d targetPos;
        if (hole.doubleHole) {
            if (hole.toTarget != null) {
                targetPos = new Vec3d((double) hole.toTarget.getX() + 0.5, HoleUtils.mc.player.posY, (double) hole.toTarget.getZ() + 0.5);
            } else {
                BlockPos pos1 = hole.pos1;
                BlockPos pos2 = hole.pos2;
                double centerX = ((double) pos1.getX() + 0.5 + ((double) pos2.getX() + 0.5)) / 2.0;
                double centerZ = ((double) pos1.getZ() + 0.5 + ((double) pos2.getZ() + 0.5)) / 2.0;
                targetPos = new Vec3d(centerX, HoleUtils.mc.player.posY, centerZ);
            }
        } else {
            targetPos = new Vec3d((double) hole.pos1.getX() + 0.5, HoleUtils.mc.player.posY, (double) hole.pos1.getZ() + 0.5);
        }
        return HoleUtils.mc.player.getPositionVector().distanceTo(targetPos);
    }

    public static class Hole {
        public boolean bedrock;
        public boolean doubleHole;
        public BlockPos pos1;
        public BlockPos pos2;
        public BlockPos toTarget;

        public Hole(boolean bedrock, boolean doubleHole, BlockPos pos1, BlockPos pos2) {
            this.bedrock = bedrock;
            this.doubleHole = doubleHole;
            this.pos1 = pos1;
            this.pos2 = pos2;
        }

        public Hole(boolean bedrock, boolean doubleHole, BlockPos pos1, BlockPos pos2, BlockPos toTarget) {
            this.bedrock = bedrock;
            this.doubleHole = doubleHole;
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.toTarget = toTarget;
        }

        public Hole(boolean bedrock, boolean doubleHole, BlockPos pos1) {
            this.bedrock = bedrock;
            this.doubleHole = doubleHole;
            this.pos1 = pos1;
        }
    }
}
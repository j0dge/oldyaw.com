package opm.luftwaffe.api.util;

import java.util.Comparator;

import net.minecraft.client.Minecraft;


public class TargetUtils {
    private static Minecraft mc;

        public static HoleUtils.Hole getTargetHole(double targetRange) {
            return HoleUtils.getHoles(targetRange, PlayerUtil.getPlayerPos(), false).stream().filter(hole -> TargetUtils.mc.player.getDistanceSq(hole.pos1) <= targetRange).min(Comparator.comparingDouble(hole -> TargetUtils.mc.player.getDistanceSq(hole.pos1))).orElse(null);
        }

        public static HoleUtils.Hole getTargetHoleVec3D(double targetRange, boolean self, boolean doubles) {
            return HoleUtils.getHolesHolesnap(targetRange, PlayerUtil.getPlayerPos(), doubles, self).stream().filter(hole -> HoleUtils.distanceTo(hole) <= targetRange).min(Comparator.comparingDouble(HoleUtils::distanceTo)).orElse(null);
        }
    }

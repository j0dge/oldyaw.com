package opm.luftwaffe.api.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BlockUtils {
    public static List<BlockPos> tickCache = new ArrayList<BlockPos>();
    private static Minecraft mc;

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        tickCache = new ArrayList<BlockPos>();
    }

    public static double getDistanceSq(BlockPos pos) {
        return BlockUtils.getDistanceSq((Entity)BlockUtils.mc.player, pos);
    }

    public static double getDistanceSq(Entity from, BlockPos to) {
        return from.getDistanceSqToCenter(to);
    }

    public BlockUtils() {
        MinecraftForge.EVENT_BUS.register(this);
    }


    public static List<BlockPos> getSphere(double range, BlockPos pos, boolean sphere, boolean hollow) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        int x = cx - (int)range;
        while ((double)x <= (double)cx + range) {
            int z = cz - (int)range;
            while ((double)z <= (double)cz + range) {
                int y = sphere ? cy - (int)range : cy;
                while (true) {
                    double d = y;
                    double d2 = sphere ? (double)cy + range : (double)cy + range;
                    if (!(d < d2)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (!(!(dist < range * range) || hollow && dist < (range - 1.0) * (range - 1.0))) {
                        BlockPos l = new BlockPos(x, y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }
}

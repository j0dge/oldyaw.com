package opm.luftwaffe.features.modules.misc;

import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LagBackDetector extends Module {
    private static LagBackDetector INSTANCE = new LagBackDetector();
    private static final Minecraft mc = Minecraft.getMinecraft();
    private Vec3d lastPosition = Vec3d.ZERO;
    private int ticksStill = 0;
    private boolean wasMoving = false;

    public LagBackDetector() {
        super("LagBackDetector", "Displays current arrow type", Category.CLIENT, true, false, false);
        INSTANCE = this;
    }

    public static LagBackDetector getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LagBackDetector();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        lastPosition = mc.player.getPositionVector();
        ticksStill = 0;
        wasMoving = false;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null || mc.world == null) return;

        Vec3d currentPos = mc.player.getPositionVector();
        boolean isMoving = !currentPos.equals(lastPosition);

        if (isMoving) {
            wasMoving = true;
            ticksStill = 0;
        } else if (wasMoving) {
            ticksStill++;
            // Если игрок был в движении, но внезапно остановился (возможно LagBack)
            if (ticksStill >= 2) { // 2 тика для надежности
                checkLagBack(currentPos);
            }
        }

        lastPosition = currentPos;
    }

    private void checkLagBack(Vec3d currentPos) {
        // Проверяем, был ли реальный RubberBand (резкое возвращение назад)
        Vec3d predictedNextPos = lastPosition.add(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
        double distance = currentPos.distanceTo(predictedNextPos);

        if (distance > 0.5) { // Пороговое значение
            Command.sendMessage("\"\" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + \"luftwaffe\" + ChatFormatting.GRAY + ChatFormatting.BOLD + \".xyz\" +  \" \" + ChatFormatting.DARK_GREEN + \"Detected Lagback\": " + String.format("%.2f", distance));
            wasMoving = false;
            ticksStill = 0;
        }
    }
}
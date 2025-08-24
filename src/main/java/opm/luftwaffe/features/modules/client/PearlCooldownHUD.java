package opm.luftwaffe.features.modules.client;

import opm.luftwaffe.features.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PearlCooldownHUD extends Module {
    private static PearlCooldownHUD INSTANCE = new PearlCooldownHUD();
    private static long lastPearlThrowTime = 0;
    private static final int COOLDOWN_DURATION = 15 * 20; // 15 секунд в тиках (20 тиков/сек)
    public PearlCooldownHUD() {super("PearlCooldownHUD", "john2016 sucks", Module.Category.PLAYER, true, false, false);}

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player == Minecraft.getMinecraft().player) {
            EntityPlayer player = event.player;
            if (player.getCooledAttackStrength(0) < 1.0F) {
                lastPearlThrowTime = player.world.getTotalWorldTime();
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

        long currentTime = Minecraft.getMinecraft().world.getTotalWorldTime();
        long timeSinceThrow = currentTime - lastPearlThrowTime;

        if (timeSinceThrow < COOLDOWN_DURATION) {
            float secondsLeft = (COOLDOWN_DURATION - timeSinceThrow) / 20.0F;
            String text = String.format("Pearl cooldown: %.1fs", secondsLeft);

            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer fontRenderer = mc.fontRenderer;
            ScaledResolution scaledResolution = new ScaledResolution(mc);

            int x = scaledResolution.getScaledWidth() / 2;
            int y = scaledResolution.getScaledHeight() - 50;

            fontRenderer.drawStringWithShadow(text, x - fontRenderer.getStringWidth(text) / 2, y, 0xFFFFFF);
        }
    }
}
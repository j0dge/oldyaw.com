package opm.luftwaffe.features.modules.HUD;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.setting.Setting;

public class HealthIndicator extends Module {
    private final Setting<Boolean> showHeart = this.register(new Setting<>("ShowHeart", true, "Shows heart symbol next to health"));
    private final Setting<Boolean> showShadow = this.register(new Setting<>("ShowShadow", true, "Shows shadow behind health text"));
    String heartsymbol = "❤";
    public HealthIndicator () {
        super("HealthIndicator", "Displays your health in the HUD", Category.HUD, true, false, false);
    }
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;

        int screenWidth = event.getResolution().getScaledWidth();
        int screenHeight = event.getResolution().getScaledHeight();

        int health = (int) mc.player.getHealth() + (int) mc.player.getAbsorptionAmount();

        String healthText = health +" "+heartsymbol;
        if (!showHeart.getValue()) {
            healthText = String.valueOf(health);
        }

        int textWidth = mc.fontRenderer.getStringWidth(healthText);
        int x = (screenWidth - textWidth) / 2 + 1;
        int y = (screenHeight / 2) + 10;

        int color;
        if (health > 12 ) {
            color = 0x00FF00;
        } else {
            color = 0xFF0000;
        }

        if (showShadow.getValue() == true) {
            mc.fontRenderer.drawStringWithShadow(healthText, x, y, color);
        } else {
            mc.fontRenderer.drawString(healthText, x, y, color);
        }
    }
}

//idk what the fuck am i doin but it works didnt use chatgpt 4 this one LOL!
package opm.luftwaffe.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.setting.Setting;
import opm.luftwaffe.features.modules.client.ClickGui;
import opm.luftwaffe.features.command.Command;

public class ColorSync extends Module {
    Setting<Boolean> future = this.register(new Setting("Future", true));
    public Setting<String> prefixFuture = this.register(new Setting("Prefix", ",", (v) -> {
        return (Boolean)this.future.getValue();
    }));

    public ColorSync() {
        super("ColorSync", "Sync client color to other ones", Module.Category.CLIENT, true, false, false);
    }

    public void onEnable() {
        int red = (Integer)ClickGui.getInstance().red.getValue();
        int green = (Integer)ClickGui.getInstance().green.getValue();
        int blue = (Integer)ClickGui.getInstance().blue.getValue();
        float[] hsl = this.calculateHSL(red, green, blue);
        float hue = hsl[0];
        float saturation = hsl[1];
        float lightness = hsl[2];
        mc.player.sendChatMessage((String)this.prefixFuture.getPlannedValue() + "colors hue " + hue);
        mc.player.sendChatMessage((String)this.prefixFuture.getPlannedValue() + "colors saturation " + saturation);
        mc.player.sendChatMessage((String)this.prefixFuture.getPlannedValue() + "colors lightness " + lightness);
        Command.sendMessage(ChatFormatting.GRAY + "Colors have been synced to Future");
        this.disable();
    }

    private float[] calculateHSL(int r, int g, int b) {
        float rf = (float)r / 255.0F;
        float gf = (float)g / 255.0F;
        float bf = (float)b / 255.0F;
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float h = 0.0F;
        float l = (max + min) / 2.0F;
        float s;
        float d;
        if (max == min) {
            s = 0.0F;
            h = 0.0F;
        } else {
            d = max - min;
            s = (double)l > 0.5D ? d / (2.0F - max - min) : d / (max + min);
            if (max == rf) {
                h = (gf - bf) / d + (float)(gf < bf ? 6 : 0);
            } else if (max == gf) {
                h = (bf - rf) / d + 2.0F;
            } else if (max == bf) {
                h = (rf - gf) / d + 4.0F;
            }

            h /= 6.0F;
        }

        d = h * 360.0F;
        float saturation = s * 100.0F;
        float lightness = l * 100.0F;
        return new float[]{d, saturation, lightness};
    }
}
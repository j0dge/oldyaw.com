package opm.luftwaffe.features.modules.HUD;

import net.minecraft.entity.EntityLivingBase;
import opm.luftwaffe.api.util.ColorUtil;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.modules.client.ClickGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import opm.luftwaffe.features.setting.Setting;

import static opm.luftwaffe.features.gui.components.Component.counter1;

public class Welcomer extends Module {
    private static Welcomer INSTANCE = new Welcomer();
    String hiMsg = "Wassup ";
    String nameMsg = "%name%";
    String welcomeMsg = " welcome to luftwaffe :')";
    int white = 0xFFFFFFFF;

    public enum modes {Custom, Default}

    Setting<modes> mode = register(new Setting("ColorScheme", modes.Default));
    public Setting<String> text = register(new Setting("Text", "Welcome to luftwaffe, %name%"));
    public final Setting<Integer> y = this.register(new Setting<Integer>("Y-Pos", 3, 0, 1000));

    public Welcomer() {
        super("Welcomer", "Welcomer", Category.HUD, true, false, false);
    }

    public static Welcomer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Welcomer();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private int centerX(String huy) {
        return (this.renderer.scaledWidth - this.renderer.getStringWidth(huy)) / 2;
    }

    private String text2() {
        String text2 = text.getValue();
        text2 = text2.replace("%name%", mc.player.getDisplayNameString());
        nameMsg = nameMsg.replace("%name%", mc.player.getDisplayNameString());
        return text2;
    }
    private String nameMsg2() {
        String nameMsg2 = nameMsg;
        nameMsg2 = nameMsg2.replace("%name%", mc.player.getDisplayNameString());
        return nameMsg2;
    }


    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (mode.getValue() == modes.Default) {
            if (ClickGui.getInstance().rainbow.getValue() == false) {
                String fullMessage = hiMsg + nameMsg2() + welcomeMsg;
                int messageWidth = this.renderer.getStringWidth(fullMessage);
                int startX = (this.renderer.scaledWidth - messageWidth) / 2;
                this.renderer.drawString(hiMsg, startX, y.getValue(), ClickGui.getInstance().syncColor(), true);
                this.renderer.drawString(nameMsg2(), startX + this.renderer.getStringWidth(hiMsg), y.getValue(), white, true);
                this.renderer.drawString(welcomeMsg, startX + this.renderer.getStringWidth(hiMsg) + this.renderer.getStringWidth(nameMsg2()), y.getValue(), ClickGui.getInstance().syncColor(), true);
            } else {
                String fullMessage = hiMsg + nameMsg2() + welcomeMsg;
                int messageWidth = this.renderer.getStringWidth(fullMessage);
                int startX = (this.renderer.scaledWidth - messageWidth) / 2;
                this.renderer.drawString(hiMsg, startX, y.getValue(), ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                this.renderer.drawString(nameMsg2(), startX + this.renderer.getStringWidth(hiMsg), y.getValue(), white, true);
                this.renderer.drawString(welcomeMsg, startX + this.renderer.getStringWidth(hiMsg) + this.renderer.getStringWidth(nameMsg2()), y.getValue(), ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
            }
        }
        if (mode.getValue() == modes.Custom) {
            if (ClickGui.getInstance().rainbow.getValue() == false) {
                this.renderer.drawString(text2(), centerX(text.getValue()), y.getValue(), ClickGui.getInstance().syncColor(), true);
            } else {
                this.renderer.drawString(text2(), centerX(text.getValue()), y.getValue(), ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
            }
        }
    }
}
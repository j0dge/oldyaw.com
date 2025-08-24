package opm.luftwaffe.features.modules.HUD;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Iterator;
import opm.luftwaffe.Luftwaffe;
import opm.luftwaffe.api.event.events.Render2DEvent;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.modules.client.ClickGui;
import opm.luftwaffe.features.setting.Setting;
import opm.luftwaffe.api.util.ColorUtil;
import net.minecraft.entity.player.EntityPlayer;

public class TextRadar extends Module {
    Setting<Integer> maxAmount = this.register(new Setting("MaxAmount", 10, 1, 100));
    Setting<Integer> offset = this.register(new Setting("Offset", 5, 0, 50));

    public TextRadar() {
        super("TextRadar", "Shows players in render distance on hud", Category.HUD, true, false, false);
    }

    public void onRender2D(Render2DEvent event) {
        int i = 0;
        Iterator var3 = mc.world.loadedEntityList.iterator();

        while(true) {
            while(true) {
                Object o;
                do {
                    do {
                        if (!var3.hasNext()) {
                            return;
                        }

                        o = var3.next();
                    } while(!(o instanceof EntityPlayer));
                } while(o == mc.player);

                ++i;
                if (i > (Integer)this.maxAmount.getValue()) {
                    return;
                }

                int[] counter1 = new int[]{1};
                EntityPlayer entity = (EntityPlayer)o;
                float health = (float)(Math.round(entity.getHealth()) + Math.round(entity.getAbsorptionAmount()));
                DecimalFormat dfDistance = new DecimalFormat("#.#");
                dfDistance.setRoundingMode(RoundingMode.CEILING);
                StringBuilder distanceSB = new StringBuilder();
                int color = ColorUtil.toRGBA((Integer)ClickGui.getInstance().red.getValue(), (Integer)ClickGui.getInstance().green.getValue(), (Integer)ClickGui.getInstance().blue.getValue());
                String health_str = "" + health;
                health_str = health_str.replace(".0", "");
                int distanceInt = (int)mc.player.getDistance(entity);
                String distance = dfDistance.format((long)distanceInt);
                if (distanceInt >= 35) {
                    distanceSB.append(ChatFormatting.GREEN);
                } else if (distanceInt > 10) {
                    distanceSB.append(ChatFormatting.GOLD);
                } else {
                    distanceSB.append(ChatFormatting.RED);
                }

                distanceSB.append(distance);
                String heal;
                if ((double)health >= 12.0D) {
                    heal = " " + ChatFormatting.GREEN + health_str + "";
                } else if ((double)health >= 5.0D) {
                    heal = " " + ChatFormatting.YELLOW + health_str + "";
                } else {
                    heal = " " + ChatFormatting.RED + health_str + "";
                }

                String name = entity.getDisplayName().getFormattedText();
                String str = heal + " " + ChatFormatting.RESET;
                int var10002;
                if (Luftwaffe.friendManager.isFriend(entity.getName())) {
                    Luftwaffe.textManager.drawString(str + ChatFormatting.AQUA + name + " " + distanceSB + ChatFormatting.WHITE, -2.0F, (float)((Integer)this.offset.getValue() + i * 10), color, true);
                    var10002 = counter1[0]++;
                } else if ((Boolean)ClickGui.getInstance().rainbow.getValue() && ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    Luftwaffe.textManager.drawString(str + name + " " + distanceSB + ChatFormatting.GRAY + "", -2.0F, (float)((Integer)this.offset.getValue() + i * 10), ColorUtil.rainbow(counter1[0] * (Integer)ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    var10002 = counter1[0]++;
                } else {
                    Luftwaffe.textManager.drawString(str + name + " " + distanceSB + ChatFormatting.GRAY + "", -2.0F, (float)((Integer)this.offset.getValue() + i * 10), color, true);
                    var10002 = counter1[0]++;
                }
            }
        }
    }
}
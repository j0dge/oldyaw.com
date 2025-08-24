package opm.luftwaffe.features.modules.HUD;

import opm.luftwaffe.api.event.events.Render2DEvent;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.modules.client.ClickGui;
import opm.luftwaffe.features.setting.Setting;
import opm.luftwaffe.api.util.ColorUtil;
import opm.luftwaffe.api.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

public class ArmorHUD extends Module {
    Setting<Boolean> text = this.register(new Setting("Text", true));
    Setting<Boolean> percent = this.register(new Setting("Percent", false, (v) -> {
        return (Boolean)this.text.getValue();
    }));
    Setting<Boolean> sync = this.register(new Setting("Sync", true, (v) -> {
        return (Boolean)this.text.getValue();
    }));
    private Setting<Boolean> reversed = this.register(new Setting("Reversed", false));

    public ArmorHUD() {
        super("ArmorHUD", "shows your armour", Module.Category.HUD, true, false, false);
    }

    public void onRender2D(Render2DEvent event) {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        GlStateManager.pushMatrix();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - (mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
        int armorCount = mc.player.inventory.armorInventory.size();
        int index;
        ItemStack is;
        int x;
        String s;
        float green;
        float red;
        int dmg;
        String durability;
        int color;
        if ((Boolean)this.reversed.getValue()) {
            for(index = armorCount - 1; index >= 0; --index) {
                is = (ItemStack)mc.player.inventory.armorInventory.get(index);
                ++iteration;
                if (!is.isEmpty()) {
                    x = i - 90 + (9 - iteration) * 20 + 2;
                    GlStateManager.enableTexture2D();
                    RenderUtil.itemRender.zLevel = 200.0F;
                    RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
                    RenderUtil.itemRender.renderItemOverlays(mc.fontRenderer, is, x, y);
                    RenderUtil.itemRender.zLevel = 0.0F;
                    GlStateManager.popMatrix();
                    GlStateManager.disableDepth();
                    GlStateManager.enableBlend();
                    s = is.getCount() > 1 ? is.getCount() + "" : "";
                    this.renderer.drawStringWithShadow(s, (float)(x + 19 - 2 - this.renderer.getStringWidth(s)), (float)(y + 9), 16777215);
                    green = ((float)is.getMaxDamage() - (float)is.getItemDamage()) / (float)is.getMaxDamage();
                    red = 1.0F - green;
                    dmg = 100 - (int)(red * 100.0F);
                    durability = (Boolean)this.percent.getValue() && dmg < 100 ? dmg + "%" : dmg + "";
                    color = !(Boolean)this.sync.getValue() ? ColorUtil.toRGBA((int)(red * 255.0F), (int)(green * 255.0F), 0) : ((Boolean)ClickGui.getInstance().rainbow.getValue() ? ColorUtil.toRGBA(ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue())) : ColorUtil.toRGBA((Integer)ClickGui.getInstance().red.getValue(), (Integer)ClickGui.getInstance().green.getValue(), (Integer)ClickGui.getInstance().blue.getValue()));
                    if ((Boolean)this.text.getValue()) {
                        this.renderer.drawStringWithShadow(durability, (float)(x + 8 - this.renderer.getStringWidth(durability) / 2), (float)(y - 11), color);
                    }
                }
            }
        } else {
            for(index = 0; index < armorCount; ++index) {
                is = (ItemStack)mc.player.inventory.armorInventory.get(index);
                ++iteration;
                if (!is.isEmpty()) {
                    x = i - 90 + (9 - iteration) * 20 + 2;
                    GlStateManager.enableTexture2D();
                    RenderUtil.itemRender.zLevel = 200.0F;
                    RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
                    RenderUtil.itemRender.renderItemOverlays(mc.fontRenderer, is, x, y);
                    RenderUtil.itemRender.zLevel = 0.0F;
                    GlStateManager.popMatrix();
                    GlStateManager.disableDepth();
                    GlStateManager.enableBlend();
                    s = is.getCount() > 1 ? is.getCount() + "" : "";
                    this.renderer.drawStringWithShadow(s, (float)(x + 19 - 2 - this.renderer.getStringWidth(s)), (float)(y + 9), 16777215);
                    green = ((float)is.getMaxDamage() - (float)is.getItemDamage()) / (float)is.getMaxDamage();
                    red = 1.0F - green;
                    dmg = 100 - (int)(red * 100.0F);
                    durability = (Boolean)this.percent.getValue() && dmg < 100 ? dmg + "%" : dmg + "";
                    color = !(Boolean)this.sync.getValue()
                            ? ColorUtil.toRGBA((int)(255 * (1 - green)), (int)(255 * green), 0)
                            : ((Boolean)ClickGui.getInstance().rainbow.getValue()
                            ? ColorUtil.toRGBA(ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue()))
                            : ColorUtil.toRGBA((Integer)ClickGui.getInstance().red.getValue(),
                            (Integer)ClickGui.getInstance().green.getValue(),
                            (Integer)ClickGui.getInstance().blue.getValue()));
                    if ((Boolean)this.text.getValue()) {
                        this.renderer.drawStringWithShadow(durability, (float)(x + 8 - this.renderer.getStringWidth(durability) / 2), (float)(y - 11), color);
                    }
                }
            }
        }
    }
}
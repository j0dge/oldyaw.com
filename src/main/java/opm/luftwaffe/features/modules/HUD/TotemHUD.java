package opm.luftwaffe.features.modules.HUD;

import opm.luftwaffe.api.event.events.Render2DEvent;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.modules.client.ClickGui;
import opm.luftwaffe.features.setting.Setting;
import opm.luftwaffe.api.util.ColorUtil;
import opm.luftwaffe.api.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TotemHUD extends Module {
    private static final ItemStack totem;
    public Setting<Boolean> show0 = this.register(new Setting("Show0", true));
    Setting<Boolean> sync = this.register(new Setting("Sync", true));

    public TotemHUD() {
        super("TotemHUD", "shows how many totems you have", Module.Category.HUD, true, false, false);
    }

    public void onRender2D(Render2DEvent event) {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int color = !(Boolean)this.sync.getValue() ? 16777215 : ((Boolean)ClickGui.getInstance().rainbow.getValue() ? ColorUtil.toRGBA(ColorUtil.rainbow((Integer)ClickGui.getInstance().rainbowHue.getValue())) : ColorUtil.toRGBA((Integer)ClickGui.getInstance().red.getValue(), (Integer)ClickGui.getInstance().green.getValue(), (Integer)ClickGui.getInstance().blue.getValue()));
        int totems = mc.player.inventory.mainInventory.stream().filter((itemStack) -> {
            return itemStack.getItem() == Items.TOTEM_OF_UNDYING;
        }).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totems += mc.player.getHeldItemOffhand().getCount();
        }

        int i;
        int y;
        int x;
        if (totems > 0) {
            GlStateManager.pushMatrix();
            i = width / 2;
            y = height - 55 - (mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            x = i - 189 + 180 + 2;
            GlStateManager.enableTexture2D();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, x, y);
            RenderUtil.itemRender.renderItemOverlays(mc.fontRenderer, totem, x, y);
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.popMatrix();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            this.renderer.drawStringWithShadow(totems + "", (float)(x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (float)(y + 9), color);
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
        }

        if (totems == 0) {
            GlStateManager.pushMatrix();
            i = width / 2;
            y = height - 55 - (mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            x = i - 189 + 180 + 2;
            GlStateManager.enableTexture2D();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, x, y);
            RenderUtil.itemRender.renderItemOverlays(mc.fontRenderer, totem, x, y);
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.popMatrix();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            if ((Boolean)this.show0.getValue()) {
                this.renderer.drawStringWithShadow(totems + "", (float)(x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (float)(y + 9), color);
            }

            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
        }
    }

    static {
        totem = new ItemStack(Items.TOTEM_OF_UNDYING);
    }
}
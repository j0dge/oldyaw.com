package opm.luftwaffe.mixin.mixins;

import opm.luftwaffe.Luftwaffe;
import opm.luftwaffe.features.modules.render.Background;
import opm.luftwaffe.api.util.ColorUtil;
import opm.luftwaffe.api.util.RenderUtil;
import opm.luftwaffe.features.modules.misc.ToolTips;
import opm.luftwaffe.api.util.Util;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {GuiScreen.class})
public class MixinGuiScreen
        extends Gui {
    @Inject(method = {"renderToolTip"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void renderToolTipHook(ItemStack stack, int x, int y, CallbackInfo info) {
        if (ToolTips.getInstance().isOn() && stack.getItem() instanceof ItemShulkerBox) {
            ToolTips.getInstance().renderShulkerToolTip(stack, x, y, null);
            info.cancel();
        }
    }

    @Inject(method = {"drawDefaultBackground"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void drawDefaultBackgroundHook(CallbackInfo info) {
        if (Background.getINSTANCE().isOn() && Util.mc.world != null) {
            if (Background.getINSTANCE().gradient.getValue()) {
                RenderUtil.drawGradientRect(0, 0, Luftwaffe.textManager.scaledWidth, Luftwaffe.textManager.scaledHeight + 1, ColorUtil.toRGBA(Background.getINSTANCE().red.getValue(), Background.getINSTANCE().green.getValue(), Background.getINSTANCE().blue.getValue(), Background.getINSTANCE().alpha.getValue()), ColorUtil.toRGBA(Background.getINSTANCE().red2.getValue(), Background.getINSTANCE().green2.getValue(), Background.getINSTANCE().blue2.getValue(), Background.getINSTANCE().alpha2.getValue()), true);
            }
            if (!Background.getINSTANCE().vanilla.getValue()) info.cancel();
        }
    }
}
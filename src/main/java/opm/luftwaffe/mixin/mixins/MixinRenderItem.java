package opm.luftwaffe.mixin.mixins;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import opm.luftwaffe.features.modules.render.ViewModel;

@Mixin(value={RenderItem.class})
public class MixinRenderItem {

    @Inject(method = {"renderItemModel"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", shift = At.Shift.BEFORE)})
    private void renderItemModel(ItemStack stack, IBakedModel bakedModel, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
        if (ViewModel.getINSTANCE().isOn()) {
            GlStateManager.scale(ViewModel.getINSTANCE().X.getValue(), ViewModel.getINSTANCE().Y.getValue(), ViewModel.getINSTANCE().Z.getValue());
        }
    }
    @Inject(method = {"rotateArm"}, at = { @At("HEAD") }, cancellable = true)
    public void rotateArm(final float p_187458_1_, final CallbackInfo info) {
        if (ViewModel.getINSTANCE().nosway.getValue()) {
            info.cancel();
        }
    }
}
package opm.luftwaffe.mixin.mixins;

import opm.luftwaffe.features.modules.render.Animations;
import opm.luftwaffe.features.command.Command;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase {
    @Inject(method = "getArmSwingAnimationEnd",at = @At(value = "HEAD"), cancellable = true)
    public void getArmSwingAnimationEndHook(CallbackInfoReturnable<Integer> info) {
        int i = Animations.getINSTANCE().isOn() ? (int) Math.floor(6 / Animations.getINSTANCE().swingspeed.getValue()) : 6;
        info.setReturnValue(i);
    }
}
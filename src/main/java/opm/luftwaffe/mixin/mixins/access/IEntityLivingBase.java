package opm.luftwaffe.mixin.mixins.access;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={EntityLivingBase.class})
public interface IEntityLivingBase {
    @Accessor(value="ticksSinceLastSwing")
    public int getTicksSinceLastSwing();

    @Accessor(value="activeItemStackUseCount")
    public int getActiveItemStackUseCount();

    @Accessor(value="ticksSinceLastSwing")
    public void setTicksSinceLastSwing(int var1);

    @Accessor(value="activeItemStackUseCount")
    public void setActiveItemStackUseCount(int var1);
}
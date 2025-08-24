package opm.luftwaffe.mixin.mixins.access;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={Minecraft.class})
public interface IMinecraft {
    @Accessor(value="timer")
    public Timer getTimer();

    @Accessor(value="rightClickDelayTimer")
    public void setRightClickDelayTimer(int var1);

    @Invoker(value="rightClickMouse")
    public void invokeRightClick();

    @Accessor(value="rightClickDelayTimer")
    public int getRightClickDelayTimer();
}
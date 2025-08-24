package opm.luftwaffe.api.util;

import net.minecraft.client.Minecraft;

public interface Wrapper {
    public static final Minecraft mc = Minecraft.getMinecraft();

    default public boolean nullCheck() {
        return Wrapper.mc.player != null && Wrapper.mc.world != null;
    }
}
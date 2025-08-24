package opm.luftwaffe.mixin;

import opm.luftwaffe.Luftwaffe;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class LuftwaffeLoader
        implements IFMLLoadingPlugin {
    private static boolean isObfuscatedEnvironment = false;

    public LuftwaffeLoader() {
        Luftwaffe.LOGGER.info("\n\nLoading mixins by luftwaffe");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.luftwaffe.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        Luftwaffe.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    public String getAccessTransformerClass() {
        return null;
    }
}


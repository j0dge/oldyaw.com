package opm.luftwaffe.features.modules.render;

import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.setting.Setting;

public class ViewModel extends Module {
    private static ViewModel INSTANCE = new ViewModel();

    public final Setting<Boolean> nosway = this.register(new Setting<Boolean>("NoSway", false));
    public Setting<Float> X = this.register(new Setting<Float>("X-Scale", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(2.0f)));
    public Setting<Float> Y = this.register(new Setting<Float>("Y-Scale", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(2.0f)));
    public Setting<Float> Z = this.register(new Setting<Float>("Z-Scale", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(2.0f)));

    public ViewModel() {
        super("ViewModel", "Hands customization(unfinished", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static ViewModel getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ViewModel();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
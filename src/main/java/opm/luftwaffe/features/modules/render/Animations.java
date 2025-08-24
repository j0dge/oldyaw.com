package opm.luftwaffe.features.modules.render;

import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.setting.Setting;

import static opm.luftwaffe.api.wrapper.IMinecraft.mc;

public class Animations extends Module {
    private static Animations INSTANCE = new Animations();

    private final Setting<Boolean> oldswing = this.register(new Setting<Boolean>("Old Swing", false));
    public Setting<Float> swingspeed = register(new Setting("Swing Speed", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(1.0f)));
    public final Setting<Float> size = this.register(new Setting<Float>("C-Size", 1.0f, 0.0f, 1.0f));
    public final Setting<Float> speed = this.register(new Setting<Float>("C-Speed", 1.0f, 0.0f, 8.0f));
    public final Setting<Float> oscillate = this.register(new Setting<Float>("C-Oscillate", 1.0f, 0.0f, 3.0f));

    public Animations() {
        super("Animations", "", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public void onUpdate() {
        if (oldswing.getValue() && mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
        }
        if (oldswing.getValue() && mc.entityRenderer.itemRenderer.prevEquippedProgressOffHand >= 0.9) {
            mc.entityRenderer.itemRenderer.equippedProgressOffHand = 1.0f;
            mc.entityRenderer.itemRenderer.itemStackOffHand = mc.player.getHeldItemOffhand();
        }
    }

    public static Animations getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new Animations();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public String getDisplayInfo() {
        return String.valueOf(oldswing.getValue());
    }
}
package opm.luftwaffe.features.gui.components.items.buttons;

import opm.luftwaffe.Luftwaffe;
import opm.luftwaffe.features.gui.LuftwaffeGui;
import opm.luftwaffe.features.gui.components.Component;
import opm.luftwaffe.features.gui.components.items.Item;
import opm.luftwaffe.features.modules.client.ClickGui;
import opm.luftwaffe.api.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Button
        extends Item {
    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean enabled = this.getState();
        boolean showBg = ClickGui.getInstance().moduleButtonBackground.getValue();

        if (enabled && showBg) {
            RenderUtil.drawRect(
                    this.x, this.y,
                    this.x + (float) this.width, this.y + (float) this.height - 0.5f,
                    !this.isHovering(mouseX, mouseY)
                            ? Luftwaffe.colorManager.getColorWithAlpha(Luftwaffe.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue())
                            : Luftwaffe.colorManager.getColorWithAlpha(Luftwaffe.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue())
            );
        }

        Luftwaffe.textManager.drawStringWithShadow(
                this.getName(),
                this.x + 2.3f,
                this.y - 2.0f - (float) LuftwaffeGui.getClickGui().getTextOffset(),
                enabled ? -1 : -5592406
        );
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }
    }

    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public void toggle() {
    }

    public boolean getState() {
        return this.state;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : LuftwaffeGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float) mouseX >= this.getX() && (float) mouseX <= this.getX() + (float) this.getWidth() && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }
}


package opm.luftwaffe.features.gui;

import opm.luftwaffe.Luftwaffe;
import opm.luftwaffe.features.Feature;
import opm.luftwaffe.features.gui.components.Component;
import opm.luftwaffe.features.gui.components.items.Item;
import opm.luftwaffe.features.gui.components.items.buttons.ModuleButton;
import opm.luftwaffe.features.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class LuftwaffeGui
        extends GuiScreen {
    private static LuftwaffeGui LuftwaffeGui;
    private static LuftwaffeGui INSTANCE;

    static {
        INSTANCE = new LuftwaffeGui();
    }

    private final ArrayList<Component> components = new ArrayList();

    public LuftwaffeGui() {
        this.setInstance();
        this.load();
    }

    public static LuftwaffeGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LuftwaffeGui();
        }
        return INSTANCE;
    }

    public static LuftwaffeGui getClickGui() {
        return LuftwaffeGui.getInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void load() {
        int x = -84;
        for (final Module.Category category : Luftwaffe.moduleManager.getCategories()) {
            this.components.add(new Component(category.getName(), x += 90, 4, true) {

                @Override
                public void setupItems() {
                    counter1 = new int[]{1};
                    Luftwaffe.moduleManager.getModulesByCategory(category).forEach(module -> {
                        if (!module.hidden) {
                            this.addButton(new ModuleButton(module));
                        }
                    });
                }
            });
        }
        this.components.forEach(components -> components.getItems().sort(Comparator.comparing(Feature::getName)));
    }

    public void updateModule(Module module) {
        for (Component component : this.components) {
            for (Item item : component.getItems()) {
                if (!(item instanceof ModuleButton)) continue;
                ModuleButton button = (ModuleButton) item;
                Module mod = button.getModule();
                if (module == null || !module.equals(mod)) continue;
                button.initSettings();
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.checkMouseWheel();
        this.drawDefaultBackground();
        this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));

        ModuleButton hovered = getHoveredModuleButton(mouseX, mouseY);
        if (hovered != null) {
            String desc = hovered.getModule().getDescription();
            if (desc != null && !desc.isEmpty()) {
                int descWidth = Luftwaffe.textManager.getStringWidth(desc) + 8;
                int descHeight = 12;
                int x = mouseX + 10;
                int y = mouseY + 10;
                net.minecraft.client.gui.Gui.drawRect(x, y, x + descWidth, y + descHeight, 0xAA000000);
                Luftwaffe.textManager.drawStringWithShadow(desc, x + 4, y + 2, 0xFFFFFF);
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        this.components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return this.components;
    }

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            this.components.forEach(component -> component.setY(component.getY() - 10));
        } else if (dWheel > 0) {
            this.components.forEach(component -> component.setY(component.getY() + 10));
        }
    }

    public int getTextOffset() {
        return -6;
    }

    public Component getComponentByName(String name) {
        for (Component component : this.components) {
            if (!component.getName().equalsIgnoreCase(name)) continue;
            return component;
        }
        return null;
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }

    public ModuleButton getHoveredModuleButton(int mouseX, int mouseY) {
        for (Component component : this.components) {
            for (Item item : component.getItems()) {
                if (item instanceof ModuleButton && item.isHovering(mouseX, mouseY)) {
                    return (ModuleButton) item;
                }
            }
        }
        return null;
    }
}


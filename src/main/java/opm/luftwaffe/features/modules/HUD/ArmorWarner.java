package opm.luftwaffe.features.modules.HUD;

import opm.luftwaffe.features.modules.Module;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ArmorWarner extends Module {
    private static ArmorWarner INSTANCE = new ArmorWarner();
    private final List<String> warnings = new ArrayList<>();
    private static final int WARNING_COLOR = 0xFFFF5555; // Ярко-красный
    private static final float WARNING_THRESHOLD = 33.0f; // 50% прочности

    public ArmorWarner() {
        super("ArmorWarner", "Warns about low armor durability", Category.HUD, true, false, false);
        setInstance();
    }

    public static ArmorWarner getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArmorWarner();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (mc.player == null || mc.world == null) return;

        warnings.clear();
        checkArmorDurability();

        if (!warnings.isEmpty()) {
            renderArmorWarnings();
        }
    }

    private void checkArmorDurability() {
        EntityPlayer player = mc.player;

        for (int i = 0; i < 4; i++) {
            ItemStack armorPiece = player.inventory.armorInventory.get(i);
            if (armorPiece != null && armorPiece.getItem() instanceof ItemArmor) {
                float durabilityPercent = getDurabilityPercent(armorPiece);
                if (durabilityPercent <= WARNING_THRESHOLD) {
                    String pieceName = getArmorPieceName(i);
                    warnings.add(String.format("§4§l%s: §c§l%.0f%%", pieceName, durabilityPercent));
                }
            }
        }
    }

    private String getArmorPieceName(int slot) {
        switch (slot) {
            case 3: return "Helmet";
            case 2: return "Chestplate";
            case 1: return "Leggings";
            case 0: return "Boots";
            default: return "Armor";
        }
    }

    private float getDurabilityPercent(ItemStack stack) {
        float maxDurability = stack.getMaxDamage();
        float currentDamage = stack.getItemDamage();
        return 100.0f - (currentDamage / maxDurability * 100.0f);
    }

    private void renderArmorWarnings() {
        ScaledResolution sr = new ScaledResolution(mc);
        int screenWidth = sr.getScaledWidth();
        int yOffset = sr.getScaledHeight() / 4; // Позиция в верхней четверти экрана

        for (int i = 0; i < warnings.size(); i++) {
            String warning = warnings.get(i);
            int textWidth = mc.fontRenderer.getStringWidth(warning);
            int xPos = (screenWidth - textWidth) / 2;
            int yPos = yOffset + (i * 12); // 12 пикселей между строками

            mc.fontRenderer.drawStringWithShadow(warning, xPos, yPos, WARNING_COLOR);
        }
    }
}
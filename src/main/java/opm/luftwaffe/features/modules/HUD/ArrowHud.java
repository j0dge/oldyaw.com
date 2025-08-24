package opm.luftwaffe.features.modules.HUD;

import opm.luftwaffe.features.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArrow;
import net.minecraft.init.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import opm.luftwaffe.features.setting.Setting;

import java.util.List;

public class ArrowHud extends Module {
    private static ArrowHud INSTANCE = new ArrowHud();
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static String currentArrowText = "";
    private final Setting<Integer> posX = this.register(new Setting<Integer>("x", 740, 0, 1300));
    private final Setting<Integer> posY = this.register(new Setting<Integer>("y", 703, 0, 750));

    public ArrowHud() {
        super("ArrowInfo", "Displays current arrow type", Category.HUD, true, false, false);
        INSTANCE = this;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (mc.player == null || mc.world == null) return;

        ScaledResolution res = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRenderer;


        fr.drawStringWithShadow(currentArrowText, this.posX.getValue(), this.posY.getValue(), 0xFFFFFF);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || mc.player == null) return;

        ItemStack arrows = findBestArrows();
        currentArrowText = getArrowDescription(arrows);
    }

    private static ItemStack findBestArrows() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (isArrow(stack)) return stack;
        }

        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (isArrow(stack)) return stack;
        }

        return ItemStack.EMPTY;
    }

    private static boolean isArrow(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemArrow;
    }

    private static String getArrowDescription(ItemStack arrowStack) {
        if (arrowStack.isEmpty()) {
            return TextFormatting.RED + "Arrows: None";
        }

        if (arrowStack.getItem() == Items.ARROW) {
            return TextFormatting.WHITE + "Arrows: Default (" + arrowStack.getCount() + ")";
        }
        else if (arrowStack.getItem() == Items.TIPPED_ARROW) {
            List<PotionEffect> effects = PotionUtils.getEffectsFromStack(arrowStack);
            if (effects.isEmpty()) {
                return TextFormatting.GRAY + "Arrows: Unkown (" + arrowStack.getCount() + ")";
            }

            PotionEffect effect = effects.get(0);
            return getEffectColor(effect.getPotion()) + "Arrows: " +
                    I18n.format(effect.getEffectName()) +
                    " (" + arrowStack.getCount() + ")";
        }
        else if (arrowStack.getItem() == Items.SPECTRAL_ARROW) {
            return TextFormatting.AQUA + "Arrows: Spectral (" + arrowStack.getCount() + ")";
        }

        return TextFormatting.YELLOW + "Arrows: Unkown (" + arrowStack.getCount() + ")";
    }

    private static TextFormatting getEffectColor(Potion potion) {
        if (potion == Potion.getPotionById(18)) return TextFormatting.GRAY; // Слабость
        if (potion == Potion.getPotionById(5)) return TextFormatting.RED; // Сила
        if (potion == Potion.getPotionById(10)) return TextFormatting.LIGHT_PURPLE; // Регенерация
        if (potion == Potion.getPotionById(1)) return TextFormatting.BLUE; // Ускорение
        if (potion == Potion.getPotionById(3)) return TextFormatting.DARK_GREEN; // Яд
        if (potion == Potion.getPotionById(12)) return TextFormatting.GOLD; // Огнестойкость
        if (potion == Potion.getPotionById(6)) return TextFormatting.GREEN; // Лечение
        if (potion == Potion.getPotionById(2)) return TextFormatting.DARK_BLUE; // Медлительность
        return TextFormatting.LIGHT_PURPLE;
    }
}
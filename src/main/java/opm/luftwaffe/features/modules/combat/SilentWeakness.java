package opm.luftwaffe.features.modules.combat;

import opm.luftwaffe.features.modules.player.CornerClip;
import opm.luftwaffe.features.setting.Setting;
import opm.luftwaffe.features.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SilentWeakness extends Module {
    private static SilentWeakness INSTANCE = new SilentWeakness();
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean isShooting = false;
    private int previousSlot = -1;
    private int bowSlot = -1;
    private int shootTimer = 0;
    private boolean hasFired = false;

    public final Setting<Boolean> autoDisable = this.register(new Setting<>("AutoDisable", true));

    public SilentWeakness() {
        super("SilentWeakness", "AntiRetard", Category.COMBAT, true, false, false);
    }

    public static SilentWeakness getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SilentWeakness();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        resetState();
    }

    private void resetState() {
        isShooting = false;
        previousSlot = -1;
        bowSlot = -1;
        shootTimer = 0;
        hasFired = false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || mc.player == null || hasFired) return;
        if (bowSlot == -1) {
            findBowInHotbar();
            if (bowSlot == -1) return;
        }
        if (!isShooting) {
            startShooting();
        } else {
            handleShooting();
        }
    }

    private void findBowInHotbar() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == Items.BOW) {
                bowSlot = i;
                break;
            }
        }
    }

    private void startShooting() {
        previousSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = bowSlot;
        isShooting = true;
        mc.gameSettings.keyBindUseItem.pressed = true;
        shootTimer = 0;
    }

    private void handleShooting() {
        shootTimer++;
        if (shootTimer >= 3) {
            mc.gameSettings.keyBindUseItem.pressed = false;
            isShooting = false;
            hasFired = true;
            if (previousSlot != -1 && previousSlot < 9) {
                mc.player.inventory.currentItem = previousSlot;
            }

            if (autoDisable.getValue()) {
                this.disable();
            }
        }
    }
}
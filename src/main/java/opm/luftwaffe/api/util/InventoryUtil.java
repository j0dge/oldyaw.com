package opm.luftwaffe.api.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import opm.luftwaffe.api.util.Util;
import opm.luftwaffe.features.command.Command;
import opm.luftwaffe.features.modules.Module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

import static opm.luftwaffe.api.wrapper.IMinecraft.mc;

public class InventoryUtil implements Util {
    public static int itemCount;

    public static void switchToHotbarSlot(int slot, boolean silent) {
        if (mc.player.inventory.currentItem != slot && slot >= 0) {
            if (silent) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                mc.playerController.updateController();
            } else {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                mc.player.inventory.currentItem = slot;
                mc.playerController.updateController();
            }
        }
    }

    public static int getBlockFromHotbar(Block block) {
        int slot = -1;

        for(int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(block)) {
                slot = i;
            }
        }

        return slot;
    }

    public static void check(Module module) {
        if (mc.player != null) {
            int obbySlot = findHotbarBlock(BlockObsidian.class);
            int eChestSlot = findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && eChestSlot == -1) {
                Command.sendSilentMessage(ChatFormatting.GRAY + "No Obsidian or EChests found disabling HoleFiller.");
                module.disable();
            }
        }
    }

    public static boolean isNull(ItemStack stack) {
        return stack == null || stack.getItem() instanceof ItemAir;
    }

    public static int getItemFromHotbar(Item item) {
        int slot = -1;

        for(int i = 8; i >= 0; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                slot = i;
            }
        }

        return slot;
    }

    public static int findSkullSlot() {
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for(int i = 0; i < 9; ++i) {
            ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemSkull) {
                return i;
            }
        }

        return -1;
    }

    public static void doSwap(int slot) {
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    public static int findHotbarClass(Class clazz) {
        for(int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (clazz.isInstance(stack.getItem())) {
                    return i;
                }

                if (stack.getItem() instanceof ItemBlock && clazz.isInstance(((ItemBlock)stack.getItem()).getBlock())) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static ItemStack getItemStack(int id) {
        try {
            return mc.player.inventory.getStackInSlot(id);
        } catch (NullPointerException var2) {
            return null;
        }
    }

    public static void clickSlot(int id) {
        if (id != -1) {
            try {
                mc.playerController.windowClick(mc.player.openContainer.windowId, getClickSlot(id), 0, ClickType.PICKUP, mc.player);
            } catch (Exception var2) {
            }
        }
    }

    public static int getClickSlot(int id) {
        if (id == -1) {
            return id;
        } else if (id < 9) {
            id += 36;
            return id;
        } else {
            if (id == 39) {
                id = 5;
            } else if (id == 38) {
                id = 6;
            } else if (id == 37) {
                id = 7;
            } else if (id == 36) {
                id = 8;
            } else if (id == 40) {
                id = 45;
            }

            return id;
        }
    }

    public static int getItemSlot(Item items) {
        for(int i = 0; i < 36; ++i) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == items) {
                if (i < 9) {
                    i += 36;
                }

                return i;
            }
        }

        return -1;
    }

    public static int getItemCount(Item item) {
        return getItemCount(item, false);
    }

    public static int getItemCount(Item item, boolean countDrag) {
        int count = 0;
        int size = mc.player.inventory.mainInventory.size();

        for(int i = 0; i < size; ++i) {
            ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
            if (itemStack.getItem() == item) {
                count += itemStack.getCount();
            }
        }

        ItemStack offhandStack = mc.player.getHeldItemOffhand();
        if (offhandStack.getItem() == item) {
            count += offhandStack.getCount();
        }

        if (countDrag) {
            ItemStack drag = mc.player.inventory.getItemStack();
            Item dragItem = drag.getItem();
            if (dragItem == item) {
                count += drag.getCount();
            }
        }

        return count;
    }

    public static boolean validScreen() {
        return !(mc.currentScreen instanceof GuiContainer) || mc.currentScreen instanceof GuiInventory;
    }

    public static void click(int slot) {
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
    }

    public static void clickLocked(int slot, int to, Item inSlot, Item inTo) {
        if ((slot == -1 || get(slot).getItem() == inSlot) && get(to).getItem() == inTo) {
            click(to);
        }
    }

    public static int findHotbar(Class clazz) {
        for(int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (clazz.isInstance(stack.getItem())) {
                    return i;
                }
                return i;
            }
        }

        return -1;
    }

    public static ItemStack get(int slot) {
        return slot == -2 ? mc.player.inventory.getItemStack() : mc.player.openContainer.getInventory().get(slot);
    }

    public static int getCount(Item item) {
        int result = 0;

        for(int i = 0; i < 46; ++i) {
            ItemStack stack = mc.player.openContainer.getInventory().get(i);
            if (stack.getItem() == item) {
                result += stack.getCount();
            }
        }

        if (mc.player.inventory.getItemStack().getItem() == item) {
            result += mc.player.inventory.getItemStack().getCount();
        }

        return result;
    }

    public static void switchTo(int slot) {
        if (mc.player.inventory.currentItem != slot && slot > -1 && slot < 9) {
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public static int findInHotbar(Predicate<ItemStack> condition) {
        return findInHotbar(condition, true);
    }

    public static int findInHotbar(Predicate<ItemStack> condition, boolean offhand) {
        if (offhand && condition.test(mc.player.getHeldItemOffhand())) {
            return -2;
        } else {
            int result = -1;

            for(int i = 8; i > -1; --i) {
                if (condition.test(mc.player.inventory.getStackInSlot(i))) {
                    result = i;
                    if (mc.player.inventory.currentItem == i) {
                        break;
                    }
                }
            }

            return result;
        }
    }

    public static void switchToHotbarSlot(Class clazz, boolean silent) {
        int slot = findHotbarBlock(clazz);
        if (slot > -1) {
            switchToHotbarSlot(slot, silent);
        }
    }

    public static int findHotbarBlock(Class clazz) {
        for(int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (clazz.isInstance(stack.getItem())) {
                    return i;
                }

                if (stack.getItem() instanceof ItemBlock && clazz.isInstance(((ItemBlock)stack.getItem()).getBlock())) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int findHotbarBlock(Block blockIn) {
        for(int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock() == blockIn) {
                return i;
            }
        }

        return -1;
    }

    public static int getItemHotbar(Item input) {
        for(int i = 0; i < 9; ++i) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem(item) == Item.getIdFromItem(input)) {
                return i;
            }
        }

        return -1;
    }

    public static int findItemInventorySlot(Item item, boolean offHand) {
        AtomicInteger slot = new AtomicInteger();
        slot.set(-1);
        Iterator<Entry<Integer, ItemStack>> var3 = getInventoryAndHotbarSlots().entrySet().iterator();

        Entry<Integer, ItemStack> entry;
        do {
            do {
                if (!var3.hasNext()) {
                    return slot.get();
                }

                entry = var3.next();
            } while(entry.getValue().getItem() != item);
        } while(entry.getKey() == 45 && !offHand);

        slot.set(entry.getKey());
        return slot.get();
    }

    public static List<Integer> findEmptySlots(boolean withXCarry) {
        ArrayList<Integer> outPut = new ArrayList<>();
        Iterator<Entry<Integer, ItemStack>> var2 = getInventoryAndHotbarSlots().entrySet().iterator();

        while(true) {
            Entry<Integer, ItemStack> entry;
            do {
                if (!var2.hasNext()) {
                    if (withXCarry) {
                        for(int i = 1; i < 5; ++i) {
                            Slot craftingSlot = mc.player.openContainer.inventorySlots.get(i);
                            ItemStack craftingStack = craftingSlot.getStack();
                            if (craftingStack.isEmpty() || craftingStack.getItem() == Items.AIR) {
                                outPut.add(i);
                            }
                        }
                    }
                    return outPut;
                }

                entry = var2.next();
            } while(!entry.getValue().isEmpty() && entry.getValue().getItem() != Items.AIR);

            outPut.add(entry.getKey());
        }
    }

    public static boolean isBlock(Item item, Class clazz) {
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock)item).getBlock();
            return clazz.isInstance(block);
        } else {
            return false;
        }
    }

    public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        return getInventorySlots(9, 44);
    }

    private static Map<Integer, ItemStack> getInventorySlots(int currentI, int last) {
        HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<>();

        for(int current = currentI; current <= last; ++current) {
            fullInventorySlots.put(current, mc.player.openContainer.getInventory().get(current));
        }

        return fullInventorySlots;
    }

    public static boolean holdingItem(Class clazz) {
        boolean result = false;
        ItemStack stack = mc.player.getHeldItemMainhand();
        result = isInstanceOf(stack, clazz);
        if (!result) {
            ItemStack offhand = mc.player.getHeldItemOffhand();
            result = isInstanceOf(stack, clazz);
        }
        return result;
    }

    public static boolean isInstanceOf(ItemStack stack, Class clazz) {
        if (stack == null) {
            return false;
        } else {
            Item item = stack.getItem();
            if (clazz.isInstance(item)) {
                return true;
            } else if (item instanceof ItemBlock) {
                Block block = Block.getBlockFromItem(item);
                return clazz.isInstance(block);
            } else {
                return false;
            }
        }
    }

    public static int getEmptyXCarry() {
        for(int i = 1; i < 5; ++i) {
            Slot craftingSlot = mc.player.openContainer.inventorySlots.get(i);
            ItemStack craftingStack = craftingSlot.getStack();
            if (craftingStack.isEmpty() || craftingStack.getItem() == Items.AIR) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isSlotEmpty(int i) {
        Slot slot = mc.player.openContainer.inventorySlots.get(i);
        ItemStack stack = slot.getStack();
        return stack.isEmpty();
    }

    public static int findArmorSlot(EntityEquipmentSlot type, boolean binding) {
        int slot = -1;
        float damage = 0.0F;

        for(int i = 9; i < 45; ++i) {
            ItemStack s = Minecraft.getMinecraft().player.openContainer.getSlot(i).getStack();
            ItemArmor armor;
            if (s.getItem() instanceof ItemArmor && (armor = (ItemArmor)s.getItem()).armorType == type) {
                float currentDamage = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, s);
                boolean cursed = binding && EnchantmentHelper.hasBindingCurse(s);
                if (currentDamage > damage && !cursed) {
                    damage = currentDamage;
                    slot = i;
                }
            }
        }

        return slot;
    }

    public static int findArmorSlot(EntityEquipmentSlot type, boolean binding, boolean withXCarry) {
        int slot = findArmorSlot(type, binding);
        if (slot == -1 && withXCarry) {
            float damage = 0.0F;

            for(int i = 1; i < 5; ++i) {
                Slot craftingSlot = mc.player.openContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                ItemArmor armor;
                if (craftingStack.getItem() != Items.AIR && craftingStack.getItem() instanceof ItemArmor && (armor = (ItemArmor)craftingStack.getItem()).armorType == type) {
                    float currentDamage = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, craftingStack);
                    boolean cursed = binding && EnchantmentHelper.hasBindingCurse(craftingStack);
                    if (currentDamage > damage && !cursed) {
                        damage = currentDamage;
                        slot = i;
                    }
                }
            }
        }
        return slot;
    }

    public static int findItemInventorySlot(Item item, boolean offHand, boolean withXCarry) {
        int slot = findItemInventorySlot(item, offHand);
        if (slot == -1 && withXCarry) {
            for(int i = 1; i < 5; ++i) {
                Slot craftingSlot = mc.player.openContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if (craftingStack.getItem() != Items.AIR && craftingStack.getItem() == item) {
                    slot = i;
                }
            }
        }
        return slot;
    }

    public static class Task {
        private final int slot;
        private final boolean update;
        private final boolean quickClick;

        public Task() {
            this.update = true;
            this.slot = -1;
            this.quickClick = false;
        }

        public Task(int slot) {
            this.slot = slot;
            this.quickClick = false;
            this.update = false;
        }

        public Task(int slot, boolean quickClick) {
            this.slot = slot;
            this.quickClick = quickClick;
            this.update = false;
        }

        public void run() {
            if (this.update) {
                Util.mc.playerController.updateController();
            }

            if (this.slot != -1) {
                Util.mc.playerController.windowClick(0, this.slot, 0, this.quickClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, Util.mc.player);
            }
        }

        public boolean isSwitching() {
            return !this.update;
        }
    }

    public static class QueuedTask {
        private final int slot;
        private final boolean update;
        private final boolean quickClick;

        public QueuedTask() {
            this.update = true;
            this.slot = -1;
            this.quickClick = false;
        }

        public QueuedTask(int slot) {
            this.slot = slot;
            this.quickClick = false;
            this.update = false;
        }

        public QueuedTask(int slot, boolean quickClick) {
            this.slot = slot;
            this.quickClick = quickClick;
            this.update = false;
        }

        public void run() {
            if (this.update) {
                Util.mc.playerController.updateController();
            }

            if (this.slot != -1) {
                Util.mc.playerController.windowClick(0, this.slot, 0, this.quickClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, Util.mc.player);
            }
        }
    }
}
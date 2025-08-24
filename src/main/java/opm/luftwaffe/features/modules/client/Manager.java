package opm.luftwaffe.features.modules.client;


import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import opm.luftwaffe.Luftwaffe;
import opm.luftwaffe.api.event.events.PacketEvent;
import opm.luftwaffe.features.command.Command;
import opm.luftwaffe.features.modules.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import opm.luftwaffe.features.setting.Setting;
import opm.luftwaffe.api.manager.ChatManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.ITextComponent;

import java.util.HashMap;

public class Manager extends Module {
    public static HashMap<String, Integer> TotemPopContainer = new HashMap<>();
    public static Manager INSTANCE = new Manager();
    public Setting<Boolean> flag = register(new Setting("Rubberband", Boolean.valueOf(false), "NoCheatPlus config issue"));
    public Setting<Boolean> notifyToggles = register(new Setting("Toggle Modules", Boolean.valueOf(true), "notifys in chat"));
//   public Setting<Boolean> notifyPearl = register(new Setting("Pearl Notify", Boolean.valueOf(false), "notifys in chat"));

    public enum ClientName {luftwaffe, SomahaxRAINBOW, Somahax, SomahaxCool}

    public final Setting<ClientName> clientname = register(new Setting<>("PopMode", ClientName.SomahaxRAINBOW));

    private static final int SENDER_ID = 1;

    public Manager() {
        super("Manager", "Notify you about any shit", Category.CLIENT, true, false, false);
        setInstance();
    }

    public static Manager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Manager();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        TotemPopContainer.clear();
    }

    public void onTotemPop(EntityPlayer player) {
        if (Manager.fullNullCheck()) return;
        if (Manager.mc.player.equals(player)) return;

        int count = TotemPopContainer.getOrDefault(player.getName(), 0) + 1;
        TotemPopContainer.put(player.getName(), count);

        ChatManager chatManager = getChatManager();
        if (chatManager == null) return;

        String message = getPopMessage(player.getName(), count);
        ITextComponent component = new TextComponentString(message);
        chatManager.replace(component, player.getName() + "_pop", SENDER_ID, true);
    }

    public void onDeath(EntityPlayer player) {
        if (player == null) return;
        TotemPopContainer.remove(player.getName());
        ChatManager chatManager = getChatManager();
    }

    private String getPopMessage(String player, int count) {
        switch (clientname.getValue()) {
            case luftwaffe:
                return "" +
                        ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" +
                        ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz " +
                        ChatFormatting.WHITE + player + " popped " +
                        ChatFormatting.DARK_RED + ChatFormatting.BOLD + count +
                        ChatFormatting.DARK_RED + ChatFormatting.BOLD + this.getPopString(count) +
                        ChatFormatting.WHITE + " totem" + (count == 1 ? "" : "s");
            case SomahaxRAINBOW:
                return ChatFormatting.GRAY + "[" +
                        ChatFormatting.RED + "s" +
                        ChatFormatting.GOLD + "o" +
                        ChatFormatting.YELLOW + "m" +
                        ChatFormatting.GREEN + "a" +
                        ChatFormatting.AQUA + "h" +
                        ChatFormatting.BLUE + "a" +
                        ChatFormatting.LIGHT_PURPLE + "x" +
                        ChatFormatting.DARK_PURPLE + ".new" +
                        ChatFormatting.GRAY + "] " +
                        ChatFormatting.WHITE + player + " popped " +
                        ChatFormatting.LIGHT_PURPLE + count +
                        ChatFormatting.WHITE + " totem" + (count == 1 ? "" : "s") + " LELEL";
            case Somahax:
                return ChatFormatting.GRAY + "[" +
                        ChatFormatting.WHITE + "soma" +
                        ChatFormatting.DARK_PURPLE + "hax" +
                        ChatFormatting.GRAY + "] " +
                        ChatFormatting.WHITE + player + " popped " +
                        ChatFormatting.DARK_PURPLE + count +
                        ChatFormatting.WHITE + " totem" + (count == 1 ? "" : "s") + " LELEL";
            case SomahaxCool:
                return ChatFormatting.GRAY + "[" +
                        ChatFormatting.LIGHT_PURPLE + "soma" +
                        ChatFormatting.WHITE + "hax" +
                        ChatFormatting.WHITE + ".new" +
                        ChatFormatting.GRAY + "] " +
                        ChatFormatting.WHITE + player + " popped " +
                        ChatFormatting.LIGHT_PURPLE + count +
                        ChatFormatting.WHITE + " totem" + (count == 1 ? "" : "s") + " LELEL";
            default:
                return player + " popped " + count + " totem" + (count == 1 ? "" : "s") + "!";
        }
    }

    public String getPopString(int pops) {
        if (pops == 1) {
            return "st";
        } else if (pops == 2) {
            return "nd";
        } else if (pops == 3) {
            return "rd";
        } else if (pops >= 4 && pops < 21) {
            return "th";
        } else {
            int lastDigit = pops % 10;
            if (lastDigit == 1) {
                return "st";
            } else if (lastDigit == 2) {
                return "nd";
            } else {
                return lastDigit == 3 ? "rd" : "th";
            }
        }
    }

    public static boolean spawnCheck() {
        return (mc.player.ticksExisted > 15);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (flag.getValue().booleanValue()) {
            if (!fullNullCheck() && spawnCheck()) {

                if (event.getPacket() instanceof SPacketPlayerPosLook) {
                    Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" + " " + ChatFormatting.RED + "Detected Lagback");
                }
            }
        }
    }
        private ChatManager getChatManager () {
            return Luftwaffe.chatManager;
        }
    }


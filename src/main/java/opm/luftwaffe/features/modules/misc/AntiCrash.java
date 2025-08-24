package opm.luftwaffe.features.modules.misc;

import com.google.common.collect.Sets;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Set;
import opm.luftwaffe.api.event.events.PacketEvent;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.setting.Setting;
import opm.luftwaffe.api.util.NullUtils;
import opm.luftwaffe.features.command.Command;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiCrash
        extends Module {
    private static AntiCrash INSTANCE = new AntiCrash();
    public final Setting<Boolean> chat = this.register(new Setting<Boolean>("No Unicode", true));
    public final Setting<Boolean> particles = this.register(new Setting<Boolean>("Particles", true));
    public final Setting<Boolean> sound = this.register(new Setting<Boolean>("Sound", true));
    protected static final Set<SoundEvent> SOUNDS = Sets.newHashSet(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundEvents.ITEM_ARMOR_EQIIP_ELYTRA, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER);

    public AntiCrash() {super("AntiCrash", "antiunicode/nosoundlag", Category.MISC, true, false, false);}

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        SPacketChat packet;
        if (NullUtils.nullCheck()) {
            return;
        }
        if (this.chat.getValue().booleanValue() && event.getPacket() instanceof SPacketChat) {
            packet = (SPacketChat)event.getPacket();
            String text = packet.getChatComponent().getUnformattedText();
            int flag = 0;
            for (char currentChar : text.toCharArray()) {
                if (Character.UnicodeBlock.of(currentChar) == Character.UnicodeBlock.BASIC_LATIN) continue;
                ++flag;
            }
            if (flag > 20) {
                Style style = new Style().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, text){

                    public ClickEvent.Action getAction() {
                        return ClickEvent.Action.SUGGEST_COMMAND;
                    }
                });
                AntiCrash.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz " + ChatFormatting.RESET + "Blocked message " + flag + " flags." + ChatFormatting.WHITE + " [" + ChatFormatting.GRAY + "Click to view" + ChatFormatting.WHITE + "]").setStyle(style));
                event.setCanceled(true);
            }
        }
        SPacketParticles packet2;
        if (this.particles.getValue().booleanValue() && event.getPacket() instanceof SPacketParticles && (packet2 = (SPacketParticles)event.getPacket()).getParticleCount() > 800) {
            Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" +  " " + ChatFormatting.RED + "A server administrator attempted to crash your game! Method: Particles");
            event.setCanceled(true);
        }
        if (this.sound.getValue().booleanValue() && event.getPacket() instanceof SPacketSoundEffect && SOUNDS.contains(((SPacketSoundEffect)event.getPacket()).getSound())) {
            event.setCanceled(true);
        }
    }
}
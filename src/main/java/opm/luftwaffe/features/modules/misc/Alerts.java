package opm.luftwaffe.features.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashSet;
import java.util.Random;

import opm.luftwaffe.api.event.events.PacketEvent1;
import opm.luftwaffe.features.command.Command;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.setting.Setting;
import opm.luftwaffe.api.manager.FriendManager;
import opm.luftwaffe.api.util.MathUtil;
import opm.luftwaffe.api.util.NullUtils;
import opm.luftwaffe.api.util.Timer;
import opm.luftwaffe.api.event.events.EntityAddEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Alerts
        extends Module {
    public final Object2IntOpenHashMap<String> registry = new Object2IntOpenHashMap();
    public Setting<Boolean> pops = this.register(new Setting<Boolean>("Pops", true));
    public Setting<Boolean> icehackStyle = this.register(new Setting<Boolean>("2b Colors", true));
    public Setting<Boolean> boldName = this.register(new Setting<Boolean>("Bold Name Pops", true));
    public Setting<Boolean> pearls = this.register(new Setting<Boolean>("Pearls", true));
    public Setting<Boolean> boldNamePearls = this.register(new Setting<Boolean>("Bold Name Pearls", true));
    public Setting<Boolean> strengthDetect = this.register(new Setting<Boolean>("Strength Detect", true));
    public Setting<Boolean> weakDetect = this.register(new Setting<Boolean>("Weak Detect", true));
    public Setting<Boolean> toggleModule = this.register(new Setting<Boolean>("Toggle Modules", true));
    Random rand = new Random();
    public static Alerts INSTANCE;
    Timer delay = new Timer();
    private final HashSet<EntityPlayer> list;
    private final HashSet<EntityPlayer> weaklist;

    public Alerts() {
        super("Notifications", "Notify you about shi", Category.MISC, true, false, false);
        INSTANCE = this;
        this.list = new HashSet();
        this.weaklist = new HashSet();
    }

    @SubscribeEvent
    public void onPacket(PacketEvent1 event) {
        SPacketEntityStatus packet;
        if (NullUtils.nullCheck()) {
            return;
        }
        if (this.pops.getValue().booleanValue() && event.getPacket() instanceof SPacketEntityStatus && event.getTime() == PacketEvent1.Time.Receive && (packet = (SPacketEntityStatus)event.getPacket()).getOpCode() == 35 && packet.getEntity((World)Alerts.mc.world) != null) {
            Entity entity = packet.getEntity((World)Alerts.mc.world);
            this.onPop(entity);
        }
    }

    public void onPop(Entity entity) {
        if (NullUtils.nullCheck()) {
            return;
        }
        if (!this.isEnabled()) {
            return;
        }
        if (this.pops.getValue().booleanValue()) {
            String name = entity.getName();
            boolean isSelf = Alerts.mc.player == entity;
            this.registry.put(name, this.registry.getInt(name) + 1);
            int pops = this.registry.getInt(name);
            if (this.icehackStyle.getValue().booleanValue()) {
                Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" +  " " + (this.boldName.getValue() != false ? ChatFormatting.BOLD : "") + (isSelf ? "You" : name) + ChatFormatting.RESET + ChatFormatting.DARK_RED + (isSelf ? " have" : " has") + " popped" + (pops == 0 ? "." : (isSelf ? " your " : " their ") + ChatFormatting.GOLD + pops + MathUtil.getOrdinal(pops) + " totem."));
            } else {
                Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" + "" + (this.boldName.getValue() != false ? ChatFormatting.BOLD : "") + (isSelf ? "You" : name) + ChatFormatting.RESET + ChatFormatting.AQUA + (isSelf ? " have" : " has") + " popped" + (pops == 0 ? "." : (isSelf ? " your " : " their ") + ChatFormatting.BLUE + pops + MathUtil.getOrdinal(pops) + ChatFormatting.AQUA + " totem"));
            }
        }
    }

    public void onDeath(EntityPlayer player) {
        String name;
        if (NullUtils.nullCheck()) {
            return;
        }
        if (this.pops.getValue().booleanValue() && this.registry.containsKey((name = player.getName()))) {
            int pops = this.registry.getInt(name);
            this.registry.removeInt(name);
            if (this.icehackStyle.getValue().booleanValue()) {
                Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" +  " " + ChatFormatting.DARK_AQUA + "" + (this.boldName.getValue() != false ? ChatFormatting.BOLD : "") + player.getName() + ChatFormatting.RESET + ChatFormatting.DARK_RED + " died after popping" + (pops == 0 ? "." : " their " + ChatFormatting.GOLD + pops + MathUtil.getOrdinal(pops) + " totem."));
            } else {
                Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" +  " " + ((this.boldName.getValue() != false ? ChatFormatting.BOLD : "") + player.getName() + ChatFormatting.RESET + ChatFormatting.AQUA + " died after popping" + (pops == 0 ? "." : " their " + ChatFormatting.BLUE + pops + MathUtil.getOrdinal(pops) + ChatFormatting.AQUA + " totem")));
            }
        }
    }

    @SubscribeEvent
    public void onEntityAdd(EntityAddEvent event) {
        EntityPlayer kidWhoThrowPearl;
        if (this.pearls.getValue().booleanValue() && event.getEntity() instanceof EntityEnderPearl && (kidWhoThrowPearl = Alerts.mc.world.getClosestPlayerToEntity(event.getEntity(), 3.0)) != null) {
            String facing = event.getEntity().getHorizontalFacing().toString();
            if (facing.equals("west")) {
                facing = "east";
            } else if (facing.equals("east")) {
                facing = "west";
            }
            Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" +  " " + ("" + (FriendManager.getInstance().isFriend(kidWhoThrowPearl) ? ChatFormatting.AQUA : "") + (this.boldNamePearls.getValue() != false ? ChatFormatting.BOLD : "") + kidWhoThrowPearl.getDisplayName().getUnformattedText() + ChatFormatting.WHITE + " has thrown an ender pearl " + ChatFormatting.AQUA + facing + "!"));
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (NullUtils.nullCheck()) {
            return;
        }
        if (this.pops.getValue().booleanValue()) {
            for (EntityPlayer player : Alerts.mc.world.playerEntities) {
                if (player == null || player.getHealth() > 0.0f) continue;
                this.onDeath(player);
            }
        }
        if (this.strengthDetect.getValue().booleanValue() || this.weakDetect.getValue().booleanValue()) {
            for (EntityPlayer player : Alerts.mc.world.playerEntities) {
                boolean isSelf;
                boolean bl = isSelf = Alerts.mc.player == player;
                if (this.strengthDetect.getValue().booleanValue()) {
                    if (player.isPotionActive(MobEffects.STRENGTH) && !this.list.contains(player)) {
                        Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" +  " " + ("" + (FriendManager.INSTANCE.isFriend(player) || isSelf ? ChatFormatting.AQUA : ChatFormatting.DARK_AQUA) + (isSelf ? "You" : player.getDisplayName().getUnformattedText()) + ChatFormatting.GREEN + " now " + (isSelf ? "have " : "has ") + "Strength!"));
                        this.list.add(player);
                    }
                    if (!player.isPotionActive(MobEffects.STRENGTH) && this.list.contains(player)) {
                        Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" +  " " + ("" + (FriendManager.INSTANCE.isFriend(player) || isSelf ? ChatFormatting.AQUA : ChatFormatting.DARK_AQUA) + (isSelf ? "You" : player.getDisplayName().getUnformattedText()) + ChatFormatting.DARK_RED + " " + (isSelf ? "have " : "has ") + "lost " + (isSelf ? "your " : "their ") + "Strength!"));
                        this.list.remove(player);
                    }
                }
                if (!this.weakDetect.getValue().booleanValue()) continue;
                if (player.isPotionActive(MobEffects.WEAKNESS) && !this.weaklist.contains(player)) {
                    Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" +  " " + ("" + (FriendManager.INSTANCE.isFriend(player) || isSelf ? ChatFormatting.AQUA : ChatFormatting.DARK_AQUA) + (isSelf ? "You" : player.getDisplayName().getUnformattedText()) + ChatFormatting.WHITE + " " + (isSelf ? "have " : "has ") + "got Weakness!"));
                    this.weaklist.add(player);
                }
                if (player.isPotionActive(MobEffects.WEAKNESS) || !this.weaklist.contains(player)) continue;
                Command.sendSilentMessage("" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "luftwaffe" + ChatFormatting.GRAY + ChatFormatting.BOLD + ".xyz" +  " " + ("" + (FriendManager.INSTANCE.isFriend(player) || isSelf ? ChatFormatting.AQUA : ChatFormatting.DARK_AQUA) + (isSelf ? "You" : player.getDisplayName().getUnformattedText()) + ChatFormatting.WHITE + " " + (isSelf ? "have " : "has ") + "lost " + (isSelf ? "your " : "their ") + "" + ChatFormatting.DARK_GRAY + "Weakness" + ChatFormatting.WHITE + "!"));
                this.weaklist.remove(player);
            }
        }
    }

    @Override
    public String getDescription() {
        return "Alerts: Alerts you of various pvp related things";
    }
}
package opm.luftwaffe.features.modules.HUD;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import opm.luftwaffe.api.event.events.PacketEvent;
import opm.luftwaffe.api.util.ColorUtil;
import opm.luftwaffe.api.util.NullUtils;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.modules.client.ClickGui;
import opm.luftwaffe.features.setting.Setting;

import static opm.luftwaffe.features.gui.components.Component.counter1;

public class KillAlerts extends Module {
    int white = 0xFFFFFFFF;
    public final Setting<Integer> y = this.register(new Setting<Integer>("Y-Pos", 3, 0, 9));
    private final Map<AttackedPlayer, Long> shitMessages = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<AttackedPlayer, Long> attackedPlayers;

    public KillAlerts() {
        super("KillAlerts", "local trashtalking lmfao", Module.Category.HUD, true, false, false);
        this.attackedPlayers = new ConcurrentHashMap<>();
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        CPacketUseEntity cPacket;
        Entity entity;
        if (NullUtils.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketEntityMetadata) {
            SPacketEntityMetadata sPacket = (SPacketEntityMetadata)event.getPacket();
            for (EntityDataManager.DataEntry entry : sPacket.getDataManagerEntries()) {
                Entity entity2;
                float value;
                if (entry.getKey().getId() != 7 || !entry.isDirty() || !(entry.getValue() instanceof Float) ||
                        (value = ((Float)entry.getValue()).floatValue()) > 0.0f ||
                        !((entity2 = mc.world.getEntityByID(sPacket.getEntityId())) instanceof EntityPlayer))
                    continue;

                EntityPlayer dead = (EntityPlayer)entity2;
                for (Map.Entry<AttackedPlayer, Long> attackedPlayer : this.attackedPlayers.entrySet()) {
                    if (!attackedPlayer.getKey().player.equals(dead)) continue;
                    this.DoShit(attackedPlayer.getKey());
                    return;
                }
            }
        }
        if (event.getPacket() instanceof CPacketUseEntity &&
                (entity = (cPacket = (CPacketUseEntity)event.getPacket()).getEntityFromWorld(mc.world)) instanceof EntityPlayer) {

            AttackedPlayer player = null;
            for (Map.Entry<AttackedPlayer, Long> attackedPlayer : this.attackedPlayers.entrySet()) {
                if (!attackedPlayer.getKey().player.equals(entity)) continue;
                player = attackedPlayer.getKey();
                break;
            }

            AttackedPlayer updated = new AttackedPlayer((EntityPlayer)entity);
            if (player != null) {
                updated.attackPackets = player.attackPackets + 1;
                this.attackedPlayers.remove(player);
            }
            this.attackedPlayers.put(updated, System.currentTimeMillis());
        }
    }

    public void DoShit(AttackedPlayer player) {
        String name = player.player.getName();
        String shittalking = "LOLOLOLOLO " + name + " IS DOWN LELELELE";
        shitMessages.put(player, System.currentTimeMillis());
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;
        long currentTime = System.currentTimeMillis();
        shitMessages.entrySet().removeIf(entry -> {
            if (currentTime - entry.getValue() > 5000) {
                return true;
            }
            AttackedPlayer player = entry.getKey();
            String name = player.player.getName();
            Random random = new Random();
            String t1 = name.toUpperCase() + " GOT SMOKED LIKE A BROKE BOI FROM THE HOOD!";
            String t2 = "LMAO " + name.toUpperCase() + " CAN'T EVEN AFFORD A RESPEC AFTER THAT L!";
            String t3 = name.toUpperCase() + " GOT SENT BACK TO THE PROJECTS!";
            String t4 = "BROKE " + name.toUpperCase() + " GOT ZERO BTC AND ZERO CHANCE!";
            String t5 = name.toUpperCase() + " GOT CLAPPED LIKE A CRACKHEAD'S LAST DOLLAR!";
            String t6 = "LOL " + name.toUpperCase() + " GOT WHACKED FOR LESS THAN A SATOSHI!";
            String t7 = name.toUpperCase() + " GOT PUT IN A BODY BAG OVER SOME FAKE CHAIN!";
            String t8 = "BROKE ASS " + name.toUpperCase() + " CAN'T EVEN BUY A BETTER K/D!";
            String t9 = name.toUpperCase() + " GOT DUMPED LIKE A SHITCOIN IN BEAR MARKET!";
            String t10 = "LMAO " + name.toUpperCase() + " GOT ELIMINATED FOR TAX WRITE-OFF!";
            String t11 = name.toUpperCase() + " GOT SMOKED OVER SOME OWED CRYPTO!";
            String t12 = "BROKE " + name.toUpperCase() + " GOT WHACKED FOR BUS FARE!";
            String t13 = name.toUpperCase() + " GOT CLAPPED FOR TRYNA SCAM SOME ETH!";
            String t14 = "LOL " + name.toUpperCase() + " GOT SENT TO THE SHADOW REALM!";
            String t15 = name.toUpperCase() + " GOT DUMPSTERED LIKE A RUG PULL!";
            String t16 = "BROKE " + name.toUpperCase() + " GOT ELIMINATED FOR GAS MONEY!";
            String t17 = name.toUpperCase() + " GOT SMOKED OVER SOME FAKE YEEZYS!";
            String t18 = "LMAO " + name.toUpperCase() + " GOT WHACKED FOR CRUMBS!";
            String t19 = name.toUpperCase() + " GOT CLAPPED TRYNA FLEX EMPTY WALLET!";
            String t20 = "BROKE " + name.toUpperCase() + " GOT SENT TO OBLIVION!";
            String t21 = name.toUpperCase() + " GOT DUMPED LIKE A BAG HOLDER!";
            String t22 = "LOL " + name.toUpperCase() + " GOT ELIMINATED FOR WIFI MONEY!";
            String t23 = name.toUpperCase() + " GOT SMOKED OVER SOME OWED LUNC!";
            String t24 = "BROKE " + name.toUpperCase() + " GOT WHACKED FOR RAMEN MONEY!";
            String t25 = name.toUpperCase() + " GOT CLAPPED TRYNA HUSTLE WRONG BLOCK!";
            String t26 = "LMAO " + name.toUpperCase() + " GOT SENT TO THE GULY!";
            String t27 = name.toUpperCase() + " GOT DUMPSTERED LIKE A FAILED NFT!";
            String t28 = "BROKE " + name.toUpperCase() + " GOT ELIMINATED FOR CIG MONEY!";
            String t29 = name.toUpperCase() + " GOT SMOKED OVER SOME FAKE ICE!";
            String t30 = "LOL " + name.toUpperCase() + " GOT WHACKED FOR CHIPOTLE!";
            String t31 = name.toUpperCase() + " GOT CLAPPED TRYNA FRONT WITH NO FUNDS!";
            String t32 = "BROKE " + name.toUpperCase() + " GOT SENT TO THE AFTERLIFE!";
            String t33 = name.toUpperCase() + " GOT DUMPED LIKE A DEAD SHITCOIN!";
            String t34 = "LMAO " + name.toUpperCase() + " GOT ELIMINATED FOR PHONE CREDITS!";
            String t35 = name.toUpperCase() + " GOT SMOKED OVER SOME OWED DOGE!";
            String t36 = "BROKE " + name.toUpperCase() + " GOT WHACKED FOR CHICKEN WINGS!";
            String t37 = name.toUpperCase() + " GOT CLAPPED TRYNA RUN FROM OPP!";
            String t38 = "LOL " + name.toUpperCase() + " GOT SENT TO THE GRAVEYARD!";
            String t39 = name.toUpperCase() + " GOT DUMPSTERED LIKE A FAILED ICO!";
            String t40 = "BROKE " + name.toUpperCase() + " GOT ELIMINATED FOR BUS TICKET!";
            String t41 = name.toUpperCase() + " GOT SMOKED OVER SOME FAKE ROLEX!";
            String t42 = "LMAO " + name.toUpperCase() + " GOT WHACKED FOR FORTNITE VBUCKS!";
            String t43 = name.toUpperCase() + " GOT CLAPPED TRYNA SCAM SOME SEED PHRASE!";
            String t44 = "BROKE " + name.toUpperCase() + " GOT SENT TO THE SHADOWS!";
            String t45 = name.toUpperCase() + " GOT DUMPED LIKE A DEAD MEMECOIN!";
            String t46 = "LOL " + name.toUpperCase() + " GOT ELIMINATED FOR NETFLIX SUB!";
            String t47 = name.toUpperCase() + " GOT SMOKED OVER SOME OWED SHIB!";
            String t48 = "BROKE " + name.toUpperCase() + " GOT WHACKED FOR PIZZA MONEY!";
            String t49 = name.toUpperCase() + " GOT CLAPPED TRYNA FLEX BROKE ASS PORTFOLIO!";
            String t50 = "LMAO " + name.toUpperCase() + " GOT SENT TO THE UNDERWORLD!";
            String[] trashtalk = {t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33, t34, t35, t36, t37, t38, t39, t40, t41, t42, t43, t44, t45, t46, t47, t48, t49, t50};
            int randomIndex = random.nextInt(trashtalk.length);
            String randomtrashtalk = trashtalk[randomIndex];
            String notrandomtrashtalk = "LMAO " + name.toUpperCase() + " IS DOWN";

            if (ClickGui.getInstance().rainbow.getValue() == false) {
                renderer.drawString(notrandomtrashtalk, renderer.scaledWidth / 2f - renderer.getStringWidth(randomtrashtalk) / 2f, y.getValue(), ClickGui.getInstance().syncColor(), true);
            } else {
                renderer.drawString(notrandomtrashtalk, renderer.scaledWidth / 2f - renderer.getStringWidth(randomtrashtalk) / 2f, y.getValue(), ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
            }
            return false;
        });
    }

    public static final class AttackedPlayer {
        private final EntityPlayer player;
        private int attackPackets;

        public AttackedPlayer(EntityPlayer player) {
            this.player = player;
            this.attackPackets = 0;
        }
    }
}
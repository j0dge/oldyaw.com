package opm.luftwaffe.features.modules.misc;

import opm.luftwaffe.Luftwaffe;
import opm.luftwaffe.api.event.events.PacketEvent;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.setting.Setting;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoReply extends Module {
    private final Setting<Boolean> coords = this.register(new Setting("AutoReply", true));

    public AutoReply() {
        super("AutoReply", "auto reply rbh", Module.Category.MISC, true, false, false);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (!fullNullCheck() && !this.isDisabled()) {
            if (e.getPacket() instanceof SPacketChat) {
                SPacketChat p = (SPacketChat)e.getPacket();
                String unformatted = p.getChatComponent().getUnformattedText();
                if (unformatted.contains("says: ") || unformatted.contains("whispers: ")) {
                    String ign = unformatted.split(" ")[0];
                    if (mc.player.getName().equals(ign) || !Luftwaffe.friendManager.isFriend(ign)) {
                        return;
                    }

                    String msg = unformatted.toLowerCase();
                    if (msg.contains("my coordinates are")) {
                        return;
                    }

                    if ((Boolean)this.coords.getValue() && msg.matches(".*(cord|coord|coords|cords|wya|where are u|where are you|where r u|where ru).*") && !msg.matches(".*(discord|record).*")) {
                        int x = (int)mc.player.posX;
                        int z = (int)mc.player.posZ;
                        String dimension = this.getDimensionName(mc.player.dimension);
                        mc.player.sendChatMessage("/msg " + ign + " My coordinates are X: " + x + " Y: " + (int)mc.player.posY + " Z: " + z + " in the " + dimension + " (Thanks to luftwaffe.xyz)");
                    }
                }
            }
        }
    }

    private String getDimensionName(int dimensionId) {
        Map<Integer, String> dimensionMap = new HashMap();
        dimensionMap.put(0, "OverWorld");
        dimensionMap.put(1, "End");
        dimensionMap.put(-1, "Nether");
        return (String)dimensionMap.getOrDefault(dimensionId, "failed to detect dimension");
    }
}
package opm.luftwaffe.features.modules.misc;

import opm.luftwaffe.features.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ColoredChat extends Module {
    private static ColoredChat INSTANCE = new ColoredChat();
    private static final String[] RAINBOW_COLORS = {"&4&l", "&6&l", "&e&l", "&2&l", "&a&l", "&b&l", "&3&l", "&1&l", "&5&l", "&d&l"};
    public ColoredChat() {
        super("ColoredChat", "Makes your text rainbow and bold(for 2b2tpvp)", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    private int colorIndex = 0;

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        String originalMessage = event.getMessage();

        if (originalMessage.startsWith("/")) {
            return;
        }

        if (originalMessage.startsWith("+")) {
            return;
        }

        if (originalMessage.startsWith(".")) {
            return;
        }

        if (originalMessage.startsWith("-")) {
            return;
        }

        if (originalMessage.startsWith(";")) {
            return;
        }

        if (originalMessage.startsWith("`")) {
            return;
        }

        if (originalMessage.startsWith(",")) {
            return;
        }

        if (originalMessage.startsWith(":")) {
            return;
        }

        if (originalMessage.startsWith("=")) {
            return;
        }

        StringBuilder rainbowMessage = new StringBuilder();

        for (char c : originalMessage.toCharArray()) {
            if (c == ' ') {
                rainbowMessage.append(c);
                continue;
            }

            rainbowMessage.append(RAINBOW_COLORS[colorIndex]).append(c);
            colorIndex = (colorIndex + 1) % RAINBOW_COLORS.length;
        }

        event.setMessage(rainbowMessage.toString());
    }
    public static ColoredChat getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ColoredChat();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}


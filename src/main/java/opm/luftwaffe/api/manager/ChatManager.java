package opm.luftwaffe.api.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatManager {
    private final Map<Integer, Map<String, Integer>> messageIds = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(1337);
    private final Minecraft mc = Minecraft.getMinecraft();

    public void clear() {
        if (mc.ingameGUI != null && mc.ingameGUI.getChatGUI() != null) {
            messageIds.values().forEach(m ->
                    m.values().forEach(id ->
                            mc.ingameGUI.getChatGUI().deleteChatLine(id)
                    )
            );
        }
        messageIds.clear();
        counter.set(1337);
    }

    public void sendDeleteMessage(String message, String uniqueWord, int senderID) {
        if (mc.ingameGUI == null || mc.ingameGUI.getChatGUI() == null) return;
        int id = messageIds
                .computeIfAbsent(senderID, v -> new ConcurrentHashMap<>())
                .computeIfAbsent(uniqueWord, v -> counter.getAndIncrement());
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(message), id);
    }

    public void deleteMessage(String uniqueWord, int senderID) {
        if (mc.ingameGUI == null || mc.ingameGUI.getChatGUI() == null) return;
        Map<String, Integer> map = messageIds.get(senderID);
        if (map != null) {
            Integer id = map.remove(uniqueWord);
            if (id != null) {
                mc.ingameGUI.getChatGUI().deleteChatLine(id);
            }
        }
    }

    public void sendDeleteComponent(ITextComponent component, String uniqueWord, int senderID) {
        if (mc.ingameGUI == null || mc.ingameGUI.getChatGUI() == null) return;
        int id = messageIds
                .computeIfAbsent(senderID, v -> new ConcurrentHashMap<>())
                .computeIfAbsent(uniqueWord, v -> counter.getAndIncrement());
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(component, id);
    }

    public int getId(String uniqueWord, int senderID) {
        Map<String, Integer> map = messageIds.get(senderID);
        if (map != null) {
            Integer id = map.get(uniqueWord);
            if (id != null) {
                return id;
            }
        }
        return -1;
    }

    public void replace(ITextComponent component, String uniqueWord, int senderID, boolean sendIfAbsent) {
        if (mc.ingameGUI == null || mc.ingameGUI.getChatGUI() == null) return;
        Map<String, Integer> map = messageIds.get(senderID);
        if (map != null) {
            Integer id = map.get(uniqueWord);
            if (id != null) {
                mc.ingameGUI.getChatGUI().deleteChatLine(id);
                mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(component, id);
                return;
            }
        }
        if (sendIfAbsent) {
            sendDeleteComponent(component, uniqueWord, senderID);
        }
    }
}
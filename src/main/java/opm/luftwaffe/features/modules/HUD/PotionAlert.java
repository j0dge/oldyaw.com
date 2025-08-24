package opm.luftwaffe.features.modules.HUD;

import opm.luftwaffe.features.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PotionAlert extends Module {
    private static class DisplayMessage {
        final Potion potion;
        final long createTime;
        boolean isFading;
        final int previousDuration;

        DisplayMessage(Potion potion, long createTime, int previousDuration) {
            this.potion = potion;
            this.createTime = createTime;
            this.isFading = false;
            this.previousDuration = previousDuration;
        }
    }

    private static PotionAlert INSTANCE = new PotionAlert();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<DisplayMessage> activeMessages = new CopyOnWriteArrayList<>();
    private final Map<Potion, PotionEffect> previousEffects = new HashMap<>();

    public PotionAlert() {
        super("PotionHUDAlert", "Shows ending potion effects", Category.HUD, true, false, false);
    }

    private static final Map<String, EffectInfo> EFFECT_INFO = new HashMap<String, EffectInfo>() {{
        put("effect.moveSpeed", new EffectInfo("Speed", 0x7CAFC6));
        put("effect.moveSlowdown", new EffectInfo("Slowness", 0x5A6C81));
        put("effect.damageBoost", new EffectInfo("Strength", 0x932423));
        put("effect.weakness", new EffectInfo("Weakness", 0x484D48));
    }};

    private static class EffectInfo {
        final String displayName;
        final int color;

        EffectInfo(String displayName, int color) {
            this.displayName = displayName;
            this.color = color;
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player != mc.player) return;

        Map<Potion, PotionEffect> currentEffects = new HashMap<>();
        for (PotionEffect effect : event.player.getActivePotionEffects()) {
            currentEffects.put(effect.getPotion(), effect);
        }

        for (Map.Entry<Potion, PotionEffect> entry : previousEffects.entrySet()) {
            String effectKey = entry.getKey().getName();
            if (EFFECT_INFO.containsKey(effectKey)) {
                PotionEffect currentEffect = currentEffects.get(entry.getKey());

                if (currentEffect == null || currentEffect.getDuration() <= 1) {
                    int prevDuration = entry.getValue() != null ? entry.getValue().getDuration() : 0;

                    if (prevDuration > 0) {
                        addNewMessage(entry.getKey(), prevDuration);
                    }
                }
            }
        }

        previousEffects.clear();
        previousEffects.putAll(currentEffects);

        long currentTime = System.currentTimeMillis();
        activeMessages.removeIf(message -> {
            long displayTime = currentTime - message.createTime;
            return displayTime > 3600; // Удаляем через 4 секунды
        });
    }

    private void addNewMessage(Potion potion, int previousDuration) {
        boolean exists = activeMessages.stream()
                .anyMatch(m -> m.potion == potion && !m.isFading);

        if (!exists) {
            activeMessages.add(new DisplayMessage(potion, System.currentTimeMillis(), previousDuration));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        ScaledResolution res = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRenderer;
        long currentTime = System.currentTimeMillis();

        int baseY = res.getScaledHeight() / 2 + 20;
        int centerX = res.getScaledWidth() / 2;

        for (int i = 0; i < activeMessages.size(); i++) {
            DisplayMessage message = activeMessages.get(i);
            long displayTime = currentTime - message.createTime;

            float alpha;
            if (displayTime > 2000) {
                message.isFading = true;
                alpha = 1.0f - ((float)(displayTime - 2000) / 2000.0f);
                if (alpha <= 0.01f) continue;
            } else {
                alpha = 1.0f;
            }

            EffectInfo info = EFFECT_INFO.get(message.potion.getName());
            if (info == null) continue;

            String part1 = "Potion ";
            String part2 = info.displayName;
            String part3 = " ended";

            int whiteColor = (int)(alpha * 255.0f) << 24 | 0xFFFFFF;
            int effectColor = (int)(alpha * 255.0f) << 24 | info.color;

            int totalWidth = fr.getStringWidth(part1) + fr.getStringWidth(part2) + fr.getStringWidth(part3);
            int startX = centerX - totalWidth / 2;

            fr.drawStringWithShadow(part1, startX, baseY + i * 12, whiteColor);
            startX += fr.getStringWidth(part1);

            fr.drawStringWithShadow(part2, startX, baseY + i * 12, effectColor);
            startX += fr.getStringWidth(part2);

            fr.drawStringWithShadow(part3, startX, baseY + i * 12, whiteColor);
        }
    }
}
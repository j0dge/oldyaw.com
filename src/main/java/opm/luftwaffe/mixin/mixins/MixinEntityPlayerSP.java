package opm.luftwaffe.mixin.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.injection.Redirect;
import opm.luftwaffe.Luftwaffe;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import opm.luftwaffe.api.event.events.MoveEvent;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends AbstractClientPlayer {
    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        // Only process if luftwaffe is fully loaded
        if (Luftwaffe.commandManager == null) {
            Luftwaffe.LOGGER.info("CommandManager not initialized yet, ignoring: " + message);
            return;
        }

        Luftwaffe.LOGGER.info("Chat message: " + message);
        Luftwaffe.LOGGER.info("Prefix: " + Luftwaffe.commandManager.getPrefix());

        if (message.startsWith(Luftwaffe.commandManager.getPrefix())) {
            Luftwaffe.LOGGER.info("Command intercepted: " + message);
            try {
                Luftwaffe.commandManager.executeCommand(message);
                ci.cancel();
            } catch (Exception e) {
                Luftwaffe.LOGGER.error("Error executing command: " + message, e);
            }
        }

    }  @Redirect(method = "move", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(AbstractClientPlayer player, MoverType moverType, double x, double y, double z) {
        MoveEvent event = new MoveEvent(0, moverType, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            super.move(event.getType(), event.getX(), event.getY(), event.getZ());
        }
    }
}
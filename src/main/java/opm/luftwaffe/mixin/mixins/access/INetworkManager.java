package opm.luftwaffe.mixin.mixins.access;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={NetworkManager.class})
public interface INetworkManager {
    @Invoker(value="dispatchPacket")
    public void distpatchNow(Packet<?> var1, GenericFutureListener<? extends Future<? super Void>>[] var2);
}
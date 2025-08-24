package opm.luftwaffe.api.event.events;


import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PacketEvent1
        extends Event {
    public Packet<?> packet;
    public Time time;

    public PacketEvent1(Packet<?> packet, Time time) {
        this.packet = packet;
        this.time = time;
    }

    public Time getTime() {
        return this.time;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    public boolean isCancelable() {
        return true;
    }

    public static enum Time {
        Send,
        Receive;

    }
}
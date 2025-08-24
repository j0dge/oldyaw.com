package opm.luftwaffe.api.util.discord;

import com.google.gson.JsonObject;

public class Packet {
    private final Opcode opcode;
    private final JsonObject data;

    public Packet(Opcode opcode, JsonObject data) {
        this.opcode = opcode;
        this.data = data;
    }

    public Opcode opcode() {
        return opcode;
    }

    public JsonObject data() {
        return data;
    }
}

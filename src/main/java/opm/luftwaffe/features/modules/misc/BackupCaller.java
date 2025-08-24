package opm.luftwaffe.features.modules.misc;

import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.command.Command;
import opm.luftwaffe.api.manager.FriendManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class BackupCaller extends Module {

    private static final String BACKUP_SUPPORT_ROLE_ID = "1403269655614717972";
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1406914204992012368/oKKEmnFQzSij3fGowrpn_zv-KSjknFjOY2jv_nRSs6g1pkyCHSdIuSm3xyq86pqkd2mJ";

    public BackupCaller() {
        super("BackupCaller", "Pings for backup in luftwaffe discord server", Category.MISC, true, false, false);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            Command.sendMessage("Player or world not available.");
            this.disable();
            return;
        }
        String username = mc.player.getDisplayNameString();
        String icon = "https://minotar.net/avatar/" + username + "/128.png";
        long posX = Math.round(mc.player.posX / 10) * 10;
        long posZ = Math.round(mc.player.posZ / 10) * 10;
        assert mc.getCurrentServerData() != null;
        String server = mc.getCurrentServerData().serverIP;

        List<EntityPlayer> nearbyPlayers = mc.world.playerEntities.stream()
                .filter(p -> p != mc.player && mc.player.getDistance(p) < 50)
                .collect(Collectors.toList());

        List<String> friends = new ArrayList<>();
        List<String> others = new ArrayList<>();
        for (EntityPlayer p : nearbyPlayers) {
            if (FriendManager.getInstance().isFriend(p)) {
                friends.add(p.getName());
            } else {
                others.add(p.getName());
            }
        }

        StringBuilder desc = new StringBuilder();
        desc.append("I'm getting attacked on **").append(server)
                .append("** at **").append(posX).append(" ").append(posZ).append("**. Get on!\n\n");
        if (!friends.isEmpty()) {
            desc.append("**Friends:** ").append(String.join(", ", friends)).append("\n");
        }
        if (!others.isEmpty()) {
            desc.append("**Others:** ").append(String.join(", ", others)).append("\n");
        }
        String dimension = mc.player.dimension == 0 ? "Overworld" : mc.player.dimension == -1 ? "Nether" : "End";
        try {
            sendWebhook(username, icon, desc.toString(), server, dimension, posX, posZ);
            Command.sendMessage("Message sent!");
        } catch (Exception e) {
            Command.sendMessage("Failed to send :(");
            e.printStackTrace();
        }
        this.disable();
    }

    private void sendWebhook(String username, String icon, String description, String server, String dimension, long posX, long posZ) throws IOException {
        String boundary = "----LuftwaffeBackup" + UUID.randomUUID();
        URL url = new URL(WEBHOOK_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"username\":").append(jsonEscape(username)).append(",")
                .append("\"avatar_url\":").append(jsonEscape(icon)).append(",")
                .append("\"content\":").append(jsonEscape("<@&" + BACKUP_SUPPORT_ROLE_ID + ">")).append(",")
                .append("\"embeds\":[{")
                .append("\"title\":\"Backup Needed!\",")
                .append("\"description\":").append(jsonEscape(description)).append(",")
                .append("\"color\":2368553,")
                .append("\"fields\":[")
                .append("{\"name\":\"Server\",\"value\":").append(jsonEscape(server)).append(",\"inline\":true},")
                .append("{\"name\":\"Coordinates\",\"value\":").append(jsonEscape(posX + ", " + posZ)).append(",\"inline\":true},")
                .append("{\"name\":\"Dimension\",\"value\":").append(jsonEscape(dimension)).append(",\"inline\":true}")
                .append("],")
                .append("\"thumbnail\":{\"url\":").append(jsonEscape(icon)).append("}")
                .append("}]")
                .append("}");

        try (OutputStream os = conn.getOutputStream();
             DataOutputStream writer = new DataOutputStream(os)) {

            // payload_json part
            writer.writeBytes("--" + boundary + "\r\n");
            writer.writeBytes("Content-Disposition: form-data; name=\"payload_json\"\r\n");
            writer.writeBytes("Content-Type: application/json; charset=UTF-8\r\n\r\n");
            writer.write(json.toString().getBytes(StandardCharsets.UTF_8));
            writer.writeBytes("\r\n");

            writer.writeBytes("--" + boundary + "--\r\n");
        }

        int code = conn.getResponseCode();
        if (code != 204 && code != 200) {
            try (InputStream err = conn.getErrorStream()) {
                String errResp = err != null ? readStream(err) : "No error body";
                throw new IOException("Discord returned " + code + ": " + errResp);
            }
        }
    }

    private String jsonEscape(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20 || c > 0x7E) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    private String readStream(InputStream in) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }
}
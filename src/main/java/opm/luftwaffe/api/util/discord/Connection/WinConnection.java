package opm.luftwaffe.api.util.discord.Connection;

import com.google.gson.JsonParser;
import opm.luftwaffe.api.util.discord.Opcode;
import opm.luftwaffe.api.util.discord.Packet;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class WinConnection extends Connection {
    private final RandomAccessFile raf;
    private final Consumer<Packet> callback;

    WinConnection(String name, Consumer<Packet> callback) throws IOException {
        this.raf = new RandomAccessFile(name, "rw");
        this.callback = callback;

        Thread thread = new Thread(this::run);
        thread.setName("discord IPC - Read thread");
        thread.start();
    }

    @Override
    protected void write(ByteBuffer buffer) {
        try {
            raf.write(buffer.array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        ByteBuffer intB = ByteBuffer.allocate(4);

        try {
            while (true) {
                // Opcode
                readFully(intB);
                Opcode opcode = Opcode.valueOf(Integer.reverseBytes(intB.getInt(0)));

                // Length
                readFully(intB);
                int length = Integer.reverseBytes(intB.getInt(0));

                // Data
                ByteBuffer dataB = ByteBuffer.allocate(length);
                readFully(dataB);

                // Convert ByteBuffer to String using older method
                dataB.rewind();
                byte[] bytes = new byte[dataB.remaining()];
                dataB.get(bytes);
                String data = new String(bytes, Charset.defaultCharset());

                // Call callback using older JsonParser method
                callback.accept(new Packet(opcode, new JsonParser().parse(data).getAsJsonObject()));
            }
        } catch (Exception ignored) {}
    }

    private void readFully(ByteBuffer buffer) throws IOException {
        buffer.rewind();

        while (raf.length() < buffer.remaining()) {
            // Remove Thread.onSpinWait() for older Java compatibility
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (buffer.hasRemaining()) raf.getChannel().read(buffer);
    }

    @Override
    public void close() {
        try {
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

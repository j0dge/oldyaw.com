package opm.luftwaffe.api.util.discord.Connection;

import com.google.gson.JsonParser;
import opm.luftwaffe.api.util.discord.Opcode;
import opm.luftwaffe.api.util.discord.Packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class UnixConnection extends Connection {
    private final Selector s;
    private final SocketChannel sc;
    private final Consumer<Packet> callback;

    public UnixConnection(String name, Consumer<Packet> callback) throws IOException {
        this.s = Selector.open();

        // Use alternative approach for Unix domain sockets in older Java
        try {
            // Try using unix domain socket through reflection or JNI
            // For compatibility with older Java versions, we'll use a different approach
            this.sc = SocketChannel.open();
            this.callback = callback;

            // Configure socket
            sc.configureBlocking(false);
            sc.register(s, SelectionKey.OP_READ);

            // Connect to Unix socket using alternative method
            connectToUnixSocket(name);

        } catch (Exception e) {
            throw new IOException("Failed to connect to Unix socket: " + name, e);
        }

        Thread thread = new Thread(this::run);
        thread.setName("discord IPC - Read thread");
        thread.start();
    }

    private void connectToUnixSocket(String name) throws IOException {
        // For older Java versions, Unix domain sockets are not directly supported
        // This is a placeholder - in practice, you might need to use JNI or external libraries
        // For now, we'll throw an exception to indicate this limitation
        throw new IOException("Unix domain sockets not supported in this Java version. Path: " + name);
    }

    private void run() {
        State state = State.Opcode;

        ByteBuffer intB = ByteBuffer.allocate(4);
        ByteBuffer dataB = null;

        Opcode opcode = null;

        try {
            while (true) {
                s.select();

                switch (state) {
                    case Opcode:
                        sc.read(intB);
                        if (intB.hasRemaining()) break;

                        opcode = Opcode.valueOf(Integer.reverseBytes(intB.getInt(0)));
                        state = State.Length;

                        intB.rewind();
                        break;
                    case Length:
                        sc.read(intB);
                        if (intB.hasRemaining()) break;

                        dataB = ByteBuffer.allocate(Integer.reverseBytes(intB.getInt(0)));
                        state = State.Data;

                        intB.rewind();
                        break;
                    case Data:
                        sc.read(dataB);
                        if (dataB.hasRemaining()) break;

                        // Convert ByteBuffer to String using older method
                        dataB.rewind();
                        byte[] bytes = new byte[dataB.remaining()];
                        dataB.get(bytes);
                        String data = new String(bytes, Charset.defaultCharset());

                        callback.accept(new Packet(opcode, new JsonParser().parse(data).getAsJsonObject()));

                        dataB = null;
                        state = State.Opcode;
                        break;
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected void write(ByteBuffer buffer) {
        try {
            sc.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            s.close();
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private enum State {
        Opcode,
        Length,
        Data
    }
}

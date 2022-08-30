import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class DestroyerServer implements Runnable {

    private final String serverAddress;
    private final int port;

    public DestroyerServer(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(serverAddress, port));

            while (serverSocketChannel.isOpen()) {
                try (SocketChannel socketChannel = serverSocketChannel.accept()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    while (socketChannel.isConnected() && socketChannel.read(buffer) != -1) {
                        String input = new String(buffer.array(), StandardCharsets.UTF_8).trim();
                        buffer.clear();
                        String output = input.replaceAll("\\s+", "") + "\n";
                        socketChannel.write(ByteBuffer.wrap(output.getBytes(StandardCharsets.UTF_8)));
                    }
                } catch (IOException e) {
                    System.err.println("Could not read from socket channel.");
                    serverSocketChannel.close();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

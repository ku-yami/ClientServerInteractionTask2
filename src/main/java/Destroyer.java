import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Destroyer {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12212;

    public static void main(String[] args) throws IOException {
        // Start server thread
        Thread serverThread = new Thread(new DestroyerServer(SERVER_ADDRESS, SERVER_PORT));
        serverThread.start();

        InetSocketAddress serverAddress = new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT);
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(serverAddress);
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (true) {
                System.out.print("Введите строку для удаления пробелов или end для завершения работы: ");
                String inputLine = scanner.nextLine();
                if ("end".equalsIgnoreCase(inputLine)) {
                    buffer.clear();
                    break;
                }
                socketChannel.write(ByteBuffer.wrap(inputLine.getBytes(StandardCharsets.UTF_8)));

                int bytesRead = socketChannel.read(buffer);
                String response = new String(buffer.array(), 0, bytesRead);
                System.out.println("Ответ сервера: " + response);
                buffer.clear();
            }
        }
        serverThread.interrupt();
    }
}

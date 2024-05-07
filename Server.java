import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;

public class Server {
    static ServerSocket serverSocket;
    Server (int port) throws IOException, ClassNotFoundException {
        serverSocket = new ServerSocket(port);
        Socket socket = serverSocket.accept();
        System.out.println("Server started");
        System.out.println("Client connected");
        serverSocket.close();
        System.out.println("Socket closed");
        new ChatParticipant(socket, "");

    }
}

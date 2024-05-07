import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;

public class Server {
    static ServerSocket serverSocket;
    Server (int port) throws IOException, ClassNotFoundException {
        serverSocket = new ServerSocket(port);
        new ServerWindow(serverSocket, port);
        System.out.println("Server started");
    }
}

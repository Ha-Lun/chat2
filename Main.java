import java.io.IOException;
public class Main{
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        final int port = 11234;
        try {
            System.out.println("Starting Client");
            new Client(port);
        }
        catch (Exception e) {
            System.out.println("Client failed (" + e + "), starting Server");
            new Server(port);
        }
    }
}

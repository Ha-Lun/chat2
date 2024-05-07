import java.io.IOException;
public class Main{
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        final int port = 3445;
        try {
            System.out.println("Starting Client");
            new Client();
        }
        catch (Exception e) {
            System.out.println("Client failed, starting Server");
            new Server(port);
        }
    }
}

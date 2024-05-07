import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class ServerWindow implements ActionListener {
    JButton exitButton;
    JFrame frame;
    List<Socket> socketList = new ArrayList<Socket>();
    ServerSocket serverSocket;

    ServerWindow(ServerSocket serverSocket, int port) throws IOException
    {
        this.serverSocket = serverSocket;
        createFrame();
        findConnections(serverSocket);
        new Thread(() -> {
            while(true){
                messageListener();
            }
        }).start();
    }

    private void createFrame()
    {
        frame = new JFrame("Server");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChatWindow chatWindow = createServerWindow();

        exitButton = new JButton("EXIT");
        exitButton.addActionListener(this);
        chatWindow.add(exitButton, BorderLayout.NORTH);

        frame.add(chatWindow);
        frame.pack();
        frame.setVisible(true);
    }
    private ChatWindow createServerWindow()
    {
        ChatWindow chatWindow = new ChatWindow();
        chatWindow.setLayout(new BorderLayout());
        exitButton = new JButton("EXIT");
        exitButton.addActionListener(this);
        chatWindow.add(exitButton, BorderLayout.NORTH);
        return chatWindow;
    }

    private void createExitMessage()
    {
        JDialog dialog = new JDialog(frame, "EXIT MESSAGE");
        dialog.setSize(new Dimension(400, 400));
        dialog.setLayout(new FlowLayout());
        JLabel label = new JLabel("THE CHAT HAS BEEN EXITED. TURNING OFF IN 5 SECONDS");
        dialog.add(label);
        dialog.setVisible(true);
    }
    private void findConnections(ServerSocket serverSocket)
    {
        new Thread(() -> {
            try
            {
                while (!serverSocket.isClosed())
                {
                    Socket clientSocket = serverSocket.accept();
                    if (!socketList.contains(clientSocket))
                    {
                        socketList.add(clientSocket);
                    }
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }).start();
    }
    private void messageListener()
    {
        int len = socketList.size();
        System.out.println("test" + len);
        for (int i = 0; i < socketList.size(); i++)
        {
            System.out.println("test2" + len);
            int j = i;
            new Thread(() -> {
                try {
                    while(true)
                    {
                        System.out.println("message1");
                        InputStream inputStream = socketList.get(j).getInputStream();
                        ObjectInput objectInput = new ObjectInputStream(inputStream);
                        System.out.println("message2");
                        String message = (String) objectInput.readObject();
                        sendMessage(message);
                    }

                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("TEST2");
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
    private void sendMessage(String message) throws IOException
    {
        for (Socket socket : socketList)
        {
            if (!socket.isClosed())
            {
                try
                {
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(message);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if (Objects.equals(e.getActionCommand(), "EXIT"))
        {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

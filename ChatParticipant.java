import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;

import javax.swing.*;
import javax.swing.border.Border;

public class ChatParticipant implements KeyListener, ActionListener
{
    JButton exitButton;
    JTextField textField;
    JTextArea textArea;
    ObjectInput objectInput;
    ObjectOutput objectOutput;
    JFrame frame;
    String name;

    ChatParticipant(Socket socket, String name) throws IOException
    {
        this.name = name;
        createStreams(socket);
        createFrame();
        streamSearcher();
    }
    private void streamSearcher()
    {
        new Thread(() -> {
            try {
                while (true) {
                    String inputText = (String) objectInput.readObject();
                    if (Objects.equals(inputText, "EXIT")){
                        break;
                    }
                    if (!inputText.isBlank()){
                        textArea.append("Other Person: " + inputText + "\n");
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
            finally {
                try {
                    objectOutput.writeObject("EXIT");
                    createExitMessage();
                    Thread.sleep(5000);
                    if (Client.clientSocket != null){
                        frame.dispose();
                        Client.clientSocket.close();
                    }
                    frame.dispose();
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }

                frame.dispose();
            }
        }).start();
    }
    private void createFrame()
    {
        frame = new JFrame("Message " + name);
        ChatWindow chatWindow = createChat();
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chatWindow);
        frame.pack();
        frame.setVisible(true);
    }
    private ChatWindow createChat()
    {
        ChatWindow chatWindow = new ChatWindow();
        chatWindow.setLayout(new BorderLayout());

        textField = new JTextField(20);
        textField.addKeyListener(this);
        chatWindow.add(textField, BorderLayout.SOUTH);

        textArea = new JTextArea(25, 30);
        textArea.setEditable(false);
        chatWindow.add(textArea, BorderLayout.CENTER);

        exitButton = new JButton("EXIT");
        exitButton.addActionListener(this);
        chatWindow.add(exitButton, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(textArea);
        chatWindow.add(scrollPane);
        return chatWindow;
    }
    private void createStreams(Socket socket) throws IOException
    {
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        objectOutput = new ObjectOutputStream(outputStream);
        objectInput = new ObjectInputStream(inputStream);
    }
    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER)
        {
           String outputText = textField.getText();
           textField.setText("");
           if (!outputText.isBlank()){
               textArea.append("You: " + outputText + "\n");
           }
            try {
                objectOutput.writeObject(outputText);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (Objects.equals(e.getActionCommand(), "EXIT"))
        {
            try {
                objectOutput.writeObject("EXIT");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
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
}

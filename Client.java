import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;
import java.util.Arrays;
import java.util.Scanner;

public class Client{
    static Socket clientSocket;
    static int selectedIndex = 0;
    String filePath = "C:/Programmering/Textfiler/favoritnamn.txt";
    Client() throws IOException, ClassNotFoundException {
        String[] data = findData(filePath);
        System.out.println("Client connected");

        clientSocket = new Socket(separateIp(data)[selectedIndex], separatePort(data)[selectedIndex]);
        createFrame(data);
    }

    private String[] findData(String filePath) throws FileNotFoundException
    {
        int nameCounter = 0;
        File names = new File(filePath);
        Scanner countReader = new Scanner(names);


        while(countReader.hasNextLine())
        {
            countReader.nextLine();
            nameCounter++;
        }

        Scanner myReader = new Scanner(names);
        String[] nameArray = new String[nameCounter];
        int nameIndex = 0;
        while(nameIndex < nameCounter && myReader.hasNextLine())
        {
            nameArray[nameIndex] = myReader.nextLine();
            nameIndex++;
        }
        return nameArray;
    }
    private void createFrame(String[] dataArray)
    {
        JFrame frame = new JFrame("Chat");
        JComboBox box = createBox(dataArray);
        JButton myButton = createButton(frame, dataArray);
        updateInputIndex(box);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(300, 150));
        frame.setLayout(new BorderLayout());
        frame.add(box, BorderLayout.CENTER);
        frame.add(myButton, BorderLayout.NORTH);
        frame.add(createTextField(frame), BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }
    private JComboBox createBox(String[] array)
    {
        JComboBox<String> nameBox = new JComboBox<String>(separateNames(array));
        return nameBox;
    }
    private JButton createButton(JFrame frame, String[] dataArray)
    {
        JButton myButton = new JButton("Choose");
        myButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                try {
                    //clientSocket = new Socket(separateIp(dataArray)[selectedIndex], separatePort(dataArray)[selectedIndex]);
                    new ChatParticipant(clientSocket, separateNames(dataArray)[selectedIndex]);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        return myButton;
    }
    private JTextField createTextField(JFrame frame)
    {
        JTextField textField = new JTextField(20);
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER)
                {
                    String newFavorite = textField.getText();

                    frame.dispose();
                    try {
                        FileWriter myWriter = new FileWriter(filePath, true);
                        myWriter.write("\n" + newFavorite);
                        myWriter.close();

                        new ChatParticipant(clientSocket, newFavorite.split(";")[0]);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        return textField;
    }
    private void updateInputIndex(JComboBox box)
    {
        box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedIndex = box.getSelectedIndex();
            }
        });
    }
    private String[] separateNames(String[] dataArray)
    {
        String[] returnArray = new String[dataArray.length];
        for(int i = 0; i < dataArray.length; i++)
        {
            returnArray[i] = dataArray[i].split(";")[0];
        }
        return returnArray;
    }
    private int[] separatePort(String[] dataArray)
    {
        int[] returnArray = new int[dataArray.length];
        for(int i = 0; i < dataArray.length; i++)
        {
            returnArray[i] = Integer.parseInt(dataArray[i].split(":")[1]);
        }
        return returnArray;
    }
    private String[] separateIp(String[] dataArray)
    {
        String[] returnArray = new String[dataArray.length];
        for(int i = 0; i < dataArray.length; i++)
        {
            returnArray[i] = dataArray[i].split(";")[1].split(":")[0];
        }
        return returnArray;
    }
}

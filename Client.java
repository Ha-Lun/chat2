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
    Client(int port) throws IOException, ClassNotFoundException {
        clientSocket = new Socket("localhost", port);
        System.out.println("Client connected");
        new ChatParticipant(clientSocket);
    }
}

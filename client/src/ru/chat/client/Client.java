package ru.chat.client;

import ru.chat.network.TCPConnection;
import ru.chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Client extends JFrame implements TCPConnectionListener {

    private static final String IP = "localhost";
    private static final int PORT = 8189;
    private TCPConnection connection;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private final JTextArea msgList = new JTextArea();
    private final JTextField fieldNick = new JTextField("incognito");
    private final JTextField fieldMsg = new JTextField();
    private final JScrollPane scrollPane = new JScrollPane();

    private Client() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setResizable(false);
        setVisible(true);
        scrollPane.setBounds(23, 40, 394, 191);
        scrollPane.setViewportView(msgList);
        msgList.setEditable(false);
        msgList.setLineWrap(true);
        add(scrollPane, BorderLayout.CENTER);
        add(fieldMsg, BorderLayout.SOUTH);
        add(fieldNick, BorderLayout.NORTH);
        try {
            connection = new TCPConnection(Client.this, IP, PORT);
        } catch (IOException e) {
            onException(connection, e);
        }
        fieldMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fieldMsg.getText().equals("")) return;
                connection.sendMsg(fieldNick.getText() + ": " + fieldMsg.getText());
                fieldMsg.setText(null);
            }
        });

    }

    private synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                msgList.append(msg + "\n");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String msg) {
        printMsg(msg);
    }

    @Override
    public void onDisconnection(TCPConnection tcpConnection) {
        printMsg("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("Connection error: " + e);
    }
}
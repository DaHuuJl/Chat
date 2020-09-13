package ru.chat.server;

import ru.chat.network.TCPConnection;
import ru.chat.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements TCPConnectionListener {

    private final ArrayList<TCPConnection> connections = new ArrayList<>();
    private Server() {
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)){
            while (true){
                try {
                    new TCPConnection(serverSocket.accept(), this);
                } catch (IOException e){
                    System.out.println("TCPConnection error: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Server();
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendAll("Client connected " + tcpConnection.toString());
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String msg) {
        sendAll(msg);
    }

    @Override
    public synchronized void onDisconnection(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendAll("Client disconnection: " + tcpConnection.toString());
    }

    @Override
    public synchronized  void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection error: " + e);
    }

    private void sendAll (String msg){
            for (TCPConnection connection : connections) {
                connection.sendMsg(msg);
            }
    }
}

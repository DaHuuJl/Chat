package ru.chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPConnection {

    private final Socket socket;
    private final Thread thread;
    private final TCPConnectionListener listener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionListener listener, String IP, int PORT) throws  IOException {
        this(new Socket(IP, PORT), listener);
    }

    public TCPConnection(Socket socket, TCPConnectionListener listener) throws IOException {
        this.socket = socket;
        this.listener = listener;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listener.onConnectionReady(TCPConnection.this);
                    while (!thread.isInterrupted()){
                        listener.onReceiveString(TCPConnection.this, in.readLine());

                    }
                } catch (IOException e) {
                    listener.onException(TCPConnection.this, e);
                } finally {
                    listener.onDisconnection(TCPConnection.this);
                }
            }
        });
        thread.start();
    }

    public synchronized void sendMsg(String msg){
        try {
            out.write(msg + "\r\n");
            out.flush();
        } catch (IOException e) {
            listener.onException(TCPConnection.this, e);
            disconnection();
        }
    }

    public synchronized void disconnection(){
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            listener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}

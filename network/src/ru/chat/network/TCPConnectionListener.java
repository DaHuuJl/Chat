package ru.chat.network;

public interface TCPConnectionListener {

    /**
     * Принимает готовое соединение
     * @param tcpConnection соединение
     */
    void onConnectionReady(TCPConnection tcpConnection);

    /**
     * Принимает строку
     * @param tcpConnection соединение
     * @param msg строка
     */
    void onReceiveString(TCPConnection tcpConnection, String msg);

    /**
     * Обрывает соединение
     * @param tcpConnection соединение
     */
    void  onDisconnection(TCPConnection tcpConnection);

    /**
     * Обработка исключений
     * @param tcpConnection соединение
     * @param e исключение
     */
    void onException(TCPConnection tcpConnection, Exception e);
}

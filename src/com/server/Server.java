package com.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private ServerInputHandler inputHandler;
    private ServerRequestHandler requestHandler;
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

    public Server(int port, ServerInputHandler inputHandler, ServerRequestHandler requestHandler) {
        this.port = port;
        this.inputHandler = inputHandler;
        this.requestHandler = requestHandler;
    }

    public void start() {
            openSocket();
            inputHandler.start();
            while (true) {
                Socket clientSocket = listenForClientSocket();
                fixedThreadPool.submit(new ServerConnectionHandler(this, clientSocket, requestHandler));
            }
    }

    public void openSocket() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.printf("Не удалось развернуть сервер на порте %d.", port);
        }
    }

    private Socket listenForClientSocket() {
        try {
            Socket clientSocket = serverSocket.accept();
            return clientSocket;
        } catch (IOException exception) {
            System.out.println("Ошибка установления соединения с клиентом.");
        }
        return null;
    }
}

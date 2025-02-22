package com.shiyixi.messager.simulator;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static final int PORT = 12345;
    private static List<PrintWriter> clientOutputStreams;

    public static void main(String[] args) {
        clientOutputStreams = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                // accept 阻塞程序，直到有客户端的连接
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                Thread clientHandler = new Thread(new ClientHandler(clientSocket));
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private BufferedReader reader;

        // 构造方法，获取 reader
        public ClientHandler(Socket clientSocket) {
            try {
                InputStreamReader isReader = new InputStreamReader(clientSocket.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received: " + message);
                    tellEveryone(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 给所有连接对象发送消息
        private void tellEveryone(String message) {
            for (PrintWriter writer : clientOutputStreams) {
                writer.println(message);
                writer.flush();
            }
        }
    }
}
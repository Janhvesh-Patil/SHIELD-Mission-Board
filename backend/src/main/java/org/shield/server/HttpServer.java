package org.shield.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private final int port;
    private final ExecutorService threadPool;

    public HttpServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.submit(() -> handleClient(socket));
            }
        } catch (IOException e) {
            System.out.println("System error " + e.getMessage());
        }
    }

    private void handleClient(Socket socket) {
        try (socket) {
            try {
                HttpRequest request = new HttpRequest(socket.getInputStream());
                String response = Router.route(request);
                socket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
                socket.getOutputStream().flush();
            } catch (Exception e) {
                e.printStackTrace();
                String errorResponse = HttpResponse.buildResponse(500,
                        "{\"error\":\"Internal server error\"}");
                socket.getOutputStream().write(errorResponse.getBytes(StandardCharsets.UTF_8));
                socket.getOutputStream().flush();
            }
        } catch (IOException e) {
            System.out.println("Socket error: " + e.getMessage());
        }
    }
}

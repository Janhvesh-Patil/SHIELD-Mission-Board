package org.shield;

import org.shield.server.HttpServer;

public class Main {
    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer(9090);
        httpServer.start();
    }
}

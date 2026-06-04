package org.shield;

import org.shield.server.HttpServer;

public class Main {
    public static void main(String[] args) {
        String portEnv = System.getenv("PORT");
        int port = (portEnv != null) ? Integer.parseInt(portEnv) : 9090;
        new HttpServer(port).start();
    }
}

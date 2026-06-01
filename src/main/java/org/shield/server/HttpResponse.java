package org.shield.server;

import java.nio.charset.StandardCharsets;

public class HttpResponse {
    public static String buildResponse(int statusCode, String body) {
        return "HTTP/1.1 " + statusCode + " " +  // STATUS LINE
                switch (statusCode) {
            case 200 -> "OK";                         // Successful GET, PATCH, DELETE
            case 201 -> "Created";                    // Successful POST (something was created)
            case 204 -> "No Content";                 //Successful DELETE (nothing to return)
            case 400 -> "Bad Request";                // Client sent invalid data
            case 404 -> "Not Found";                  // Mission ID doesn't exist
            case 500 -> "Internal Server Error";      // Something broke on your side
            default -> "Unknown";
                } + "\r\n" +
                //Headers
            "Content-Type: application/json" + "\r\n" +
            "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "Access-Control-Allow-Origin: *" + "\r\n" +
                "Access-Control-Allow-Methods: GET, POST, PATCH, DELETE, OPTIONS" + "\r\n" +
                "Access-Control-Allow-Headers: Content-Type" + "\r\n" +
                //Body
                "\r\n" +
                body;
    }
}

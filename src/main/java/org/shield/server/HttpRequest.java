package org.shield.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private String body;
    private Map<String, String> headers = new HashMap<>();
    private int contentLength;

    public HttpRequest(InputStream inputStream) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String[] methodAndPath = bufferedReader.readLine().split(" "); //Method and Path
            this.method = methodAndPath[0];
            this.path = methodAndPath[1];

            String line; // read headers
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                String[] keyValuePair = line.split(":", 2);
                this.headers.put(keyValuePair[0].trim(), keyValuePair[1].trim());
            }

            String cl = headers.get("Content-Length");
            this.contentLength = (cl != null) ? Integer.parseInt(cl.trim()) : 0;

            //read body
            char[] bodyChars = new char[contentLength];
            int totalRead = 0;
            while (totalRead < contentLength) {
                int read = bufferedReader.read(bodyChars, totalRead, contentLength - totalRead);
                if (read == -1) break; // stream ended unexpectedly
                totalRead += read;
            }
            this.body = new String(bodyChars, 0, totalRead);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public int getContentLength() {
        return contentLength;
    }
}

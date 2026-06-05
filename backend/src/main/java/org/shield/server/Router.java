package org.shield.server;

import org.shield.handler.AgentHandler;
import org.shield.handler.MissionHandler;

public class Router {
    private static final MissionHandler missionHandler = new MissionHandler();
    private static final AgentHandler agentHandler = new AgentHandler();
    public static String route(HttpRequest request) {

        if (request.getMethod().isEmpty()) {
            return HttpResponse.buildResponse(400, "");
        }

        if (request.getMethod().equals("OPTIONS")) {
            return HttpResponse.buildResponse(200, "");
        }

        String method = request.getMethod();
        String path = request.getPath();
        String[] segments = path.split("/");

        if (segments.length < 2) {
            return HttpResponse.buildResponse(404, "{\"error\":\"Route not found\"}");
        }

        switch (method) {
            case "GET" -> {
                if (segments[1].equals("missions") && segments.length == 2) {
                    return missionHandler.getAll();
                } else if (segments[1].equals("missions") && segments.length == 3) {
                    return missionHandler.getById(Integer.parseInt(segments[2]));
                } else if (segments[1].equals("agents")) {
                    return agentHandler.getAll();
                } else {
                    return HttpResponse.buildResponse(404, "{\"error\":\"Route not found\"}");
                }
            }
            case "PATCH" -> {
                if (segments.length == 4 && segments[3].equals("status")) {
                    return missionHandler.updateStatus(
                            Integer.parseInt(segments[2]),
                            request.getBody());
                } else if (segments.length == 4 && segments[3].equals("agent")) {
                    return missionHandler.updateAgent(Integer.parseInt(segments[2]), request.getBody());
                } else {
                    return HttpResponse.buildResponse(404, "{\"error\":\"Route not found\"}");
                }
            }
            case "POST" -> {
                if (segments[1].equals("missions")) {
                    return missionHandler.create(request.getBody());
                } else if (segments[1].equals("agents")) {
                    return agentHandler.create(request.getBody());
                }  else {
                    return HttpResponse.buildResponse(404, "{\"error\":\"Route not found\"}");
                }
            }
            case "DELETE" -> {
                if (segments.length == 3) {
                    return missionHandler.delete(Integer.parseInt(segments[2]));
                } else {
                    return HttpResponse.buildResponse(404, "{\"error\":\"Route not found\"}");
                }
            }
            default -> {
                return HttpResponse.buildResponse(404, "{\"error\":\"No route matches\"}");
            }
        }

    }
}

package org.shield.handler;

import com.google.gson.Gson;
import org.shield.dao.AgentDao;
import org.shield.server.HttpResponse;

public class AgentHandler {
    private final AgentDao agentDao = new AgentDao();
    private final Gson gson = new Gson();

    public String getAll() {
        var agents = agentDao.findAll();
        return HttpResponse.buildResponse(200, gson.toJson(agents));
    }

    public String create(String body) {
        if (body == null || body.isBlank()) {
            return HttpResponse.buildResponse(400, "{\"error\":\"Agent name not provided\"}");
        }

        var jsonObject = gson.fromJson(body, com.google.gson.JsonObject.class);
        var nameElement = jsonObject.get("agentName");

        if (nameElement == null || nameElement.getAsString().isBlank()) {
            return HttpResponse.buildResponse(400, "{\"error\":\"Agent name is required\"}");
        }

        boolean result = agentDao.insert(nameElement.getAsString().trim());
        if (result) {
            return HttpResponse.buildResponse(201, "{\"message\":\"Agent created successfully\"}");
        } else {
            return HttpResponse.buildResponse(400, "{\"error\":\"Insertion failed\"}");
        }
    }
}

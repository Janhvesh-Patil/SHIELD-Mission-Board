package org.shield.handler;

import com.google.gson.Gson;
import org.shield.dao.AgentDao;
import org.shield.server.HttpResponse;

public class AgentHandler {
    private final AgentDao agentDao = new AgentDao();

    public String getAll() {
        var heroes = agentDao.findAll();
        Gson gson = new Gson();
        return HttpResponse.buildResponse(200, gson.toJson(heroes));
    }
}

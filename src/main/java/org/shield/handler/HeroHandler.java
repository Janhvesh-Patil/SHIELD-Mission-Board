package org.shield.handler;

import com.google.gson.Gson;
import org.shield.dao.HeroDao;
import org.shield.server.HttpResponse;

public class HeroHandler {
    private HeroDao heroDao = new HeroDao();

    public String getAll() {
        var heroes = heroDao.findAll();
        Gson gson = new Gson();
        return HttpResponse.buildResponse(200, gson.toJson(heroes));
    }
}

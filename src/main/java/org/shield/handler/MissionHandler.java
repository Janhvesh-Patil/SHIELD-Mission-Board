package org.shield.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.shield.dao.MissionDao;
import org.shield.model.Mission;
import org.shield.server.HttpResponse;

import java.time.LocalDateTime;

public class MissionHandler {
    private final MissionDao missionDao = new MissionDao();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public String getAll() {
        var missionList = missionDao.findAll();
        return HttpResponse.buildResponse(200, gson.toJson(missionList));
    }

    public String getById(int id) {
        var missionInfo = missionDao.findById(id);
        if (missionInfo.isPresent()) {
            var information = missionInfo.get();
            return HttpResponse.buildResponse(200, gson.toJson(information));
        } else {
            return HttpResponse.buildResponse(404, "{\"error\":\"Mission not found\"}");
        }
    }

    public String create(String body) {
        Mission insertItems = gson.fromJson(body, Mission.class);
        boolean result = missionDao.insert(insertItems);
        if (result) {
            return HttpResponse.buildResponse(201, "{\"message\":\"Mission created successfully\"}");
        } else {
            return HttpResponse.buildResponse(400, "{\"error\":\"Insertion failed\"}");
        }
    }

    public String updateStatus(int id, String body) {
        Mission insertItems = gson.fromJson(body, Mission.class);
        if (insertItems.getStatus() == null) {
            return HttpResponse.buildResponse(400, "{\"error\":\"Status is required\"}");
        }
        boolean result = missionDao.updateStatus(id, insertItems.getStatus().name().toLowerCase());
        if (result) {
            return HttpResponse.buildResponse(200, "Successfully Updated");
        } else {
            return HttpResponse.buildResponse(404, "{\"error\":\"Mission not found\"}");
        }
    }

    public String delete(int id) {
        boolean result = missionDao.delete(id);
        if (result) {
            return HttpResponse.buildResponse(204, "");
        } else {
            return HttpResponse.buildResponse(404, "{\"error\":\"Mission not found\"}");
        }
    }
}

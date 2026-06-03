package org.shield.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.shield.dao.MissionDao;
import org.shield.model.Mission;
import org.shield.model.MissionCategory;
import org.shield.model.MissionPriority;
import org.shield.model.MissionStatus;
import org.shield.server.HttpResponse;

import java.time.LocalDateTime;

public class MissionHandler {
    private final MissionDao missionDao = new MissionDao();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(MissionCategory.class,
                    (JsonDeserializer<MissionCategory>) (json, typeOfT, context) -> {
                        try {
                            return MissionCategory.valueOf(json.getAsString().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
            .registerTypeAdapter(MissionPriority.class,
                    (JsonDeserializer<MissionPriority>) (json, typeOfT, context) -> {
                        try {
                            return MissionPriority.valueOf(json.getAsString().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
            .registerTypeAdapter(MissionStatus.class,
                    (JsonDeserializer<MissionStatus>) (json, typeOfT, context) -> {
                        try {
                            return MissionStatus.valueOf(json.getAsString().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
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

        if (insertItems.getTitle() == null || insertItems.getTitle().isBlank()) {
            return HttpResponse.buildResponse(400, "{\"error\":\"Title is required\"}");
        }

        if (insertItems.getCategory() == null) {
            return HttpResponse.buildResponse(400, "{\"error\":\"Category is required\"}");
        }

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
            return HttpResponse.buildResponse(400, "{\"error\":\"Invalid or missing status value\"}");
        }
        boolean result = missionDao.updateStatus(id, insertItems.getStatus().name().toLowerCase());
        if (result) {
            return HttpResponse.buildResponse(200, "{\"message\":\"Mission status updated successfully\"}");
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

package org.shield.model;

import java.time.LocalDateTime;

public class Mission {
    private int missionId;
    private String title;
    private String description;
    private MissionStatus status;
    private MissionPriority priority;
    private MissionCategory category;
    private Integer agentId;
    private LocalDateTime createdAt;
    private String agentName;

    public Mission(String title, String description, MissionStatus status,
                   MissionPriority priority, MissionCategory category, Integer agentId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.agentId = agentId;
    }

    public Mission(int missionId, String title, String description, MissionStatus status,
                   MissionPriority priority, Integer agentId, MissionCategory category,
                   LocalDateTime createdAt, String agentName) {
        this.missionId = missionId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.agentId = agentId;
        this.createdAt = createdAt;
        this.agentName = agentName;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public int getMissionId() {
        return missionId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public MissionPriority getPriority() {
        return priority;
    }

    public MissionCategory getCategory() {
        return category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public void setPriority(MissionPriority priority) {
        this.priority = priority;
    }

    public void setCategory(MissionCategory category) {
        this.category = category;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }
}

package org.shield.model;

import java.time.LocalDateTime;

public class Mission {
    private int missionId;
    private String title;
    private String description;
    private MissionStatus status;
    private MissionPriority priority;
    private MissionCategory category;
    private Integer heroId;
    private LocalDateTime createdAt;
    private String heroName;

    public Mission(String title, String description, MissionStatus status,
                   MissionPriority priority, MissionCategory category, Integer heroId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.heroId = heroId;
    }

    public Mission(int missionId, String title, String description, MissionStatus status,
                   MissionPriority priority, Integer heroId, MissionCategory category,
                   LocalDateTime createdAt, String heroName) {
        this.missionId = missionId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.heroId = heroId;
        this.category = category;
        this.createdAt = createdAt;
        this.heroName = heroName;
    }

    public String getHeroName() {
        return heroName;
    }

    public void setHeroName(String heroName) {
        this.heroName = heroName;
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

    public Integer getHeroId() {
        return heroId;
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

    public void setHeroId(Integer heroId) {
        this.heroId = heroId;
    }
}

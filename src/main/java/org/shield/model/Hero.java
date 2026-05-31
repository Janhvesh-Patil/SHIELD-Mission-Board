package org.shield.model;

public class Hero {
    private int heroId;
    private String heroName;

    public Hero(int heroId, String heroName) {
        this.heroId = heroId;
        this.heroName = heroName;
    }

    public int getHeroId() {
        return heroId;
    }

    public String getHeroName() {
        return heroName;
    }
}

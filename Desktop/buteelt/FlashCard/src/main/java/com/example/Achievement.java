package com.example;

public class Achievement {
    private String name;
    private String description;
    private boolean achieved;

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
        this.achieved = false;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isAchieved() { return achieved; }
    public void setAchieved(boolean achieved) { this.achieved = achieved; }
}

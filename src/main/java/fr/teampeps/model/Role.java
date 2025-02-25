package fr.teampeps.model;

public enum Role {

    DPS("DPS"),
    TANK("Tank"),
    SUPPORT("Support"),
    COACH("Coach"),
    MANAGER("Manager");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

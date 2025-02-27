package fr.teampeps.model;

public enum Role {

    PLAYER("PLAYER"),
    DPS("DPS"),
    TANK("TANK"),
    SUPPORT("SUPPORT"),
    COACH("COACH"),
    MANAGER("MANAGER");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

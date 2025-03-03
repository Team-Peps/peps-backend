package fr.teampeps.model.member;

public enum MemberRole {

    PLAYER("PLAYER"),
    DPS("DPS"),
    TANK("TANK"),
    SUPPORT("SUPPORT"),
    COACH("COACH"),
    MANAGER("MANAGER");

    private final String name;

    MemberRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

package fr.teampeps.model;

public enum Game {
    OVERWATCH("overwatch"),
    MARVEL_RIVALS("marvel-rivals");

    private final String name;

    Game(String name) {
        this.name = name;
    }
}

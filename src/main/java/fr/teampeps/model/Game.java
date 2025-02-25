package fr.teampeps.model;

public enum Game {

    OVERWATCH("Overwatch"),
    MARVEL_RIVAL("Marvel Rival");

    private final String name;

    Game(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

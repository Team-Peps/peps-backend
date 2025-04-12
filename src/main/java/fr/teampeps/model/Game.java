package fr.teampeps.model;

import fr.teampeps.model.article.ArticleType;

public enum Game {
    OVERWATCH("overwatch"),
    MARVEL_RIVALS("marvel-rivals");

    private final String name;

    Game(String name) {
        this.name = name;
    }

    public static boolean contains(String name) {
        for (Game game : Game.values()) {
            if (game.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}

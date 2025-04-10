package fr.teampeps.model.article;

public enum ArticleType {
    OVERWATCH,
    MARVEL_RIVALS,
    TEAM_PEPS;

    public static boolean contains(String name) {
        if(name.equals("ALL")) {
            return true;
        }
        for (ArticleType type : ArticleType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}

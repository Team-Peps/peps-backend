package fr.teampeps.model;

import lombok.Getter;

@Getter
public enum MatchType {

    FT1("Le meilleur de 2"),
    FT2("Le meilleur des 3"),
    FT3("Le meilleur des 5");

    private final String description;

    MatchType(String description) {
        this.description = description;
    }

}

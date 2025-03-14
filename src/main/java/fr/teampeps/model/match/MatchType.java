package fr.teampeps.model.match;

import lombok.Getter;

@Getter
public enum MatchType {

    FT1("FT1"),
    FT2("FT2"),
    FT3("FT3");

    private final String description;

    MatchType(String description) {
        this.description = description;
    }

}

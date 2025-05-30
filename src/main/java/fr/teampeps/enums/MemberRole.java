package fr.teampeps.enums;

import lombok.Getter;

@Getter
public enum MemberRole {

    DAMAGE("DAMAGE"),
    TANK("TANK"),
    SUPPORT("SUPPORT"),
    DUELIST("DUELIST"),
    STRATEGIST("STRATEGIST"),
    VANGUARD("VANGUARD"),
    COACH("COACH");

    private final String name;

    MemberRole(String name) {
        this.name = name;
    }

}

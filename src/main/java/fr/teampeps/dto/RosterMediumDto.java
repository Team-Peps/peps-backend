package fr.teampeps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RosterMediumDto {

    private String id;
    private String name;
    private String game;
    private List<MemberMediumDto> members;
    private String imageKey;
    private Long matchCount;
    private boolean isOpponent;

}



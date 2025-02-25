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
public class RosterDto {

    private String id;
    private String name;
    private GameDto game;
    private List<MemberDto> members;
}

package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoDto {
    private String id;
    private String link;
    private String imageKey;
}

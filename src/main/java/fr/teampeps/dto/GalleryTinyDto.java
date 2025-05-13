package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GalleryTinyDto {
    private String id;
    private String eventName;
    private String date;
    private String description;
    private List<String> authors;
}

package fr.teampeps.mapper;

import fr.teampeps.dto.AmbassadorDto;
import fr.teampeps.models.Ambassador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AmbassadorMapper {

    public AmbassadorDto toAmbassadorDto(Ambassador ambassador) {
        return AmbassadorDto.builder()
                .id(ambassador.getId())
                .name(ambassador.getName())
                .description(ambassador.getDescription())
                .twitchUsername(ambassador.getTwitchUsername())
                .youtubeUsername(ambassador.getYoutubeUsername())
                .tiktokUsername(ambassador.getTiktokUsername())
                .instagramUsername(ambassador.getInstagramUsername())
                .twitterXUsername(ambassador.getTwitterXUsername())
                .imageKey(ambassador.getImageKey())
                .build();
    }

}

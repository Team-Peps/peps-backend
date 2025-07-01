package fr.teampeps.mapper;

import fr.teampeps.dto.AmbassadorDto;
import fr.teampeps.dto.AmbassadorTranslationDto;
import fr.teampeps.models.Ambassador;
import fr.teampeps.models.AmbassadorTranslation;
import fr.teampeps.record.AmbassadorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AmbassadorMapper {

    public AmbassadorDto toAmbassadorDto(Ambassador ambassador) {
        Map<String, AmbassadorTranslationDto> translationsDto = ambassador.getTranslations().stream()
                .collect(Collectors.toMap(
                        AmbassadorTranslation::getLang,
                        t -> AmbassadorTranslationDto.builder()
                                .description(t.getDescription())
                                .build()
                ));
        
        return AmbassadorDto.builder()
                .id(ambassador.getId())
                .name(ambassador.getName())
                .twitchUsername(ambassador.getTwitchUsername())
                .youtubeUsername(ambassador.getYoutubeUsername())
                .tiktokUsername(ambassador.getTiktokUsername())
                .instagramUsername(ambassador.getInstagramUsername())
                .twitterXUsername(ambassador.getTwitterXUsername())
                .imageKey(ambassador.getImageKey())
                .translations(translationsDto)
                .build();
    }

    public Ambassador toAmbassador(AmbassadorRequest ambassadorRequest) {
        return Ambassador.builder()
                .id(ambassadorRequest.id() != null ? ambassadorRequest.id() : UUID.randomUUID().toString())
                .name(ambassadorRequest.name())
                .imageKey(ambassadorRequest.imageKey())
                .twitterXUsername(ambassadorRequest.twitterXUsername())
                .instagramUsername(ambassadorRequest.instagramUsername())
                .tiktokUsername(ambassadorRequest.tiktokUsername())
                .youtubeUsername(ambassadorRequest.youtubeUsername())
                .twitchUsername(ambassadorRequest.twitchUsername())
                .translations(ambassadorRequest.translations().entrySet().stream()
                        .map(entry -> {
                            AmbassadorTranslation translation = new AmbassadorTranslation();
                            translation.setLang(entry.getKey());
                            translation.setDescription(entry.getValue().description());
                            return translation;
                        }).collect(Collectors.toList()))
                .build();
    }

}

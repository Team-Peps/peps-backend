package fr.teampeps.mapper;

import fr.teampeps.dto.PartnerCodeDto;
import fr.teampeps.dto.PartnerDto;
import fr.teampeps.dto.PartnerTranslationDto;
import fr.teampeps.models.Partner;
import fr.teampeps.models.PartnerCode;
import fr.teampeps.models.PartnerTranslation;
import fr.teampeps.record.PartnerRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerMapper {

    public PartnerDto toPartnerDto(Partner partner) {
        Map<String, PartnerTranslationDto> translationsDto = partner.getTranslations().stream()
                .collect(Collectors.toMap(
                        PartnerTranslation::getLang,
                        t -> PartnerTranslationDto.builder()
                                .description(t.getDescription())
                                .build()
                ));

        return PartnerDto.builder()
                .id(partner.getId())
                .name(partner.getName())
                .imageKey(partner.getImageKey())
                .link(partner.getLink())
                .isActive(partner.getIsActive())
                .order(partner.getOrder())
                .type(partner.getType().name())
                .translations(translationsDto)
                .codes(toPartnerCodeDto(partner.getCodes()))
                .build();
    }

    public Partner toPartner(PartnerRequest partnerRequest) {
        return Partner.builder()
                .id(partnerRequest.id())
                .order(partnerRequest.order())
                .name(partnerRequest.name())
                .link(partnerRequest.link())
                .type(partnerRequest.type())
                .isActive(partnerRequest.isActive())
                .imageKey(partnerRequest.imageKey())
                .codes(partnerRequest.codes())
                .translations(partnerRequest.translations().entrySet().stream()
                        .map(entry -> {
                            PartnerTranslation translation = new PartnerTranslation();
                            translation.setLang(entry.getKey());
                            translation.setDescription(entry.getValue().description());
                            return translation;
                        }).collect(Collectors.toList()))
                .build();
    }

    private List<PartnerCodeDto> toPartnerCodeDto(List<PartnerCode> partnerCode) {
        return partnerCode.stream()
                .map(code -> PartnerCodeDto.builder()
                        .id(code.getId())
                        .code(code.getCode())
                        .descriptionEn(code.getDescriptionEn())
                        .descriptionFr(code.getDescriptionFr())
                        .build())
                .collect(Collectors.toList());
    }
}

package fr.teampeps.mapper;

import fr.teampeps.dto.PartnerDto;
import fr.teampeps.models.Partner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PartnerMapper {

    public PartnerDto toPartnerDto(Partner partner) {
        return PartnerDto.builder()
                .id(partner.getId())
                .name(partner.getName())
                .description(partner.getDescription())
                .imageKey(partner.getImageKey())
                .link(partner.getLink())
                .codes(partner.getCodes() != null ? partner.getCodes() : List.of())
                .isActive(partner.getIsActive())
                .order(partner.getOrder())
                .type(partner.getType().name())
                .build();
    }

}

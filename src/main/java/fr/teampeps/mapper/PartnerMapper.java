package fr.teampeps.mapper;

import fr.teampeps.dto.PartnerDto;
import fr.teampeps.model.Partner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
                .codes(Arrays.asList(partner.getCodes().split(",")))
                .isActive(partner.getIsActive())
                .build();
    }

}

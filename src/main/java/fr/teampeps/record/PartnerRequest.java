package fr.teampeps.record;

import fr.teampeps.enums.PartnerType;
import fr.teampeps.models.PartnerCode;

import java.util.List;
import java.util.Map;

public record PartnerRequest(
        String id,
        String name,
        String imageKey,
        String link,
        Boolean isActive,
        Long order,
        PartnerType type,
        List<PartnerCode> codes,
        Map<String, PartnerTranslationRequest> translations
) {
}

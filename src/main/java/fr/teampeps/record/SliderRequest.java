package fr.teampeps.record;

import java.util.Map;

public record SliderRequest(
        String id,
        String ctaLink,
        boolean isActive,
        Map<String, SliderTranslationRequest> translations
) {}

package fr.teampeps.service;

import fr.teampeps.dto.PartnerDto;
import fr.teampeps.enums.PartnerType;
import fr.teampeps.mapper.PartnerMapper;
import fr.teampeps.enums.Bucket;
import fr.teampeps.models.Partner;
import fr.teampeps.models.PartnerCode;
import fr.teampeps.models.PartnerTranslation;
import fr.teampeps.record.PartnerRequest;
import fr.teampeps.repository.PartnerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;
    private final MinioService minioService;

    public Map<String, List<PartnerDto>> getAllPartners() {

        List<PartnerDto> partnersActive = partnerRepository.findAllByIsActive(true).stream()
                .map(partnerMapper::toPartnerDto)
                .toList();

        List<PartnerDto> partnersInactive = partnerRepository.findAllByIsActive(false).stream()
                .map(partnerMapper::toPartnerDto)
                .toList();

        return Map.of(
                "activePartners", partnersActive,
                "inactivePartners", partnersInactive
        );
    }

    public PartnerDto savePartner(
            PartnerRequest partnerRequest,
            MultipartFile imageFile
    ) {

        if(imageFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image non fournie");
        }

        Partner partner = partnerMapper.toPartner(partnerRequest);

        try {
            String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, partner.getName().toLowerCase(), Bucket.PARTNERS);
            partner.setImageKey(imageUrl);

            long order = partnerRepository.count();
            partner.setOrder(order);

            partner.getCodes().removeIf(code ->
                    code.getCode() == null || code.getCode().isEmpty() ||
                    code.getDescriptionEn() == null || code.getDescriptionEn().isEmpty() ||
                    code.getDescriptionFr() == null || code.getDescriptionFr().isEmpty()
            );
            partner.getCodes().forEach(code -> {
                code.setPartner(partner);
            });
            List<PartnerTranslation> validTranslations = partner.getTranslations().stream()
                    .filter(t -> t.getLang() != null && !t.getLang().isBlank() && t.getDescription() != null && !t.getDescription().isBlank())
                    .peek(partnerTranslation -> partnerTranslation.setParent(partner))
                    .toList();
            partner.setTranslations(validTranslations);

            return partnerMapper.toPartnerDto(partnerRepository.save(partner));

        } catch (Exception e) {
            log.error("Error saving partner with ID: {}", partner.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du partnenaire", e);
        }
    }

    public PartnerDto updatePartner(
            PartnerRequest partnerRequest,
            MultipartFile imageFile
    ) {

        Partner existingPartner = partnerRepository.findById(partnerRequest.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partenaire non trouvé"));

        Map<String, PartnerTranslation> translationsByLang = existingPartner.getTranslations().stream()
                .collect(Collectors.toMap(PartnerTranslation::getLang, Function.identity()));

        partnerRequest.translations().forEach((lang, tRequest) -> {
            PartnerTranslation translation = translationsByLang.get(lang.toLowerCase());
            if (translation != null) {
                translation.setDescription(tRequest.description());
            }
        });

        List<PartnerCode> validCodes = partnerRequest.codes().stream()
            .filter(
    code -> code.getCode() != null && !code.getCode().isEmpty() &&
                code.getDescriptionEn() != null && !code.getDescriptionEn().isEmpty() &&
                code.getDescriptionFr() != null && !code.getDescriptionFr().isEmpty()
            )
            .map(code -> {
                PartnerCode partnerCode = new PartnerCode();
                partnerCode.setCode(code.getCode());
                partnerCode.setDescriptionEn(code.getDescriptionEn());
                partnerCode.setDescriptionFr(code.getDescriptionFr());
                partnerCode.setPartner(existingPartner);
                return partnerCode;
            })
            .toList();

        existingPartner.setName(partnerRequest.name());
        existingPartner.setType(partnerRequest.type());
        existingPartner.setOrder(partnerRequest.order());
        existingPartner.setIsActive(partnerRequest.isActive());
        existingPartner.setLink(partnerRequest.link());
        existingPartner.getCodes().clear();
        existingPartner.getCodes().addAll(validCodes);

        try {
            if(imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, existingPartner.getName().toLowerCase(), Bucket.PARTNERS);
                existingPartner.setImageKey(imageUrl);
            }
            return partnerMapper.toPartnerDto(partnerRepository.save(existingPartner));

        } catch (Exception e) {
            log.error("Error saving partner with ID: {}", existingPartner.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du partnenaire", e);
        }
    }

    public void deletePartner(String id) {
        try {
            partnerRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Partenaire non trouvé", e);
        }
    }

    public PartnerDto toggleActive(String id) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partenaire non trouvé"));

        partner.setIsActive(!partner.getIsActive());

        return partnerMapper.toPartnerDto(partnerRepository.save(partner));
    }

    public Map<String, List<PartnerDto>> getAllActivePartners() {
        Map<String, List<PartnerDto>> activePartners = new HashMap<>();

        List<PartnerDto> partnersMajor = partnerRepository.findAllByIsActiveAndPartnerType(true, PartnerType.MAJOR)
                .stream()
                .map(partnerMapper::toPartnerDto)
                .toList();
        activePartners.put(PartnerType.MAJOR.name(), partnersMajor);

        List<PartnerDto> partnersMinor = partnerRepository.findAllByIsActiveAndPartnerType(true, PartnerType.MINOR)
                .stream()
                .map(partnerMapper::toPartnerDto)
                .toList();
        activePartners.put(PartnerType.MINOR.name(), partnersMinor);

        return activePartners;
    }

    public void updatePartnerOrder(List<String> orderedIds) {
        for (int i = 0; i < orderedIds.size(); i++) {
            String id = orderedIds.get(i);
            int finalI = i;
            partnerRepository.findById(id).ifPresent(partner -> {
                partner.setOrder((long) finalI);
                partnerRepository.save(partner);
            });
        }
    }
}

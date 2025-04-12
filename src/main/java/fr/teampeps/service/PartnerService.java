package fr.teampeps.service;

import fr.teampeps.dto.PartnerDto;
import fr.teampeps.mapper.PartnerMapper;
import fr.teampeps.model.Bucket;
import fr.teampeps.model.Partner;
import fr.teampeps.repository.PartnerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

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

    public PartnerDto saveOrUpdatePartner(Partner partner, MultipartFile imageFile) {
        log.info("Updating partner : {}", partner);

        try {
            if (imageFile != null) {
                String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, partner.getName().toLowerCase(), Bucket.PARTNERS);
                partner.setImageKey(imageUrl);
            }

            return partnerMapper.toPartnerDto(partnerRepository.save(partner));

        } catch (Exception e) {
            log.error("Error saving partner with ID: {}", partner.getId(), e);
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

    public List<PartnerDto> getAllActivePartners() {
        return partnerRepository.findAllByIsActive(true).stream()
                .map(partnerMapper::toPartnerDto)
                .toList();
    }
}

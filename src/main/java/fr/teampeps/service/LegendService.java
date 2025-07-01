package fr.teampeps.service;

import fr.teampeps.models.Legend;
import fr.teampeps.repository.LegendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LegendService {

    private final LegendRepository legendRepository;

    public List<String> getAllLegends() {
        log.info("Fetching all legends");
        return legendRepository.findAll().stream()
                .map(Legend::getName)
                .toList();
    }

    public void addLegends(List<String> legends) {
        log.info("Removing existing legends");
        legendRepository.deleteAll();

        log.info("Adding legends: {}", legends);
        List<Legend> legendObject = legends.stream()
                .map(legendName -> Legend.builder().name(legendName).build())
                .toList();
        legendRepository.saveAll(legendObject);
        log.info("Legends added successfully");
    }
}

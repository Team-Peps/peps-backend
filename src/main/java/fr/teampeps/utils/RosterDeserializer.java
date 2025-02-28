package fr.teampeps.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.teampeps.model.Roster;
import fr.teampeps.repository.RosterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RosterDeserializer extends JsonDeserializer<Roster> {

    private final RosterRepository rosterRepository;

    @Override
    public Roster deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String rosterId = p.getText();
        return rosterRepository.findById(rosterId).orElseThrow();
    }
}

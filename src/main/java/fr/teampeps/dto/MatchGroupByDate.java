package fr.teampeps.dto;

import java.time.LocalDate;
import java.util.List;

public record MatchGroupByDate(LocalDate date, List<MatchDto> matches) { }

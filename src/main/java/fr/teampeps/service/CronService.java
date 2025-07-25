package fr.teampeps.service;

import fr.teampeps.exceptions.DateParsingException;
import fr.teampeps.exceptions.ImageUploadException;
import fr.teampeps.enums.Bucket;
import fr.teampeps.enums.Game;
import fr.teampeps.models.Match;
import fr.teampeps.record.ImageData;
import fr.teampeps.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CronService {

    private final MinioService minioService;
    private final MatchRepository matchRepository;

    @Value("${url.stream.overwatch}")
    private String urlStreamOverwatch;

    @Value("${url.stream.marvel-rivals}")
    private String urlStreamMarvelRivals;

    private static final String BASE_URL = "https://liquipedia.net";
    private static final String USER_AGENT = "TeamPepsBot/1.0 (https://teampeps.fr contact@teampeps.fr)";

    @Scheduled(cron = "${cron.expression}")
    public Map<String, List<Match>> fetchAndSaveMatches() {
        List<Match> marvelRivalsMatches = fetchAndSavePlayedMatches(Game.MARVEL_RIVALS, "/marvelrivals/Team_Peps/Played_Matches", urlStreamMarvelRivals);
        List<Match> overwatchMatches = fetchAndSavePlayedMatches(Game.OVERWATCH, "/overwatch/Team_Peps/Played_Matches", urlStreamOverwatch);

        List<Match> upcomingOverwatchMatches = fetchAndSaveUpcomingMatches(Game.OVERWATCH, "/overwatch/Team_Peps", urlStreamOverwatch);
        List<Match> upcomingMarvelRivalsMatches = fetchAndSaveUpcomingMatches(Game.MARVEL_RIVALS, "/marvelrivals/Team_Peps", urlStreamMarvelRivals);

        List<Match> playedMatches = Stream.concat(overwatchMatches.stream(), marvelRivalsMatches.stream()).toList();

        List<Match> upcomingMatches = Stream.concat(upcomingOverwatchMatches.stream(), upcomingMarvelRivalsMatches.stream()).toList();

        log.info("Found {} overwatch matches", overwatchMatches.size());
        log.info("Found {} marvel rivals matches", marvelRivalsMatches.size());

        log.info("Found {} upcoming overwatch matches", upcomingOverwatchMatches.size());
        log.info("Found {} upcoming marvel rivals matches", upcomingMarvelRivalsMatches.size());

        return Map.of(
                "upcoming", upcomingMatches,
                "played", playedMatches
        );

    }

    public void fetchAndSaveMatchesManually(SseEmitter emitter) {
        try {

            emitter.send("Suppression des matchs avec un score null...");
            matchRepository.deleteAllWhereScoreIsNull();

            emitter.send("Début de la récupération des matchs...");

            List<Match> overwatchMatches = fetchAndSavePlayedMatches(Game.OVERWATCH, "/overwatch/Team_Peps/Played_Matches", urlStreamOverwatch);
            emitter.send("Récupération des matchs joués Overwatch terminée");

            List<Match> marvelRivalsMatches = fetchAndSavePlayedMatches(Game.MARVEL_RIVALS, "/marvelrivals/Team_Peps/Played_Matches", urlStreamMarvelRivals);
            emitter.send("Récupération des matchs joués Marvel Rivals terminée");

            List<Match> upcomingOverwatchMatches = fetchAndSaveUpcomingMatches(Game.OVERWATCH, "/overwatch/Team_Peps", urlStreamOverwatch);
            emitter.send("Récupération des matchs à venir Overwatch terminée");

            List<Match> upcomingMarvelRivalsMatches = fetchAndSaveUpcomingMatches(Game.MARVEL_RIVALS, "/marvelrivals/Team_Peps", urlStreamMarvelRivals);
            emitter.send("Récupération des matchs à venir Marvel Rivals terminée");

            emitter.send("Tous les matchs ont été récupérés et sauvegardés avec succès");
            emitter.complete();

            log.info("Found {} overwatch matches", overwatchMatches.size());
            log.info("Found {} marvel rivals matches", marvelRivalsMatches.size());

            log.info("Found {} upcoming overwatch matches", upcomingOverwatchMatches.size());
            log.info("Found {} upcoming marvel rivals matches", upcomingMarvelRivalsMatches.size());

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'événement SSE: {}", e.getMessage());
            try {
                if(e instanceof DateParsingException) {
                    emitter.send("Erreur (NON BLOQUANTE) de parsing de date: " + e.getMessage());
                } else if(e instanceof ImageUploadException) {
                    emitter.send("Erreur (BLOQUANTE) de téléchargement ou d'upload d'image: " + e.getMessage());
                } else {
                    emitter.send("Une erreur s'est produite: " + e.getMessage());
                }
            } catch (IOException ioException) {
                log.error("Erreur lors de l'envoi de l'erreur SSE: {}", ioException.getMessage());
            }
            emitter.complete();
        }
    }


    protected List<Match> fetchAndSaveUpcomingMatches(Game game, String url, String streamUrl) {
        log.info("Fetching upcoming matches...");

        String fetchUrl = BASE_URL + url;

        try {
            Document doc = Jsoup.connect(fetchUrl)
                    .userAgent(USER_AGENT)
                    .get();
            HttpClient client = HttpClient.newHttpClient();
            List<Match> matches = new ArrayList<>();

            Elements tables = doc.select("div.panel").select("table.wikitable.infobox_matches_content");

            if (tables.isEmpty()) {
                log.info("No matches found");
                return matches;
            }

            for (Element table : tables) {
                Element firstRow = table.select("tr").first();
                Element lastRow = table.select("tr").last();

                if(firstRow == null || lastRow == null) {
                    continue;
                }

                Elements colsFirstRow = firstRow.select("td");
                Elements colsLastRow = lastRow.select("td");

                if (colsFirstRow.size() < 3 || colsLastRow.isEmpty()) {
                    continue;
                }

                String date = colsLastRow.get(0).select("span.timer-object").text();

                String competitionLogo = Objects.requireNonNull(colsLastRow.get(0).selectFirst("img")).attr("src");
                String competitionName = Objects.requireNonNull(colsLastRow.get(0).selectFirst("div.tournament-text-flex")).text();

                String opponent = colsFirstRow.get(2).text();
                String opponentLogo = Objects.requireNonNull(colsFirstRow.get(2).selectFirst("img")).attr("src");

                ImageData competitionLogoRecord;
                ImageData opponentLogoRecord;

                LocalDateTime parsedDate = parseToDateTime(date);
                String matchId = generateMatchId(parsedDate.toLocalDate(), game.getName(), opponent);

                if(parsedDate.isBefore(LocalDateTime.now()) || matchRepository.existsById(matchId)) {
                    log.info("Match {} is in the past or already exist: {}", matchId, date);
                    continue;
                }

                competitionLogoRecord = downloadAndUploadImage(BASE_URL + competitionLogo, Bucket.COMPETITIONS, reformatString(competitionName), client);
                opponentLogoRecord = downloadAndUploadImage(BASE_URL + opponentLogo, Bucket.OPPONENTS, reformatString(opponent), client);

                Match match = Match.builder()
                        .id(matchId)
                        .datetime(parsedDate)
                        .competitionName(competitionName)
                        .opponent(opponent)
                        .competitionImageKey(competitionLogoRecord.imageKey())
                        .competitionImageWidth(competitionLogoRecord.width())
                        .competitionImageHeight(competitionLogoRecord.height())
                        .opponentImageKey(opponentLogoRecord.imageKey())
                        .opponentImageWidth(opponentLogoRecord.width())
                        .opponentImageHeight(opponentLogoRecord.height())
                        .game(game)
                        .streamUrl(streamUrl)
                        .build();

                matches.add(match);
            }

            matchRepository.saveAll(matches);
            return matches;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected List<Match> fetchAndSavePlayedMatches(Game game, String url, String streamUrl) {

        log.info("Fetching played matches...");

        String fetchUrl = BASE_URL + url;

        try {
            Document doc = Jsoup.connect(fetchUrl)
                    .userAgent(USER_AGENT)
                    .get();
            HttpClient client = HttpClient.newHttpClient();
            List<Match> matches = new ArrayList<>();

            Element table = doc.selectFirst("table.wikitable");
            if (table == null) {
                log.info("No matches found");
                return matches;
            }

            Elements rows = table.select("tr");

            for (Element row : rows) {
                Elements cols = row.select("td");
                if (cols.size() < 5) continue;

                String date = cols.get(0).text();
                String competitionLogo = Objects.requireNonNull(cols.get(2).selectFirst("img")).attr("src");
                String competitionName = cols.get(3).text();
                String score = cols.get(4).text();
                String opponent = cols.get(5).text();
                String opponentLogo = Objects.requireNonNull(cols.get(5).selectFirst("img")).attr("src");

                ImageData competitionLogoRecord;
                ImageData opponentLogoRecord;

                String[] scores = score.split(":");
                LocalDateTime parsedDate = parseToDateTime(date);

                String matchId = generateMatchId(parsedDate.toLocalDate(), game.getName(), opponent);

                if(matchRepository.existsById(matchId) && matchRepository.isMatchScoreIsNull(matchId).isEmpty()) {
                    log.info("Match already exists and updating score: {}", matchId);
                    Match match = matchRepository.findById(matchId)
                            .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));
                    match.setScore(scores[0]);
                    match.setOpponentScore(scores[1]);
                    matches.add(match);

                }else{

                    competitionLogoRecord = downloadAndUploadImage(BASE_URL + competitionLogo, Bucket.COMPETITIONS, reformatString(competitionName), client);
                    opponentLogoRecord = downloadAndUploadImage(BASE_URL + opponentLogo, Bucket.OPPONENTS, reformatString(opponent), client);
                    Match match = Match.builder()
                            .id(matchId)
                            .datetime(parsedDate)
                            .competitionName(competitionName)
                            .score(scores[0])
                            .opponentScore(scores[1])
                            .opponent(opponent)
                            .competitionImageKey(competitionLogoRecord.imageKey())
                            .competitionImageWidth(competitionLogoRecord.width())
                            .competitionImageHeight(competitionLogoRecord.height())
                            .opponentImageKey(opponentLogoRecord.imageKey())
                            .opponentImageWidth(opponentLogoRecord.width())
                            .opponentImageHeight(opponentLogoRecord.height())
                            .game(game)
                            .streamUrl(streamUrl)
                            .build();
                    matches.add(match);

                }

            }
            matchRepository.saveAll(matches);
            return matches;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ImageData downloadAndUploadImage(String url, Bucket bucket, String fileName, HttpClient client) {
        try {
            HttpRequest imgRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .build();

            log.info("Downloading image from URL: {}", url);

            HttpResponse<byte[]> imgResponse = client.send(imgRequest, HttpResponse.BodyHandlers.ofByteArray());
            byte[] imageBytes = imgResponse.body();

            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

            int width = img.getWidth();
            int height = img.getHeight();
            log.info("Image dimensions: {}x{}", width, height);

            String extension = url.substring(url.lastIndexOf('.'));

            return new ImageData(minioService.uploadImageFromBytes(imgResponse.body(), fileName, extension, bucket), width, height);
        } catch (IOException | InterruptedException e) {
            if(e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new ImageUploadException("❌ Échec du téléchargement ou de l'upload de l'image depuis l'URL: " + url, e);
        }
    }

    private LocalDateTime parseToDateTime(String date) {
        String[] patterns = {
                "MMM d, yyyy - HH:mm z",
                "MMMM d, yyyy - HH:mm z",
                "MMM d, yyyy",
                "MMMM d, yyyy"
        };

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
                if (pattern.contains("z")) {
                    ZonedDateTime zdt = ZonedDateTime.parse(date, formatter);
                    return zdt.toLocalDateTime();
                } else {
                    LocalDate localDate = LocalDate.parse(date, formatter);
                    return LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
                }
            } catch (DateTimeParseException e) {
                log.error("Error parsing date: {} with pattern: {}", date, pattern);
            }
        }
        throw new DateParsingException("❌ Impossible de parser la date : " + date);
    }

    private String reformatString(String str) {
        return str.toLowerCase().replaceAll("[^a-zA-Z0-9_-]", "");
    }

    private String generateMatchId(LocalDate date, String game, String opponent) {
        return (game + "-Team-Peps-vs-" + opponent + "-" + date).toLowerCase();
    }

}
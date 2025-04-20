package fr.teampeps.security.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/auth/discord")
@RequiredArgsConstructor
public class DiscordAuthController {

    private static final String LOCATION_PLACEHOLDER = "Location";

    @Value("${discord.client-id}")
    private String clientId;

    @Value("${discord.client-secret}")
    private String clientSecret;

    @Value("${discord.redirect-uri}")
    private String redirectUri;

    @Value("${frontend.redirect-uri}")
    private String frontendRedirect;

    private final RestTemplate restTemplate = new RestTemplate();
    private final AuthenticationService authenticationService;

    @GetMapping("/login")
    public ResponseEntity<?> loginWithDiscord() {
        String discordUrl = "https://discord.com/api/oauth2/authorize" +
                "?response_type=code" +
                "&client_id=" + clientId +
                "&scope=identify%20email" +
                "&redirect_uri=" + redirectUri;
        return ResponseEntity.status(HttpStatus.FOUND).header(LOCATION_PLACEHOLDER, discordUrl).build();
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam String code) {
        // Étape 1 : Récupère access_token depuis Discord
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        params.add("scope", "identify email");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://discord.com/api/oauth2/token",
                request,
                Map.class
        );

        String accessToken = (String) Objects.requireNonNull(response.getBody()).get("access_token");

        // Étape 2 : Utilise access_token pour récupérer l’utilisateur
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userInfo = restTemplate.exchange(
                "https://discord.com/api/users/@me",
                HttpMethod.GET,
                userRequest,
                Map.class
        );

        String discordId = (String) Objects.requireNonNull(userInfo.getBody()).get("id");

        if (!authenticationService.isUserRegistered(discordId)) {

            String email = (String) userInfo.getBody().get("email");
            String avatarUrl = (String) userInfo.getBody().get("avatar");
            String username = (String) userInfo.getBody().get("username");

            authenticationService.registerDiscord(
                    DiscordRegisterRequest.builder()
                            .discordId(discordId)
                            .username(username)
                            .avatarUrl(avatarUrl)
                            .email(email)
                            .build());
        }

        Optional<AuthenticationResponse> authResponseOpt = authenticationService.authenticateDiscord(discordId);

        if (authResponseOpt.isEmpty()) {
            // tu peux rediriger vers une page d’erreur front, ou afficher une info
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(LOCATION_PLACEHOLDER, frontendRedirect + "?error=unauthorized")
                    .build();
        }

        AuthenticationResponse authResponse = authResponseOpt.get();

        String redirectUrl = UriComponentsBuilder
                .fromHttpUrl(frontendRedirect)
                .queryParam("access_token", authResponse.getAccessToken())
                .queryParam("refresh_token", authResponse.getRefreshToken())
                .build()
                .toUriString();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(LOCATION_PLACEHOLDER, redirectUrl)
                .build();

    }
}

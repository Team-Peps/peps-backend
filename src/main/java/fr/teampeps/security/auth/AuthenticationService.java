package fr.teampeps.security.auth;

import fr.teampeps.model.user.AuthType;
import fr.teampeps.model.user.Authority;
import fr.teampeps.model.user.User;
import fr.teampeps.model.token.Token;
import fr.teampeps.model.token.TokenType;
import fr.teampeps.repository.TokenRepository;
import fr.teampeps.repository.UserRepository;
import fr.teampeps.security.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    //Check the email regex (RFC 5322 Official Standard) before check if the user already exist permit to avoid SQL injection
    private final Pattern emailPattern = Pattern.compile("^((?:[A-Za-z0-9!#$%&'*+\\-/=?^_`{|}~]|(?<=^|\\.)\"|\"(?=$|\\.|@)|(?<=\".*)[ .](?=.*\")|(?<!\\.)\\.){1,64})(@)([A-Za-z0-9.\\-]*[A-Za-z0-9]\\.[A-Za-z0-9]{2,})$");

    private final Pattern usernamePattern = Pattern.compile("^[A-Za-z]\\w{2,29}$");

    /**
     * Register a new user.
     *
     * @param request The registration request.
     * @return AuthenticationResponse with access and refresh tokens.
     */
    public boolean register(RegisterRequest request) {

        if(!isValidRegisterRequest(request)) {
            return false;
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .authorities(List.of(Authority.STAFF))
                .authType(AuthType.LOCAL)
                .build();

        userRepository.save(user);
        return true;
    }

    /**
     * Register a new user with Discord.
     *
     * @param request The registration request.
     */
    public void registerDiscord(DiscordRegisterRequest request) {
        if (!isValidDiscordRegisterRequest(request)) {
            return;
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .discordId(request.getDiscordId())
                .authorities(List.of(Authority.USER))
                .avatarUrl(request.getAvatarUrl())
                .authType(AuthType.DISCORD)
                .enable(true)
                .build();

        userRepository.save(user);
    }

    public Optional<AuthenticationResponse> authenticateDiscord(String discordId) {
        Optional<User> userOptional = userRepository.findByDiscordId(discordId);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        boolean isEnabled = user.isEnabled();

        if (!isEnabled) {
            log.info("User with discordId={} is not enabled", discordId);
            return Optional.empty();
        }

        // Génère access + refresh tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Révoque les anciens tokens + enregistre les nouveaux
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        saveUserToken(user, refreshToken);

        return Optional.of(AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
    }

    /**
     * Authenticate a user.
     *
     * @param request The authentication request.
     * @return AuthenticationResponse with access and refresh tokens.
     */
    public Optional<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        Optional<User> userOptional;
        // Authenticate the user based on provided credentials.
        if(request.getEmail() == null){
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            userOptional = userRepository.findByUsername(request.getUsername());
        }else{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            userOptional = userRepository.findByEmail(request.getEmail());
        }

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();


        boolean isEnabled = user.isEnabled();
        boolean hasStaffAuthority = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("STAFF"));

        if (!isEnabled || !hasStaffAuthority) {
            log.info("User is not enabled or does not have the required authorities.");
            return Optional.empty();
        }

        // Generate new JWT and refresh tokens for the user.
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Revoke old tokens and save the new tokens.
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        // Save the tokens and return the response.
        saveUserToken(user, refreshToken);

        return Optional.of(AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build());
    }

    /**
     * Revoke all valid user tokens.
     *
     * @param user The user for whom tokens need to be revoked.
     */
    private void revokeAllUserTokens(User user){
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if(validUserTokens.isEmpty()){
            return;
        }
        // Mark all valid tokens as expired and revoked.
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Save a token for a user.
     *
     * @param savedUser The user for whom the token is saved.
     * @param jwtToken The JWT token.
     */
    private void saveUserToken(User savedUser, String jwtToken) {
        Token token = Token.builder()
                .user(savedUser)
                .hex(jwtToken)
                .type(TokenType.BEARER)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * Refresh the authentication token.
     *
     * @param request  The original HTTP request.
     */
    public Optional<AuthenticationResponse> refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String usernameOrEmail;
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return Optional.empty();
        }
        // Extract and validate the refresh token.
        refreshToken = authHeader.substring(7); // substring after "Bearer "
        usernameOrEmail = jwtService.extractUsernameOrEmail(refreshToken);

        if(usernameOrEmail != null){
            Optional<User> userOptional = userRepository.findByUsername(usernameOrEmail);
            if(userOptional.isEmpty()){
                return Optional.empty();
            }
            User user = userOptional.get();

            boolean isTokenValid = tokenRepository.findByHex(refreshToken)
                    .map(t -> !t.isRevoked() && !t.isExpired())
                    .orElse(false);

            if(jwtService.isTokenValid(refreshToken, user) && isTokenValid){
                // Generate a new access token for the user.
                String accessToken = jwtService.generateToken(user);
                String newRefreshToken = jwtService.generateRefreshToken(user);
                // Revoke old tokens, save the new token, and return the updated token response.
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                saveUserToken(user, newRefreshToken);
                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(newRefreshToken)
                        .build();

                return Optional.of(authResponse);
            }
        }
        return Optional.empty();
    }

    private boolean isValidRegisterRequest(RegisterRequest registerRequest) {
        return emailPattern.matcher(registerRequest.getEmail()).matches() &&
                usernamePattern.matcher(registerRequest.getUsername()).matches() &&
                !registerRequest.getPassword().isEmpty() &&
                userRepository.findByEmail(registerRequest.getEmail()).isEmpty() &&
                userRepository.findByUsername(registerRequest.getUsername()).isEmpty();
    }

    private boolean isValidDiscordRegisterRequest(DiscordRegisterRequest registerRequest) {
        return userRepository.findByDiscordId(registerRequest.getDiscordId()).isEmpty();
    }

    public boolean isUserRegistered(String discordId) {
        return userRepository.findByDiscordId(discordId).isPresent();
    }
}

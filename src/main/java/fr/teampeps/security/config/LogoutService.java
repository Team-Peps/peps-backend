package fr.teampeps.security.config;

import fr.teampeps.models.Token;
import fr.teampeps.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;

    /**
     *
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @param authentication {@link Authentication}
     */
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }

        jwt = authHeader.substring(7); // substring after "Bearer "
        Optional<Token> storedToken = tokenRepository.findByHex(jwt);

        if(storedToken.isPresent()){
            storedToken.get().setExpired(true);
            storedToken.get().setRevoked(true);
            tokenRepository.save(storedToken.get());
        }

    }
}

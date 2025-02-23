package fr.teampeps.security.config;

import fr.teampeps.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    /**
     * This method filters incoming HTTP requests and checks for a valid JWT token in the Authorization header.
     * If a valid token is found, it authenticates the user and sets the security context.
     *
     * @param request      The HTTP request.
     * @param response     The HTTP response.
     * @param filterChain  The filter chain to continue processing the request.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String usernameOrEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // substring after "Bearer "

        usernameOrEmail = jwtService.extractUsernameOrEmail(jwt);

        if (usernameOrEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {// ... and verify if the user is already connected
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(usernameOrEmail);
            boolean isTokenValid = tokenRepository.findByHex(jwt)
                    .map(t -> !t.isRevoked() && !t.isExpired())
                    .orElse(false);

            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);

    }
}

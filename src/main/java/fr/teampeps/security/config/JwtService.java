package fr.teampeps.security.config;

import fr.teampeps.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private Long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private Long refreshExpiration;

    /**
     * Extracts the username or email from a JWT token.
     *
     * @param token The JWT token.
     * @return The username or email extracted from the token.
     */
    public String extractUsernameOrEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from a JWT token.
     *
     * @param token           The JWT token.
     * @param claimsResolver  A function to resolve the desired claim.
     * @return The resolved claim from the token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token for a user with userDetails.
     *
     * @param userDetails     The user's details.
     * @return The generated JWT token.
     */
    public String generateToken(
            User userDetails
    ){
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token with additional claims for a user.
     *
     * @param extractClaims  Additional claims to be included in the token.
     * @param userDetails     The user's details.
     * @return The generated JWT token.
     */
    public String generateToken(
            Map<String, Object> extractClaims,
            User userDetails
    ){
        return buildToken(extractClaims, userDetails, jwtExpiration);
    }

    /**
     * Generates a refresh token for a user.
     *
     * @param userDetails The user's details.
     * @return The generated refresh token.
     */
    public String generateRefreshToken(
            User userDetails
    ){
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }


    private String buildToken(
            Map<String, Object> extractClaims,
            User userDetails,
            long expiration
    ){
        return Jwts.builder()
                .setClaims(extractClaims)
                .claim("authorities", userDetails.getAuthorities())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates if a JWT token is still valid for a specific user.
     *
     * @param token      The JWT token.
     * @param userDetails The user's details.
     * @return `true` if the token is valid for the user, otherwise `false`.
     */
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsernameOrEmail(token);

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token The JWT token.
     * @return All claims extracted from the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

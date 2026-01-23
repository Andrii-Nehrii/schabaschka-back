package schabaschka.security; //changed

import io.jsonwebtoken.Claims; //changed
import io.jsonwebtoken.Jws; //changed
import io.jsonwebtoken.Jwts; //changed
import io.jsonwebtoken.security.Keys; //changed
import org.springframework.beans.factory.annotation.Value; //changed
import org.springframework.stereotype.Component; //changed

import javax.crypto.SecretKey; //changed
import java.nio.charset.StandardCharsets; //changed
import java.time.Instant; //changed
import java.util.Date; //changed

@Component //changed
public class JwtTokenService { //changed

    private final SecretKey secretKey; //changed
    private final long expirationSeconds; //changed

    public JwtTokenService( //changed
                            @Value("${security.jwt.secret:change-me-secret-change-me-secret-change-me-secret}") String secret, //changed
                            @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds //changed
    ) { //changed
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); //changed
        this.expirationSeconds = expirationSeconds; //changed
    } //changed

    public String generateToken(Long userId, String email, String role) { //changed
        if (userId == null || email == null) { //changed
            throw new IllegalArgumentException("userId and email are required to generate token"); //changed
        } //changed

        Instant now = Instant.now(); //changed
        Instant expiresAt = now.plusSeconds(expirationSeconds); //changed

        return Jwts.builder() //changed
                .subject(String.valueOf(userId)) //changed
                .claim("email", email) //changed
                .claim("role", role) //changed
                .issuedAt(Date.from(now)) //changed
                .expiration(Date.from(expiresAt)) //changed
                .signWith(secretKey) //changed
                .compact(); //changed
    } //changed

    public JwtUserData parseToken(String token) { //changed
        if (token == null || token.isBlank()) { //changed
            throw new IllegalArgumentException("token is empty"); //changed
        } //changed

        Jws<Claims> jws = Jwts.parser() //changed
                .verifyWith(secretKey) //changed
                .build() //changed
                .parseSignedClaims(token); //changed

        Claims claims = jws.getPayload(); //changed

        Long userId = Long.parseLong(claims.getSubject()); //changed
        String email = claims.get("email", String.class); //changed
        String role = claims.get("role", String.class); //changed

        return new JwtUserData(userId, email, role); //changed
    } //changed

    public static class JwtUserData { //changed
        private final Long userId; //changed
        private final String email; //changed
        private final String role; //changed

        public JwtUserData(Long userId, String email, String role) { //changed
            this.userId = userId; //changed
            this.email = email; //changed
            this.role = role; //changed
        } //changed

        public Long getUserId() { //changed
            return userId; //changed
        } //changed

        public String getEmail() { //changed
            return email; //changed
        } //changed

        public String getRole() { //changed
            return role; //changed
        } //changed
    } //changed
} //changed

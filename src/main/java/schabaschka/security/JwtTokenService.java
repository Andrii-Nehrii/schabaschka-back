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

@Component
public class JwtTokenService { //changed

    private final SecretKey secretKey;
    private final long expirationSeconds;

    public JwtTokenService(
                            @Value("${security.jwt.secret:change-me-secret-change-me-secret-change-me-secret}") String secret,
                            @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(Long userId, String email, String role) {
        if (userId == null || email == null) {
            throw new IllegalArgumentException("userId and email are required to generate token");
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expirationSeconds);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    public JwtUserData parseToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token is empty");
        }

        Jws<Claims> jws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        Claims claims = jws.getPayload();

        Long userId = Long.parseLong(claims.getSubject());
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        return new JwtUserData(userId, email, role);
    }

    public static class JwtUserData {
        private final Long userId;
        private final String email;
        private final String role;

        public JwtUserData(Long userId, String email, String role) { //changed
            this.userId = userId;
            this.email = email;
            this.role = role;
        }

        public Long getUserId() {
            return userId;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }
    }
}

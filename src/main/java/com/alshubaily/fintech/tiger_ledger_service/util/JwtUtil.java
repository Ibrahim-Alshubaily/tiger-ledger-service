package com.alshubaily.fintech.tiger_ledger_service.util;

import com.alshubaily.fintech.tiger_ledger_service.db.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final SecretKey signingKey;
    private final JwtParser jwtParser;

    @Value("${jwt.expirationMs:86400000}")
    private long jwtExpirationMs;

    public JwtUtil(@Value("${jwt.secret:9d7bfd1ba3e236ae73d8c7c3ddef4ad4}") String jwtSecret) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts.parser().verifyWith(this.signingKey).build();
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(this.signingKey)
                .compact();
    }

    public Claims validateToken(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }
}

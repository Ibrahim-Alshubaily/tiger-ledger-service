package com.alshubaily.fintech.tiger_ledger_service.util;

import com.alshubaily.fintech.tiger_ledger_service.db.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final Key signingKey;
    private final JwtParser jwtParser;

    @Value("${jwt.expirationMs:86400000}")
    private long jwtExpirationMs;

    public JwtUtil(@Value("${jwt.secret:9d7bfd1ba3e236ae73d8c7c3ddef4ad4}") String jwtSecret) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts.parser().setSigningKey(signingKey).build();
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }
}

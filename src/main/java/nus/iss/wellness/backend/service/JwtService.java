package nus.iss.wellness.backend.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

//author: Junior

@Service
public class JwtService {

    private final Key signingKey;
    private final long expirationMillis;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes}") long expirationMinutes) {

        this.signingKey =
                Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        this.expirationMillis =
                expirationMinutes * 60 * 1000;
    }

    // Generate JWT
    public String generateToken(String username, String role) {

        Date now = new Date();

        Date expiry =
                new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    // Parse JWT
    public Claims authenticateToken(String token) {

        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}

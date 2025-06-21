package com.simplegoods.simplegoods.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey signingKey;

    private final long jwtExpirationMs;

    /** Re-usable, thread-safe parser instance pre-configured with the key */
    private final JwtParser jwtParser;

    public JwtUtil(
            @Value("${jwt.secret}")       String jwtSecret,
            @Value("${jwt.expirationMs}") long   jwtExpirationMs
    ) {
        this.signingKey     = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
        this.jwtParser       = Jwts.parser()
                .verifyWith(signingKey)
                .build();
    }

    public String generateAccessToken(Authentication auth) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(auth.getName())        // username
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            jwtParser.parseSignedClaims(token);    // throws on invalid / expired
            return true;
        } catch (Exception ex) {
            return false;                          // signature, format or expiry failure
        }
    }

    private Claims getAllClaims(String token) {
        return jwtParser         // already configured with the key
                .parseSignedClaims(token)
                .getPayload();
    }
}
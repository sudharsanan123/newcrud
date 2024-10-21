package com.example.spring.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.spring.config.Role;
import org.slf4j.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);
    private String secretkey = "";

    public JWTService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
            logger.info("JWTService: [Class: JWTService] Secret key generated successfully.");
        } catch (NoSuchAlgorithmException e) {
            logger.error("JWTService: [Class: JWTService] Error generating secret key", e);
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String username, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Include role in the token

        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1 hour expiration
                .signWith(getKey())
                .compact();
        logger.info("JWTService: [Class: JWTService] Token generated for user: {}", username);
        return token;
    }

    public SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        logger.debug("JWTService: [Class: JWTService] Retrieving secret key.");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        logger.debug("JWTService: [Class: JWTService] Extracting username from token.");
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        logger.debug("JWTService: [Class: JWTService] Extracting claim from token.");
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        logger.debug("JWTService: [Class: JWTService] Extracting all claims from token.");
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        boolean isValid = (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
        logger.info("JWTService: [Class: JWTService] Token validation for user: {}. Valid: {}", userName, isValid);
        return isValid;
    }

    public boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        logger.info("JWTService: [Class: JWTService] Token expiration check. Expired: {}", expired);
        return expired;
    }

    private Date extractExpiration(String token) {
        logger.debug("JWTService: [Class: JWTService] Extracting token expiration date.");
        return extractClaim(token, Claims::getExpiration);
    }

}

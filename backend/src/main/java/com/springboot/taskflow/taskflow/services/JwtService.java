package com.springboot.taskflow.taskflow.services;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.springboot.taskflow.taskflow.config.JwtProperties;
import com.springboot.taskflow.taskflow.entities.User;
import com.springboot.taskflow.taskflow.exceptions.InvalidTokenException;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final Algorithm algorithm;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.algorithm = Algorithm.HMAC256(properties.secret());
    }

    public String generateToken(User user) {
    Instant now = Instant.now();
    return JWT.create()
        .withIssuer(properties.issuer())
        .withSubject(user.getId().toString())
        .withClaim("role", user.getRole().getName())
        .withIssuedAt(Date.from(now))
        .withExpiresAt(Date.from(now.plusMillis(properties.expiration())))
        .sign(algorithm);
}

    public UUID extractUserId(String token) {
        return UUID.fromString(
            validate(token).getSubject()
        );
    }

    public String extractRole(String token) {
        return validate(token).getClaim("role").asString();
    }

    private DecodedJWT validate(String token) {
        try {
            return JWT.require(algorithm)
                .withIssuer(properties.issuer())
                .build()
                .verify(token);
        } catch (Exception ex) {
            throw new InvalidTokenException("Invalid or expired token.");
        }
    }
}
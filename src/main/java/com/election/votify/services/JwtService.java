package com.election.votify.services;


import com.election.votify.entities.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${election.jwt.secret}")
    private String SECRET_KEY="electionVerySecretKeyAndItHasToKeepToBe0001";
    @Value("${election.jwt.expiration}")
    private long EXPIRATION_TIME;

    public String generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("userId", user.getId());
        return createToken(claims, user.getUsername());
    }

    public boolean validateToken(String token, UserDetails userDetails) {

        String username = extractUsername(token);
        Date expirationDate = extractExpiration(token);
        try {
            return username.equals(userDetails.getUsername()) && !expirationDate.before(new Date());
        } catch (MalformedJwtException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    private Date extractExpiration(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }


    public long extractUserId(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? Long.valueOf(claims.get("userId").toString())
                : null;
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
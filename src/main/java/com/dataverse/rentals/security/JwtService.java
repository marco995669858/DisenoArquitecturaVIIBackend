package com.dataverse.rentals.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // Vida del token intermedio del segundo paso del login (MFA): corto a
    // proposito, solo alcanza para completar login -> ingresar el codigo.
    private static final long MFA_PENDING_EXPIRATION_MS = 5 * 60 * 1000;

    @Value("${app.security.jwt-secret}")
    private String secretKey;

    @Value("${app.security.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Aquí inyectamos los datos extra al Token
    public String generateToken(UserDetails userDetails, Integer idUsuario) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("idUsuario", idUsuario);
        extraClaims.put("roles", userDetails.getAuthorities());

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Token intermedio del segundo paso del login (MFA): identifica al
     * usuario que ya paso usuario/contraseña, sin ser todavia un token de
     * acceso valido para ningun endpoint protegido -- JwtAuthenticationFilter
     * no lo reconoce como tal porque nunca pasa por ahi, solo lo leen a mano
     * los endpoints /auth/mfa/verificar.
     */
    public String generarTokenMfaPendiente(String username, Integer idUsuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("idUsuario", idUsuario);
        claims.put("mfaPending", true);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + MFA_PENDING_EXPIRATION_MS))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Devuelve el username si el token es un pendiente-de-MFA valido y no expirado; null en cualquier otro caso. */
    public String validarTokenMfaPendiente(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Boolean pendiente = claims.get("mfaPending", Boolean.class);
            if (pendiente == null || !pendiente) {
                return null;
            }
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
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

package datn.com.cosmetics.config.auth;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTGenerator {

    private SecretKey key = Keys.hmacShaKeyFor("trancongtien123123123123123123123213".getBytes());
    private long EXPIRATION_TIME = 1000 * 60 * 60; // 1 giờ

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        System.out.println("user: " + username);
        String role = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .findFirst()
                .orElse("ROLE_USER"); // Lấy role từ authorities

        return Jwts.builder()
                .subject(username)
                .claim("role", role) // Thêm role vào token
                .claim("email", username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 24 giờ
                .signWith(key)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String email = String.valueOf(claims.get("email"));

        return email;
    }

    public String getRoleFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            System.out.println("Token validation failed: " + ex.getMessage());
            return false;
        }
    }
}
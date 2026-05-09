package dequelite.financemirror_backend.app.common.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    public String generateToken(String subject, String id, Map<String, Object> extraClaims, String key, long TTL) {
        Date today = new Date(System.currentTimeMillis());
        Date expiration = new Date(today.getTime()+TTL);

        SecretKey secretKey = getSignKey(key);

        return Jwts.builder()
                .claims(extraClaims)
                .id(id)
                .subject(subject)
                .issuedAt(today)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    private SecretKey getSignKey(String key) {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Map<String, Object> verifyToken(String token, String key) {
        SecretKey secretKey = getSignKey(key);

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

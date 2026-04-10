package com.ex.jwt;
  import io.jsonwebtoken.Claims;
  import io.jsonwebtoken.Jwts;
     import io.jsonwebtoken.security.Keys;
     import org.springframework.beans.factory.annotation.Value;
     import org.springframework.stereotype.Component;
import com.ex.emartUser.dto.EmartUserDTO;
import javax.crypto.SecretKey;
     import java.nio.charset.StandardCharsets;
    import java.util.Date;

@Component
public class TokenProvider {
    @Value("${jwt.secret}")
    private String secret;
@Value("${jwt.token-validity-in-seconds:1800}")
private long ACCESS_TOKEN_VALIDITY = 30 * 60; // 30분 (초 단위)

@Value("${jwt.refresh-token-validity-in-seconds}")
private long refreshTokenValidity;

    private SecretKey signingKey;
    private SecretKey getSigningKey() {
        if (this.signingKey == null) {
            this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
        return this.signingKey;
    }
    // Access Token 생성 (30분)
    public String createToken(String userId, int roleId) {
        Date now = new Date();
        Date Tokenvalidity = new Date(now.getTime() +  ACCESS_TOKEN_VALIDITY * 1000);
      return Jwts.builder()
            .subject(String.valueOf(userId))
             .claim("roleId", roleId)  // int 타입으로 저장
            .issuedAt(now)
            .expiration(Tokenvalidity)
            .signWith(getSigningKey())
            .compact();
    }

// Refresh Token 생성 (12시간)
public String createRefreshToken(String userId) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + refreshTokenValidity * 1000);
    
    return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("type", "refresh")
            .issuedAt(now)
            .expiration(validity)
            .signWith(getSigningKey())
            .compact();
}

  public boolean validateRefreshToken(String token) {
    try {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        // 1) type이 "refresh"인지 확인
        // 2) 만료 시간이 현재보다 뒤인지 확인
        boolean isRefresh = "refresh".equals(claims.get("type", String.class));
        boolean notExpired = claims.getExpiration().after(new Date());
        return isRefresh && notExpired;
    } catch (Exception e) {
        return false;
    }
}
  
    public String getUserId(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    public int getRoleId(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roleId", Integer.class);
    }

     // -----------------------------------------------------------------
    // 2️⃣ Parsing – returns the Claims object
    // -----------------------------------------------------------------
    public Claims getClaimsFromRefreshToken(String token) {
        return Jwts.parser()
               .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
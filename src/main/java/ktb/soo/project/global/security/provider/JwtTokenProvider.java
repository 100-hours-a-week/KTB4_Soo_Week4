package ktb.soo.project.global.security.provider;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ktb.soo.project.global.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final long accessTokenValidityMilliseconds;
    private final long refreshTokenValidityMilliseconds;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityTime,
                            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityTime) {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityMilliseconds = accessTokenValidityTime * 1000;
        this.refreshTokenValidityMilliseconds = refreshTokenValidityTime * 1000;
    }
    // 액세스 토큰 생성
    public String createAccessToken(String email, String role) {
        Date now = new Date();

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenValidityMilliseconds))
                .signWith(key)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String email) {
        Date now = new Date();

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenValidityMilliseconds))
                .signWith(key)
                .compact();
    }

    // 토큰 유효성 검증
    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        } catch (SecurityException | UnsupportedJwtException | MalformedJwtException e) {
            throw new UnauthorizedException("INVALID_TOKEN", "유효하지 않은 토큰입니다.");
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("EXPIRED_TOKEN", "만료된 토큰입니다.");
        }
    }

    //  토큰에서 이메일(UserId) 추출
    public String getEmail(String token){
            try {
                return Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject();
            } catch (ExpiredJwtException e) {
                return e.getClaims().getSubject();
            } catch (Exception e) {
                throw new UnauthorizedException("INVALID_TOKEN", "유효하지 않은 토큰입니다.");
            }
    }

}

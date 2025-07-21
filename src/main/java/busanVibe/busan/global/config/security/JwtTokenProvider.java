package busanVibe.busan.global.config.security;

import busanVibe.busan.domain.user.domain.User;
import busanVibe.busan.domain.user.dto.TokenResponseDTO;
import busanVibe.busan.domain.user.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserDetailsService userDetailsService;

  private Key key;

  @Value("${jwt.secret}")
  private String secretKey;

  private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;
  private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  public TokenResponseDTO createToken(User user) {
    Claims claims = Jwts.claims().setSubject(user.getEmail());
    Date now = new Date();

    String accessToken = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    String refreshToken = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
        .claim("random", UUID.randomUUID().toString())
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    long expiration = getExpiration(refreshToken);
    refreshTokenRepository.saveToken(user.getId(), accessToken, expiration);

    return TokenResponseDTO.of(accessToken, refreshToken);
  }

  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims =
          Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (ExpiredJwtException e) {
      System.out.println("JWT Token expired: " + e.getMessage());
    } catch (UnsupportedJwtException e) {
      System.out.println("Unsupported JWT Token: " + e.getMessage());
    } catch (MalformedJwtException e) {
      System.out.println("Malformed JWT Token: " + e.getMessage());
    } catch (SignatureException e) {
      System.out.println("Invalid JWT signature: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.out.println("JWT claims string is empty: " + e.getMessage());
    }
    return false;
  }

  public boolean isTokenExpired(String token) {
    try {
      Date expiration =
          Jwts.parserBuilder()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(token)
              .getBody()
              .getExpiration();
      return expiration.before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    }
  }

  public Authentication getAuthentication(String token) {
    String email = getEmailFromToken(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    return new UsernamePasswordAuthenticationToken(
        userDetails, "", userDetails.getAuthorities()
    );
  }

  private String getEmailFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody().getSubject();
  }

  private long getExpiration(String token) {
    try {
      Claims claims =
          Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
      return claims.getExpiration().getTime() - System.currentTimeMillis();
    } catch (ExpiredJwtException e) {
      return 0;
    }catch (Exception e) {
      throw new IllegalArgumentException("Invalid JWT Token");
    }
  }

}

package busanVibe.busan.domain.user.repository;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenRepository {

  private final StringRedisTemplate redisTemplate;

  @Autowired
  public RefreshTokenRepository(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void saveToken(Long userId, String refreshToken, long expirationTime) {
    String key = "refreshToken:" + refreshToken;

    redisTemplate
        .opsForValue()
        .set(key, userId.toString(), expirationTime / 1000, TimeUnit.SECONDS);

  }

  // RefreshToken으로 userId 가져오기
  public Long getUserIdByToken(String refreshToken) {
    String key = "refreshToken:" + refreshToken;
    String userIdStr = redisTemplate.opsForValue().get(key);
    return userIdStr != null ? Long.parseLong(userIdStr) : null;
  }

  // RefreshToken 삭제
  public void deleteToken(String refreshToken) {
    String key = "refreshToken:" + refreshToken;
    redisTemplate.delete(key);
  }

  public boolean existsByToken(String refreshToken) {
    String key = "refreshToken:" + refreshToken;
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

}

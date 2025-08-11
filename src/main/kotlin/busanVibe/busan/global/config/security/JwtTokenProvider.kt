package busanVibe.busan.global.config.security

import busanVibe.busan.domain.user.data.User
import busanVibe.busan.domain.user.data.dto.TokenResponseDto
import busanVibe.busan.domain.user.repository.RefreshTokenRepository
import busanVibe.busan.domain.user.repository.UserRepository
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.SignatureException
import java.util.Date
import java.util.UUID

@Component
class JwtTokenProvider(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userDetailsService: UserDetailsService,
    @Value("\${jwt.secret}") private val secretKey: String,
    private val userRepository: UserRepository
) {

    companion object{
        private const val ACCESS_TOKEN_EXPIRE_TIME: Long = 1000 * 60 * 60 * 24
        private const val REFRESH_TOKEN_EXPIRE_TIME: Long = 1000 * 60 * 60 * 24
    }

    private lateinit var key: Key
    private val log = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    @PostConstruct
    fun init() {
        this.key = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))
    }

    fun createToken(user: User) : TokenResponseDto {
        val claims: Claims = Jwts.claims().setSubject(user.id.toString())
        val now: Date = Date()

        val accessToken: String = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + REFRESH_TOKEN_EXPIRE_TIME))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        val refreshToken: String = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + REFRESH_TOKEN_EXPIRE_TIME))
            .claim("random", UUID.randomUUID().toString())
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        val expiration: Long = getExpiration(refreshToken)

        val userId = user.id ?: throw NullPointerException("JwtTokenProvider userId is null")
        refreshTokenRepository.saveToken(user.id, accessToken, expiration)

        return TokenResponseDto.of(accessToken, refreshToken)
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        } catch (e: ExpiredJwtException) {
            log.warn("JWT Token expired: {}", e.message)
            false
        } catch (e: UnsupportedJwtException) {
            log.warn("Unsupported JWT Token: {}", e.message)
            false
        } catch (e: MalformedJwtException) {
            log.warn("Malformed JWT Token: {}", e.message)
            false
        } catch (e: SignatureException) {
            log.warn("Invalid JWT signature: {}", e.message)
            false
        } catch (e: IllegalArgumentException) {
            log.warn("JWT claims string is empty: {}", e.message)
            false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val id: Long = getIdFromToken(token).toLong()
        val user:User = userRepository.findById(id)
            .orElseThrow { ExceptionHandler(ErrorStatus.USER_NOT_FOUND) }

        return UsernamePasswordAuthenticationToken(
            user, "", user.authorities
        )

    }

    fun getIdFromToken(token:String):String{
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body.subject
    }

    private fun getExpiration(token: String): Long {
        try {
            val claims: Claims = Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(token)
                .body
            return claims.expiration.time - System.currentTimeMillis()
        }catch (e: ExpiredJwtException) {
            return 0;
        }catch (e: Exception){
            throw IllegalArgumentException("Invalid JWT token")
        }
    }


}
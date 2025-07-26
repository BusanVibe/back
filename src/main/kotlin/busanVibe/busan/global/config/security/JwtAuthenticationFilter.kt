package busanVibe.busan.global.config.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.util.Collections

@Slf4j
class JwtAuthenticationFilter: OncePerRequestFilter{

    private val jwtTokenProvider: JwtTokenProvider
    private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    constructor(jwtTokenProvider: JwtTokenProvider){
        this.jwtTokenProvider = jwtTokenProvider
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val token = resolveToken(request)
        try {

            if (token != null) {
                log.info("Token found: $token")
                if(jwtTokenProvider.validateToken(token)){
                    val authentication: Authentication = jwtTokenProvider.getAuthentication(token)
                    SecurityContextHolder.getContext().authentication = authentication
                    log.info("Authentication set in SecurityContextHolder: ${authentication.name}")
                }else{
                    log.warn("Invalid or Expired token")
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.writer.write("Invalid or Expired token")
                    return
                }
            }else{
                val anonymouseAuth: Authentication =
                        AnonymousAuthenticationToken(
                            "anonymousUser",
                            "anonymousUser",
                            Collections.singletonList(SimpleGrantedAuthority("anonymousUser"))
                        )
                SecurityContextHolder.getContext().authentication = anonymouseAuth
            }

        }catch (e: Exception){
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            response.writer.write("Internal server error occurred")
        }

        filterChain.doFilter(request, response)

    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null;
    }



}
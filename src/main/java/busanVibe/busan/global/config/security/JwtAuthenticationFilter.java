package busanVibe.busan.global.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
  ) throws ServletException, IOException {
    String token = resolveToken(request);
    try {

      if (token != null) {
        log.info("Token Found: " + token);
        if (jwtTokenProvider.validateToken(token)) {
          Authentication authentication = jwtTokenProvider.getAuthentication(token);
          SecurityContextHolder.getContext().setAuthentication(authentication);
          log.info("Authentication set in SecurityContextHolder: " + authentication.getName());
        } else {
          log.warn("Invalid or expired token");
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.getWriter().write("Invalid or expired token");
          return;
        }
      } else {
        Authentication anonymousAuth =
            new AnonymousAuthenticationToken(
                "anonymousUser",
                "anonymousUser",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
            );
        SecurityContextHolder.getContext().setAuthentication(anonymousAuth);
      }
    }catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().write("Internal server error occurred");
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

}

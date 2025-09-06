// SimpleAuthFilter.java
package com.datatrans.ecommerce.userms.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Component
public class SimpleAuthFilter implements Filter {
  private final AuthTokenRepository tokenRepo;
  private final ObjectMapper objectMapper;

  public SimpleAuthFilter(AuthTokenRepository tokenRepo, ObjectMapper objectMapper) {
    this.tokenRepo = tokenRepo;
    this.objectMapper = objectMapper;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    String token = resolveToken(req);
    if (token != null) {
      tokenRepo.findByToken(token)
        .filter(t -> !t.isRevoked() && t.getExpiresAt().isAfter(Instant.now()))
        .ifPresent(t -> {
          try {
            Map<String,Object> claims = objectMapper.readValue(
                t.getClaimsJson(), new TypeReference<Map<String,Object>>() {});
            req.setAttribute("auth.userId", claims.get("userId"));
            req.setAttribute("auth.username", claims.get("username"));
            req.setAttribute("auth.role", claims.get("role"));
            req.setAttribute("auth.permissions", claims.get("permissions"));
          } catch (Exception ignored) {}
        });
    }
    chain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest req) {
    String x = req.getHeader("X-Auth-Token");
    if (x != null && !x.isBlank()) return x;
    String h = req.getHeader("Authorization");
    if (h != null && h.startsWith("Bearer ")) return h.substring(7);
    return null;
  }
}

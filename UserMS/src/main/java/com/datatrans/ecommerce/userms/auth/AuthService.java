// AuthService.java
package com.datatrans.ecommerce.userms.auth;

import com.datatrans.ecommerce.userms.permissions.PermissionRepository;
import com.datatrans.ecommerce.userms.user.User;
import com.datatrans.ecommerce.userms.user.UserRepository;
import com.operationHelper.errorHandler.InvalidParameters;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final AuthTokenRepository tokenRepository;
  private final PermissionRepository permissionRepository;
  private final PasswordEncoder passwordEncoder;
  private final ObjectMapper objectMapper;

  public AuthService(UserRepository userRepository,
                     AuthTokenRepository tokenRepository,
                     PermissionRepository permissionRepository,
                     PasswordEncoder passwordEncoder,
                     ObjectMapper objectMapper) {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.permissionRepository = permissionRepository;
    this.passwordEncoder = passwordEncoder;
    this.objectMapper = objectMapper;
  }

  public Map<String,Object> login(String username, String rawPassword) {
	  User user = userRepository.findByUsernameAndStatusId(username, 1)
		      .orElseThrow(() -> new InvalidParameters(
		        java.util.List.of("Invalid username or password.")));

    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
      throw new InvalidParameters(List.of("Invalid username or password."));
    }

    // 1) fetch permissions by userRole (active only)
    List<String> perms = permissionRepository
        .findAllByUserRoleAndStatusId(user.getUserRole(), 1)
        .stream().map(p -> p.getCode()).distinct().collect(Collectors.toList());

    // 2) build claims and serialize
    Map<String,Object> claims = Map.of(
        "userId", user.getId(),
        "username", user.getUsername(),
        "role", user.getUserRole(),
        "permissions", perms
    );
    String claimsJson;
    try {
      claimsJson = objectMapper.writeValueAsString(claims);
    } catch (Exception e) {
      throw new RuntimeException("Failed to serialize token claims", e);
    }

    // 3) issue token
    String token = UUID.randomUUID().toString().replace("-", "");
    AuthToken at = new AuthToken();
    at.setToken(token);
    at.setUser(user);
    at.setExpiresAt(Instant.now().plus(12, ChronoUnit.HOURS));
    at.setClaimsJson(claimsJson);
    tokenRepository.save(at);

    // 4) return token + claims (handy for client)
    return Map.of(
      "token", token,
      "tokenType", "Bearer",
      "expiresAt", at.getExpiresAt().toString(),
      "claims", claims
    );
  }
}

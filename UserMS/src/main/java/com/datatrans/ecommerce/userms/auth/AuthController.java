// AuthController.java
package com.datatrans.ecommerce.userms.auth;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public Map<String,Object> login(@RequestBody Map<String,String> body) {
    return authService.login(body.get("username"), body.get("password"));
  }

 

  private String resolveToken(String authHeader, String xToken) {
    if (xToken != null && !xToken.isBlank()) return xToken;
    if (authHeader != null && authHeader.startsWith("Bearer "))
      return authHeader.substring(7);
    return null;
  }
}

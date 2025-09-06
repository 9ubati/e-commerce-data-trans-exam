// AuthTokenRepository.java
package com.datatrans.ecommerce.userms.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
  Optional<AuthToken> findByToken(String token);
}

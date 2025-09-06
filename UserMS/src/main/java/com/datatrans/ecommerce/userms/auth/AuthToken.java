// AuthToken.java
package com.datatrans.ecommerce.userms.auth;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * 
 */
@Entity
@Table(name = "auth_tokens", indexes = @Index(columnList = "token", unique = true))
public class AuthToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 64)
	private String token;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private com.datatrans.ecommerce.userms.user.User user;

	@Column(nullable = false)
	private Instant expiresAt;

	@Column(nullable = false)
	private boolean revoked = false;

	/** Cached claims (role, perms, etc.) at login time */
	@Lob
	@Column(name = "claims_json", columnDefinition = "TEXT")
	private String claimsJson;

	// getters/setters...
	public String getClaimsJson() {
		return claimsJson;
	}

	public void setClaimsJson(String claimsJson) {
		this.claimsJson = claimsJson;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public com.datatrans.ecommerce.userms.user.User getUser() {
		return user;
	}

	public void setUser(com.datatrans.ecommerce.userms.user.User user) {
		this.user = user;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}

}

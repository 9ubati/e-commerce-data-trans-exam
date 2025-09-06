package com.datatrans.ecommerce.userms.user;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * 
 */
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String userRole;
	private String password;
	private String fullName;
	private String mobile;
	private String email;
	@Column(nullable = false)
	private Integer statusId = 1;

	// Constructors
	public User() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public List<Map<String, Object>> fields() {
		return Arrays.asList(new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("fieldName", "username");
				put("fieldType", "string");
				put("mandatory", true);
				put("unique", true);

			}
		},new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("fieldName", "userRole");
				put("fieldType", "string");
				put("mandatory", true);
				put("unique", false);

			}
		},
				new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("fieldName", "password");
				put("fieldType", "string");
				put("mandatory", true);
				put("unique", false);

			}
		},
				new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("fieldName", "fullName");
				put("fieldType", "string");
				put("mandatory", true);
				put("unique", false);

			}
		}, new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("fieldName", "email");
				put("fieldType", "string");
				put("mandatory", true);
				put("unique", false);

			}
		}, new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("fieldName", "mobile");
				put("fieldType", "integer");
				put("mandatory", true);
				put("unique", false);

			}
		}, new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("fieldName", "statusId");
				put("fieldType", "integer");
				put("mandatory", false);
				put("unique", false);

			}
		});

	}

}

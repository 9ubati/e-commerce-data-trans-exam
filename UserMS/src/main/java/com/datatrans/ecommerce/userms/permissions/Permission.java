package com.datatrans.ecommerce.userms.permissions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "permissions")
public class Permission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String code;

	@Column(nullable = false, length = 100)
	private String userRole;

	@Column(nullable = false)
	private Integer statusId = 1;

	public Permission() {
	}

	// getters/setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	/** Validation metadata */
	public List<Map<String, Object>> fields() {
		return Arrays.asList(new HashMap<String, Object>() {
			{
				put("fieldName", "code");
				put("fieldType", "string");
				put("mandatory", true);
				put("unique", true); // unique among active (statusId=1)
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "userRole");
				put("fieldType", "string");
				put("mandatory", true);
				put("unique", false);
			}
		});
	}
}

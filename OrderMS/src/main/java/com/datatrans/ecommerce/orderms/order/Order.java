package com.datatrans.ecommerce.orderms.order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrans.ecommerce.orderms.orderitems.OrderItem;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 64, unique = true)
	private String orderNumber; // unique business id

	@Column(nullable = false)
	private Long userId; // who placed the order

	@Column(nullable = false, length = 10)
	private String currency; // e.g. USD, SAR

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal totalAmount = BigDecimal.ZERO;

	@Column(nullable = false, length = 32)
	private String status = "NEW"; // NEW, PAID, CANCELLED (example)

	@Column(columnDefinition = "TEXT")
	private String notes;

	@Column(nullable = false)
	private Integer statusId = 1; // soft-delete: 1 active, 0 deleted

	@Column(nullable = false)
	private OffsetDateTime createdAt = OffsetDateTime.now();

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<OrderItem> items = new ArrayList<>();

	// getters/setters
	public Long getId() {
		return id;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	/** Validation metadata for orderData */
	public List<Map<String, Object>> fields() {
		return Arrays.asList(new HashMap<String, Object>() {
			{
				put("fieldName", "orderNumber");
				put("fieldType", "string");
				put("mandatory", true);
				put("unique", true);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "userId");
				put("fieldType", "long");
				put("mandatory", true);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "currency");
				put("fieldType", "string");
				put("mandatory", true);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "status");
				put("fieldType", "string");
				put("mandatory", false);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "notes");
				put("fieldType", "string");
				put("mandatory", false);
			}
		});
	}
}

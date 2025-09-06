package com.datatrans.ecommerce.orderms.orderitems;

import java.math.BigDecimal;

import com.datatrans.ecommerce.orderms.order.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@JsonIgnore
	private Order order;

	@Column(nullable = false)
	private Long productId;

	@Column(length = 100)
	private String sku;

	@Column(length = 200)
	private String name;

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal unitPrice;

	@Column(nullable = false)
	private Integer qty;

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal lineTotal;

	@Column(nullable = false, length = 10)
	private String currency;

	@Column(columnDefinition = "TEXT")
	private String attributesJson;

	@Column(nullable = false)
	private Integer statusId = 1;

	// getters/setters
	public Long getId() {
		return id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public BigDecimal getLineTotal() {
		return lineTotal;
	}

	public void setLineTotal(BigDecimal lineTotal) {
		this.lineTotal = lineTotal;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAttributesJson() {
		return attributesJson;
	}

	public void setAttributesJson(String attributesJson) {
		this.attributesJson = attributesJson;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	/** Validation metadata for one orderItem */
	public java.util.List<java.util.Map<String, Object>> fields() {
		return java.util.Arrays.asList(new java.util.HashMap<String, Object>() {
			{
				put("fieldName", "productId");
				put("fieldType", "long");
				put("mandatory", true);
			}
		}, new java.util.HashMap<String, Object>() {
			{
				put("fieldName", "sku");
				put("fieldType", "string");
				put("mandatory", false);
			}
		}, new java.util.HashMap<String, Object>() {
			{
				put("fieldName", "name");
				put("fieldType", "string");
				put("mandatory", false);
			}
		}, new java.util.HashMap<String, Object>() {
			{
				put("fieldName", "unitPrice");
				put("fieldType", "decimal");
				put("mandatory", true);
			}
		}, new java.util.HashMap<String, Object>() {
			{
				put("fieldName", "qty");
				put("fieldType", "integer");
				put("mandatory", true);
			}
		}, new java.util.HashMap<String, Object>() {
			{
				put("fieldName", "currency");
				put("fieldType", "string");
				put("mandatory", true);
			}
		}, new java.util.HashMap<String, Object>() {
			{
				put("fieldName", "attributesJson");
				put("fieldType", "string");
				put("mandatory", false);
			}
		});
	}
}

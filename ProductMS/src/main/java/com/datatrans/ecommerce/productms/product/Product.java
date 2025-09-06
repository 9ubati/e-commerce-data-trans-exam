package com.datatrans.ecommerce.productms.product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Unique product code/SKU */
	@Column(nullable = false, length = 100)
	private String sku;

	@Column(nullable = false, length = 200)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(length = 100)
	private String category;

	@Column(length = 100)
	private String brand;

	/** Monetary fields */
	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal price;

	@Column(nullable = false, length = 10)
	private String currency; // e.g., "USD", "EUR", "SAR"

	/** Inventory */
	@Column(nullable = false)
	private Integer stockQty = 0;

	/** Dimensions / weight (optional) */
	private Double weight; // kg
	private Double length; // cm
	private Double width; // cm
	private Double height; // cm

	/** JSON blobs for extensibility (optional) */
	@Column(columnDefinition = "TEXT")
	private String imagesJson; // e.g. ["url1","url2"]

	@Column(columnDefinition = "TEXT")
	private String attributesJson; // e.g. {"color":"red","size":"XL"}

	/** Soft delete: 1 = active, 0 = deleted */
	@Column(nullable = false)
	private Integer statusId = 1;

	// ===== Getters/Setters =====
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getStockQty() {
		return stockQty;
	}

	public void setStockQty(Integer stockQty) {
		this.stockQty = stockQty;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public String getImagesJson() {
		return imagesJson;
	}

	public void setImagesJson(String imagesJson) {
		this.imagesJson = imagesJson;
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

	/** Validation metadata (used by HelperFunctions.validateFields) */
	public List<Map<String, Object>> fields() {
		return Arrays.asList(new HashMap<String, Object>() {
			{ // Unique key
				put("fieldName", "sku");
				put("fieldType", "string");
				put("mandatory", true);
				put("unique", true);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "name");
				put("fieldType", "string");
				put("mandatory", true);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "description");
				put("fieldType", "string");
				put("mandatory", false);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "category");
				put("fieldType", "string");
				put("mandatory", false);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "brand");
				put("fieldType", "string");
				put("mandatory", false);
			}
		}, new HashMap<String, Object>() {
			{ // price as decimal/number
				put("fieldName", "price");
				put("fieldType", "decimal");
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
				put("fieldName", "stockQty");
				put("fieldType", "integer");
				put("mandatory", false);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "weight");
				put("fieldType", "number");
				put("mandatory", false);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "length");
				put("fieldType", "number");
				put("mandatory", false);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "width");
				put("fieldType", "number");
				put("mandatory", false);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "height");
				put("fieldType", "number");
				put("mandatory", false);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "imagesJson");
				put("fieldType", "string"); // pass JSON string; or switch to "object" and map elsewhere
				put("mandatory", false);
			}
		}, new HashMap<String, Object>() {
			{
				put("fieldName", "attributesJson");
				put("fieldType", "string"); // pass JSON string
				put("mandatory", false);
			}
		});
	}
}

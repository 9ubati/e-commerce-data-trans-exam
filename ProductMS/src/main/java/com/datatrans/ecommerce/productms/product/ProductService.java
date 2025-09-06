package com.datatrans.ecommerce.productms.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.operationHelper.errorHandler.InvalidParameters;
import com.operationHelper.utils.HelperFunctions;
import com.operationHelper.utils.PermissionUtil;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final HelperFunctions helperFunctions;
	private final ObjectMapper objectMapper;

	public ProductService(ProductRepository productRepository, HelperFunctions helperFunctions,
			ObjectMapper objectMapper) {
		this.productRepository = productRepository;
		this.helperFunctions = helperFunctions;
		this.objectMapper = objectMapper;
	}

	/** Create */
	@Transactional
	public Product post(Map<String, Object> requestMap, Map<String, String> headers) {
		PermissionUtil.requirePermission("add_product", headers);
		var defs = new Product().fields();
		helperFunctions.validateFields(requestMap, defs);

		Product p = objectMapper.convertValue(requestMap, Product.class);
		p.setStatusId(1);

		// basic business rules
		if (p.getPrice() == null || p.getPrice().compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidParameters("price must be non-negative");
		}
		if (p.getStockQty() != null && p.getStockQty() < 0) {
			throw new InvalidParameters("stockQty must be >= 0");
		}

		// uniqueness on SKU (active)
		if (productRepository.existsBySkuAndStatusId(p.getSku(), 1)) {
			throw new InvalidParameters("Product with sku [" + p.getSku() + "] already exists.");
		}

		return productRepository.save(p);
	}

	/** Edit / Delete */
	@Transactional
	public Product patch(Map<String, Object> requestMap, Map<String, String> headers) {
		PermissionUtil.requirePermission("edit_product", headers);

		String op = String.valueOf(requestMap.get("operation")).trim();
		if (op.isBlank())
			throw new InvalidParameters("operation is mandatory");

		Object idRaw = requestMap.get("id");
		if (idRaw == null || idRaw.toString().isBlank())
			throw new InvalidParameters("id is mandatory");
		Long id = Long.parseLong(idRaw.toString());

		Product p = productRepository.findByIdAndStatusId(id, 1)
				.orElseThrow(() -> new InvalidParameters("Product with id [" + id + "] not found"));

		switch (op.toLowerCase()) {
		case "delete": {
			p.setStatusId(0);
			return productRepository.save(p);
		}
		case "edit": {
			var allDefs = new Product().fields();
			var updatable = allDefs.stream().map(m -> String.valueOf(m.get("fieldName")))
					.filter(n -> n != null && !n.isBlank()).filter(n -> !n.equalsIgnoreCase("id"))
					.collect(java.util.stream.Collectors.toSet());

			Map<String, Object> updates = new java.util.HashMap<>();
			for (var e : requestMap.entrySet()) {
				String k = e.getKey();
				if (k == null)
					continue;
				if (k.equalsIgnoreCase("id") || k.equalsIgnoreCase("operation"))
					continue;
				if (!updatable.contains(k))
					continue;
				updates.put(k, e.getValue());
			}

			if (updates.isEmpty())
				throw new InvalidParameters("No updatable fields in request.");

			// Apply updates with coercion
			BeanWrapper bw = new BeanWrapperImpl(p);
			for (var e : updates.entrySet()) {
				String name = e.getKey();
				Object raw = e.getValue();
				Class<?> targetType = bw.getPropertyType(name);
				Object coerced = (raw == null ? null : objectMapper.convertValue(raw, targetType));
				bw.setPropertyValue(name, coerced);
			}

			// business rules
			if (updates.containsKey("price")) {
				if (p.getPrice() == null || p.getPrice().compareTo(BigDecimal.ZERO) < 0) {
					throw new InvalidParameters("price must be non-negative");
				}
			}
			if (updates.containsKey("stockQty")) {
				if (p.getStockQty() != null && p.getStockQty() < 0) {
					throw new InvalidParameters("stockQty must be >= 0");
				}
			}

			// uniqueness if sku changed/present
			if (updates.containsKey("sku")
					&& productRepository.existsBySkuAndIdNotAndStatusId(p.getSku(), p.getId(), 1)) {
				throw new InvalidParameters("Product already exists with the same sku.");
			}

			return productRepository.save(p);
		}
		default:
			throw new InvalidParameters("Unsupported operation: " + op + " (allowed: edit, delete)");
		}
	}

	/** Reads (active only) */
	public List<Product> getAll(Map<String, String> headers) {
		PermissionUtil.requirePermission("get_product", headers);
		return productRepository.findAllByStatusId(1);
	}

	public Optional<Product> getById(Long id) {
		return productRepository.findByIdAndStatusId(id, 1);
	}
}

package com.datatrans.ecommerce.productms.product;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

	private final ProductService service;

	public ProductController(ProductService service) {
		this.service = service;
	}

	@PostMapping
	public Product post(@RequestBody Map<String, Object> body, @RequestHeader Map<String, String> headers) {
		return service.post(body, headers);
	}

	@PatchMapping
	public Product patch(@RequestBody Map<String, Object> body, @RequestHeader Map<String, String> headers) {
		return service.patch(body, headers);
	}

	@GetMapping
	public List<Product> getAll(@RequestHeader Map<String, String> headers) {
		return service.getAll(headers);
	}

	@GetMapping("/{id}")
	public Product getOne(@PathVariable Long id) {
		return service.getById(id).orElseThrow(
				() -> new com.operationHelper.errorHandler.InvalidParameters("Product [" + id + "] not found"));
	}
}

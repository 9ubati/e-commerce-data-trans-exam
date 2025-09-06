package com.datatrans.ecommerce.orderms.order;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

	private final OrderService service;

	public OrderController(OrderService service) {
		this.service = service;
	}

	@PostMapping
	public Order post(@RequestBody Map<String, Object> body, @RequestHeader Map<String, String> headers) {
		return service.post(body, headers);
	}

	@PatchMapping
	public Order patch(@RequestBody Map<String, Object> body, @RequestHeader Map<String, String> headers) {
		return service.patch(body, headers);
	}

	@GetMapping
	public List<Order> getAll(@RequestHeader Map<String, String> headers) {
		return service.getAll(headers);
	}

	@GetMapping("/{id}")
	public Order getOne(@PathVariable Long id) {
		return service.getById(id).orElseThrow(
				() -> new com.operationHelper.errorHandler.InvalidParameters("Order [" + id + "] not found"));
	}
}

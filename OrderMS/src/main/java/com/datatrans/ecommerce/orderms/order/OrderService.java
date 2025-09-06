package com.datatrans.ecommerce.orderms.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.datatrans.ecommerce.orderms.orderitems.OrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.operationHelper.errorHandler.InvalidParameters;
import com.operationHelper.utils.HelperFunctions;
import com.operationHelper.utils.PermissionUtil;

@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final HelperFunctions helperFunctions;
	private final ObjectMapper objectMapper;

	public OrderService(OrderRepository orderRepository, HelperFunctions helperFunctions, ObjectMapper objectMapper) {
		this.orderRepository = orderRepository;
		this.helperFunctions = helperFunctions;
		this.objectMapper = objectMapper;
	}

	/** POST: create order with items */
	@Transactional
	@SuppressWarnings("unchecked")
	public Order post(Map<String, Object> request, Map<String, String> headers) {
		PermissionUtil.requirePermission("add_order", headers);

		Object orderDataObj = request.get("orderData");
		Object orderItemsObj = request.get("orderItems");

		if (!(orderDataObj instanceof Map))
			throw new InvalidParameters("orderData must be an object.");
		if (!(orderItemsObj instanceof List<?>))
			throw new InvalidParameters("orderItems must be an array.");
		List<Map<String, Object>> orderItems = (List<Map<String, Object>>) orderItemsObj;

		// validate orderData
		Map<String, Object> orderData = (Map<String, Object>) orderDataObj;
		var orderDefs = new Order().fields();
		helperFunctions.validateFields(orderData, orderDefs);

		// minimal sanity on array
		if (orderItems.isEmpty())
			throw new InvalidParameters("orderItems cannot be empty.");

		// validate each item
		var itemDefs = new OrderItem().fields();
		for (Map<String, Object> item : orderItems)
			helperFunctions.validateFields(item, itemDefs);

		// map order
		Order order = objectMapper.convertValue(orderData, Order.class);
		order.setStatusId(1);

		// item currency & totals
		String currency = order.getCurrency();
		BigDecimal total = BigDecimal.ZERO;
		List<OrderItem> items = new ArrayList<>();

		for (Map<String, Object> it : orderItems) {
			OrderItem oi = objectMapper.convertValue(it, OrderItem.class);

			if (!Objects.equals(currency, oi.getCurrency()))
				throw new InvalidParameters(
						"Item currency [" + oi.getCurrency() + "] does not match order currency [" + currency + "].");

			if (oi.getUnitPrice().compareTo(BigDecimal.ZERO) < 0)
				throw new InvalidParameters("unitPrice must be >= 0");
			if (oi.getQty() <= 0)
				throw new InvalidParameters("qty must be > 0");

			oi.setLineTotal(oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQty())));
			oi.setOrder(order);
			oi.setStatusId(1);

			total = total.add(oi.getLineTotal());
			items.add(oi);
		}

		order.setItems(items);
		order.setTotalAmount(total);

		// uniqueness of orderNumber among active
		if (orderRepository.existsByOrderNumberAndStatusId(order.getOrderNumber(), 1)) {
			throw new InvalidParameters("Order with orderNumber [" + order.getOrderNumber() + "] already exists.");
		}

		return orderRepository.save(order);
	}

	/** PATCH: edit or delete */
	@Transactional
	@SuppressWarnings("unchecked")
	public Order patch(Map<String, Object> request, Map<String, String> headers) {
		PermissionUtil.requirePermission("edit_order", headers);

		String op = String.valueOf(request.get("operation")).trim();
		if (op.isBlank())
			throw new InvalidParameters("operation is mandatory");

		Object idRaw = request.get("id");
		if (idRaw == null || idRaw.toString().isBlank())
			throw new InvalidParameters("id is mandatory");
		Long id = Long.parseLong(idRaw.toString());

		Order order = orderRepository.findByIdAndStatusId(id, 1)
				.orElseThrow(() -> new InvalidParameters("Order with id [" + id + "] not found"));

		switch (op.toLowerCase()) {
		case "delete": {
			order.setStatusId(0);
			// optionally mark items as 0 too (kept here in the aggregate)
			if (order.getItems() != null)
				order.getItems().forEach(i -> i.setStatusId(0));
			return orderRepository.save(order);
		}
		case "edit": {
			// apply orderData fields if provided
			Map<String, Object> orderData = request.get("orderData") instanceof Map
					? (Map<String, Object>) request.get("orderData")
					: Map.of();
			if (!orderData.isEmpty()) {
				// update fields dynamically
				BeanWrapper bw = new BeanWrapperImpl(order);
				for (var e : orderData.entrySet()) {
					String name = e.getKey();
					if (List.of("id", "items", "statusId", "totalAmount", "createdAt").contains(name))
						continue;
					Class<?> targetType = bw.getPropertyType(name);
					if (targetType == null)
						continue;
					Object coerced = (e.getValue() == null ? null
							: objectMapper.convertValue(e.getValue(), targetType));
					bw.setPropertyValue(name, coerced);
				}

				// uniqueness if orderNumber changed
				if (orderData.containsKey("orderNumber") && orderRepository
						.existsByOrderNumberAndIdNotAndStatusId(order.getOrderNumber(), order.getId(), 1)) {
					throw new InvalidParameters("Order already exists with the same orderNumber.");
				}
			}

			// if orderItems provided, REPLACE current items with the new list
			if (request.get("orderItems") instanceof List<?>) {
				var list = (List<Map<String, Object>>) request.get("orderItems");
				if (list.isEmpty())
					throw new InvalidParameters("orderItems cannot be empty.");

				var itemDefs = new OrderItem().fields();
				String currency = order.getCurrency(); // after possible edit above
				BigDecimal total = BigDecimal.ZERO;

				// clear and rebuild (orphanRemoval=true will handle deletes)
				order.getItems().clear();

				for (Map<String, Object> it : list) {
					helperFunctions.validateFields(it, itemDefs);
					OrderItem oi = objectMapper.convertValue(it, OrderItem.class);

					if (!Objects.equals(currency, oi.getCurrency()))
						throw new InvalidParameters("Item currency [" + oi.getCurrency()
								+ "] does not match order currency [" + currency + "].");

					if (oi.getUnitPrice().compareTo(BigDecimal.ZERO) < 0)
						throw new InvalidParameters("unitPrice must be >= 0");
					if (oi.getQty() <= 0)
						throw new InvalidParameters("qty must be > 0");

					oi.setLineTotal(oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQty())));
					oi.setOrder(order);
					oi.setStatusId(1);

					total = total.add(oi.getLineTotal());
					order.getItems().add(oi);
				}
				order.setTotalAmount(total);
			}

			return orderRepository.save(order);
		}
		default:
			throw new InvalidParameters("Unsupported operation: " + op + " (allowed: edit, delete)");
		}
	}

	/** READS */
	public List<Order> getAll(Map<String, String> headers) {
		PermissionUtil.requirePermission("get_order", headers);
		return orderRepository.findAllByStatusId(1);
	}

	public Optional<Order> getById(Long id) {
		return orderRepository.findByIdAndStatusId(id, 1);
	}
}

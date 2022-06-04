package com.example.bookclub.controller.api;

import com.example.bookclub.application.order.OrderService;
import com.example.bookclub.domain.order.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/orders")
public class OrderApiController {
	private final OrderService orderService;

	public OrderApiController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Object create(Order order) {
		orderService.create(order);
		return null;
	}
}

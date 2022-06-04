package com.example.bookclub.controller.api;

import com.example.bookclub.application.order.OrderService;
import com.example.bookclub.common.response.CommonResponse;
import com.example.bookclub.domain.order.Order;
import com.example.bookclub.dto.OrderDto;
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
	public CommonResponse<OrderDto.OrderCreateResultDto> create(Order order) {
		OrderDto.OrderCreateResultDto response = orderService.create(order);
		return CommonResponse.success(response);
	}
}

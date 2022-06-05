package com.example.bookclub.controller.api;

import com.example.bookclub.application.order.OrderService;
import com.example.bookclub.common.response.CommonResponse;
import com.example.bookclub.dto.OrderDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController("/api/orders")
public class OrderApiController {
	private final OrderService orderService;

	public OrderApiController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse<OrderDto.OrderCreateResponse> create(
			@RequestBody @Valid OrderDto.OrderCreateRequest orderCreateRequest
	) {
		OrderDto.OrderCreateResponse response = orderService.create(orderCreateRequest);
		return CommonResponse.success(response);
	}

	@GetMapping("/{id}")
	public CommonResponse<OrderDto.OrderDetailResponse> detail(@PathVariable Long id) {
		OrderDto.OrderDetailResponse response = orderService.getOrder(id);
		return CommonResponse.success(response);
	}
}

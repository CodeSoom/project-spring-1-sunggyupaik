package com.example.bookclub.controller.api;

import com.example.bookclub.application.order.OrderService;
import com.example.bookclub.application.order.payment.PaymentService;
import com.example.bookclub.common.response.CommonResponse;
import com.example.bookclub.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController("/api/orders")
public class OrderApiController {
	private final OrderService orderService;
	private final PaymentService paymentService;

	public OrderApiController(OrderService orderService,
							  PaymentService paymentService) {
		this.orderService = orderService;
		this.paymentService = paymentService;
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

	@PostMapping("/payment")
	@ResponseStatus(HttpStatus.CREATED)
	public Long payment(
			@RequestBody OrderDto.OrderPayRequest orderPayRequest
	) throws JsonProcessingException {
		return paymentService.pay(orderPayRequest);
	}

	@PostMapping("/serverAuth")
	@ResponseStatus(HttpStatus.CREATED)
	public OrderDto.OrderPayResponse nicePaymentAuth(
			@RequestParam String tid,
			@RequestParam Long amount,
			@RequestParam Long orderId
	) throws JsonProcessingException {
		return paymentService.nicePaymentServerAuth(tid, amount, orderId);
	}
}

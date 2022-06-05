package com.example.bookclub.application.order.payment;

import com.example.bookclub.dto.OrderDto;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PaymentService {
	@Transactional
	public OrderDto.OrderPayResponse pay(OrderDto.OrderPayRequest orderPayRequest) {
		return null;
	}
}

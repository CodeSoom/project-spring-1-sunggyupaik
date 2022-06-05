package com.example.bookclub.application.order.payment;

import com.example.bookclub.domain.order.payment.PayMethod;
import com.example.bookclub.dto.OrderDto;
import com.example.bookclub.infrastructure.order.payment.NicePayProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PaymentService {
	private final NicePayProcessor nicePayProcessor;

	public PaymentService(NicePayProcessor nicePayProcessor) {
		this.nicePayProcessor = nicePayProcessor;
	}

	@Transactional
	public Long pay(OrderDto.OrderPayRequest orderPayRequest)
			throws JsonProcessingException {
		if (orderPayRequest.getPayMethod() == PayMethod.NICE_PAY) {
			nicePayProcessor.pay(orderPayRequest);
		}

		return orderPayRequest.getOrderId();
	}

	@Transactional
	public OrderDto.OrderPayResponse nicePaymentServerAuth(String tid, Long amount, Long orderId)
			throws JsonProcessingException {
		return nicePayProcessor.nicePaymentServerAuth(tid, amount, orderId);
	}
}

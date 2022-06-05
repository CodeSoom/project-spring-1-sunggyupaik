package com.example.bookclub.application.order.payment;

import com.example.bookclub.domain.order.payment.PayMethod;
import com.example.bookclub.dto.OrderDto;
import com.example.bookclub.infrastructure.order.payment.NicePayProcessor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
public class PaymentService {
	private final NicePayProcessor nicePayProcessor;

	public PaymentService(NicePayProcessor nicePayProcessor) {
		this.nicePayProcessor = nicePayProcessor;
	}

	@Transactional
	public OrderDto.OrderPayResponse pay(OrderDto.OrderPayRequest orderPayRequest) {
		if(Objects.equals(orderPayRequest.getPayMethod(), PayMethod.NICE_PAY.name())) {
			nicePayProcessor.nicePayProcess(orderPayRequest);
		}

		return null;
	}
}

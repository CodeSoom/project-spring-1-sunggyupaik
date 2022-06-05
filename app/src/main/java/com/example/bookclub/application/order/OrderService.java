package com.example.bookclub.application.order;

import com.example.bookclub.domain.order.Order;
import com.example.bookclub.domain.order.OrderSeriesFactory;
import com.example.bookclub.dto.OrderDto;
import com.example.bookclub.infrastructure.order.JpaOrderRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class OrderService {
	private final JpaOrderRepository orderRepository;
	private final OrderSeriesFactory orderSeriesFactory;

	public OrderService(JpaOrderRepository orderRepository,
						OrderSeriesFactory orderSeriesFactory) {
		this.orderRepository = orderRepository;
		this.orderSeriesFactory = orderSeriesFactory;
	}

	@Transactional
	public OrderDto.OrderCreateResponse create(OrderDto.OrderCreateRequest orderCreateRequest) {
		Order order = orderCreateRequest.toEntity();
		orderRepository.save(order);
		orderSeriesFactory.createOrder(order, orderCreateRequest);

		return OrderDto.OrderCreateResponse.of(order);
	}
}

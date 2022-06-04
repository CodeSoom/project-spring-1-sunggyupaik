package com.example.bookclub.application.order;

import com.example.bookclub.domain.order.Order;
import com.example.bookclub.dto.OrderDto;
import com.example.bookclub.infrastructure.order.JpaOrderRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class OrderService {
	private final JpaOrderRepository orderRepository;

	public OrderService(JpaOrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Transactional
	public OrderDto.OrderCreateResponse create(OrderDto.OrderCreateRequest orderCreateRequest) {
		Order order = orderCreateRequest.toEntity();
		Order savedOrder = orderRepository.save(order);
		return OrderDto.OrderCreateResponse.of(savedOrder);
	}
}

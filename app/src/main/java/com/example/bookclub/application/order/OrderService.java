package com.example.bookclub.application.order;

import com.example.bookclub.domain.order.Order;
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
	public Order create(Order order) {
		return orderRepository.save(order);
	}
}

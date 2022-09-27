package com.example.bookclub.application.order;

import com.example.bookclub.common.exception.order.OrderNotFoundException;
import com.example.bookclub.domain.order.Order;
import com.example.bookclub.domain.order.OrderSeriesFactory;
import com.example.bookclub.domain.order.item.OrderItem;
import com.example.bookclub.dto.OrderDto;
import com.example.bookclub.infrastructure.order.JpaOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

	@Transactional(readOnly = true)
	public OrderDto.OrderDetailResponse getOrder(Long id) {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new OrderNotFoundException(id));
		List<OrderItem> orderItemLists = order.getOrderItems();

		return OrderDto.OrderDetailResponse.of(order, orderItemLists);
	}
}

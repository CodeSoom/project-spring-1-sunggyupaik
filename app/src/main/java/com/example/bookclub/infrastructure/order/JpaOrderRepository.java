package com.example.bookclub.infrastructure.order;

import com.example.bookclub.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderRepository
		extends OrderRepositoryCustom, JpaRepository<Order, Long> {
	Order save(Order order);
}

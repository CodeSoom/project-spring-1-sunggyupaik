package com.example.bookclub.infrastructure.order.orderitem;

import com.example.bookclub.domain.order.item.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderItemRepository extends JpaRepository<OrderItem, Long> {
	OrderItem save(OrderItem orderItem);
}

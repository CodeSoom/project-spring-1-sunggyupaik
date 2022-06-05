package com.example.bookclub.infrastructure.order.orderitemgroup;

import com.example.bookclub.domain.order.item.OrderItemOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderItemOptionGroupRepository extends JpaRepository<OrderItemOptionGroup, Long> {
	OrderItemOptionGroup save(OrderItemOptionGroup orderItemOptionGroup);
}

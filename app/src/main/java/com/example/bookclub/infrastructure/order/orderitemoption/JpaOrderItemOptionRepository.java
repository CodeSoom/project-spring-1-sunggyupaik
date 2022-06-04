package com.example.bookclub.infrastructure.order.orderitemoption;

import com.example.bookclub.domain.order.item.OrderItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderItemOptionRepository extends JpaRepository<OrderItemOption, Long> {
	OrderItemOption save(OrderItemOption orderItemOption);
}

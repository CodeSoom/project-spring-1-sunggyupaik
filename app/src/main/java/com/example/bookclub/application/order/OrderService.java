package com.example.bookclub.application.order;

import com.example.bookclub.domain.order.Order;
import com.example.bookclub.domain.order.item.OrderItem;
import com.example.bookclub.domain.order.item.OrderItemOption;
import com.example.bookclub.domain.order.item.OrderItemOptionGroup;
import com.example.bookclub.dto.OrderDto;
import com.example.bookclub.infrastructure.order.JpaOrderRepository;
import com.example.bookclub.infrastructure.order.orderitem.JpaOrderItemRepository;
import com.example.bookclub.infrastructure.order.orderitemgroup.JpaOrderItemOptionGroupRepository;
import com.example.bookclub.infrastructure.order.orderitemoption.JpaOrderItemOptionRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class OrderService {
	private final JpaOrderRepository orderRepository;
	private final JpaOrderItemRepository orderItemRepository;
	private final JpaOrderItemOptionGroupRepository orderItemGroupRepository;
	private final JpaOrderItemOptionRepository orderItemOptionRepository;

	public OrderService(JpaOrderRepository orderRepository,
						JpaOrderItemRepository orderItemRepository,
						JpaOrderItemOptionGroupRepository orderItemGroupRepository,
						JpaOrderItemOptionRepository orderItemOptionRepository) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.orderItemGroupRepository = orderItemGroupRepository;
		this.orderItemOptionRepository = orderItemOptionRepository;
	}

	@Transactional
	public OrderDto.OrderCreateResponse create(OrderDto.OrderCreateRequest orderCreateRequest) {
		Order order = orderCreateRequest.toEntity();
		orderRepository.save(order);

		OrderDto.OrderCreateResponse orderCreateResponse =
				(OrderDto.OrderCreateResponse) orderCreateRequest.getOrderItemCreateRequests().stream().map(orderItemCreateRequest -> {
			OrderItem orderItem = orderItemCreateRequest.toEntity();
			orderItem.setOrder(order);
			orderItemRepository.save(orderItem);

			orderItemCreateRequest.getOrderItemOptionGroupCreateRequests().forEach(
					orderItemOptionGroupCreateRequest -> {
						OrderItemOptionGroup orderItemOptionGroup = orderItemOptionGroupCreateRequest.toEntity();
						orderItemOptionGroup.setOrderItem(orderItem);
						orderItemGroupRepository.save(orderItemOptionGroup);

						orderItemOptionGroupCreateRequest.getOrderItemOptionCreateRequests().forEach(
								orderItemOptionCreateRequest -> {
									OrderItemOption orderItemOption = orderItemOptionCreateRequest.toEntity();
									orderItemOption.setOrderItemOptionGroup(orderItemOptionGroup);
									orderItemOptionRepository.save(orderItemOption);
								}
						);
					}
			);

			return OrderDto.OrderCreateResponse.builder()
					.id(order.getId())
					.accountId(orderCreateRequest.getAccountId())
					.payMethod(orderCreateRequest.getPayMethod())
					.build();
		});

		return orderCreateResponse;
	}
}

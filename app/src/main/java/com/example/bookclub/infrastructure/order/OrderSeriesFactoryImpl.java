package com.example.bookclub.infrastructure.order;

import com.example.bookclub.domain.order.Order;
import com.example.bookclub.domain.order.OrderSeriesFactory;
import com.example.bookclub.domain.order.item.OrderItem;
import com.example.bookclub.domain.order.item.OrderItemOption;
import com.example.bookclub.domain.order.item.OrderItemOptionGroup;
import com.example.bookclub.dto.OrderDto;
import com.example.bookclub.infrastructure.order.orderitem.JpaOrderItemRepository;
import com.example.bookclub.infrastructure.order.orderitemgroup.JpaOrderItemOptionGroupRepository;
import com.example.bookclub.infrastructure.order.orderitemoption.JpaOrderItemOptionRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderSeriesFactoryImpl implements OrderSeriesFactory {
	private final JpaOrderItemRepository orderItemRepository;
	private final JpaOrderItemOptionGroupRepository orderItemGroupRepository;
	private final JpaOrderItemOptionRepository orderItemOptionRepository;

	public OrderSeriesFactoryImpl(JpaOrderItemRepository orderItemRepository,
								  JpaOrderItemOptionGroupRepository orderItemGroupRepository,
								  JpaOrderItemOptionRepository orderItemOptionRepository) {
		this.orderItemRepository = orderItemRepository;
		this.orderItemGroupRepository = orderItemGroupRepository;
		this.orderItemOptionRepository = orderItemOptionRepository;
	}

	@Override
	public OrderDto.OrderCreateResponse createOrder(Order order, OrderDto.OrderCreateRequest orderCreateRequest) {
		return (OrderDto.OrderCreateResponse) orderCreateRequest.getOrderItemCreateRequests().stream().map(orderItemCreateRequest -> {
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
	}

	@Override
	public OrderDto.OrderDetailResponse detailOrder(Order order) {
		return null;
	}
}

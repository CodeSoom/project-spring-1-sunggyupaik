package com.example.bookclub.domain.order;

import com.example.bookclub.common.BaseEntity;
import com.example.bookclub.domain.order.deliveryaddress.DeliveryAddress;
import com.example.bookclub.domain.order.item.OrderItem;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@Table(name = "ORDERS")
public class Order extends BaseEntity {
	@Id @GeneratedValue
	@Column(name = "ORDER_ID")
	private Long id;

	private Long accountId;

	private String payMethod;

	private OrderStatus orderStatus;

	@Embedded
	private DeliveryAddress deliveryAddress;

	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	@Builder.Default
	@ToString.Exclude
	List<OrderItem> orderItems = new ArrayList<>();

	@Builder
	@QueryProjection
	public Order(Long id, Long accountId, String payMethod, OrderStatus orderStatus,
				 DeliveryAddress deliveryAddress, List<OrderItem> orderItems) {
		this.id = id;
		this.accountId = accountId;
		this.payMethod = payMethod;
		this.orderStatus = orderStatus;
		this.deliveryAddress = deliveryAddress;
		this.orderItems = orderItems;
	}

	public Long calculatePriceAmount() {
		return orderItems.stream().mapToLong(OrderItem::calculatePriceAmount).sum();
	}
}

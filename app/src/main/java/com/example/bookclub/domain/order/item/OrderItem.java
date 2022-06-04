package com.example.bookclub.domain.order.item;

import com.example.bookclub.common.AccountEntityListener;
import com.example.bookclub.common.BaseEntity;
import com.example.bookclub.domain.order.Order;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@EntityListeners(value = { AccountEntityListener.class })
public class OrderItem extends BaseEntity {
	@Id @GeneratedValue
	@Column(name = "ORDER_ITEM_ID")
	private Long id;

	private Long itemId;

	private Integer count;

	private Long price;

	private String name;

	@Enumerated(EnumType.STRING)
	private DeliveryStatus deliveryStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDER_ID")
	@ToString.Exclude
	private Order order;

	@OneToMany(mappedBy = "orderItem", fetch = FetchType.LAZY)
	@Builder.Default
	@ToString.Exclude
	List<OrderItemOptionGroup> orderItemOptionGroups = new ArrayList<>();

	@Builder
	public OrderItem(Long id, Long itemId, Integer count, Long price, String name,
					 DeliveryStatus deliveryStatus, Order order, List<OrderItemOptionGroup> orderItemOptionGroups) {
		this.id = id;
		this.itemId = itemId;
		this.count = count;
		this.price = price;
		this.name = name;
		this.deliveryStatus = deliveryStatus;
		this.order = order;
		this.orderItemOptionGroups = orderItemOptionGroups;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
}

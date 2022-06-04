package com.example.bookclub.domain.order.item;

import com.example.bookclub.common.AccountEntityListener;
import com.example.bookclub.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@EntityListeners(value = { AccountEntityListener.class })
public class OrderItemOptionGroup extends BaseEntity {
	@Id @GeneratedValue
	@Column(name = "ORDER_ITEM_OPTION_GROUP")
	private Long id;

	private Integer ordering;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDER_ITEM_ID")
	@ToString.Exclude
	private OrderItem orderItem;

	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}

	@Builder
	public OrderItemOptionGroup(Long id, Integer ordering, String name, OrderItem orderItem) {
		this.id = id;
		this.ordering = ordering;
		this.name = name;
		this.orderItem = orderItem;
	}
}

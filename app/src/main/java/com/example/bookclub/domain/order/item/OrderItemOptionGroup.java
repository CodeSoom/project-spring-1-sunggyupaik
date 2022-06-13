package com.example.bookclub.domain.order.item;

import com.example.bookclub.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
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
public class OrderItemOptionGroup extends BaseEntity {
	@Id @GeneratedValue
	@Column(name = "ORDER_ITEM_OPTION_GROUP_ID")
	private Long id;

	private Integer ordering;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDER_ITEM_ID")
	@ToString.Exclude
	private OrderItem orderItem;

	@OneToMany(mappedBy = "orderItemOptionGroup", fetch = FetchType.LAZY)
	@Builder.Default
	@ToString.Exclude
	List<OrderItemOption> orderItemOptions = new ArrayList<>();

	@Builder
	public OrderItemOptionGroup(Long id, Integer ordering, String name, OrderItem orderItem,
								List<OrderItemOption> orderItemOptions) {
		this.id = id;
		this.ordering = ordering;
		this.name = name;
		this.orderItem = orderItem;
		this.orderItemOptions = orderItemOptions;
	}

	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}

	public Long calculatePriceAmount() {
		return orderItemOptions.stream()
				.mapToLong(OrderItemOption::getPrice).sum();
	}
}

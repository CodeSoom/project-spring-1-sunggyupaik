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
public class OrderItemOption extends BaseEntity {
	@Id @GeneratedValue
	@Column(name = "ORDER_ITEM_OPTION_ID")
	private Long id;

	private Integer ordering;

	private String name;

	private Long price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDER_ITEM_OPTION_GROUP_ID")
	@ToString.Exclude
	OrderItemOptionGroup orderItemOptionGroup;

	@Builder
	public OrderItemOption(Long id, Integer ordering, String name, Long price,
						   OrderItemOptionGroup orderItemOptionGroup) {
		this.id = id;
		this.ordering = ordering;
		this.name = name;
		this.price = price;
		this.orderItemOptionGroup = orderItemOptionGroup;
	}
}

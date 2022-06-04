package com.example.bookclub.domain.order;

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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@EntityListeners(value = { AccountEntityListener.class })
public class Order extends BaseEntity {
	@Id @GeneratedValue
	@Column(name = "ORDER_ID")
	private Long id;

	private Long accountId;

	private String payMethod;

	private OrderStatus orderStatus;

	@Builder
	public Order(Long id, Long accountId, String payMethod, OrderStatus orderStatus) {
		this.id = id;
		this.accountId = accountId;
		this.payMethod = payMethod;
		this.orderStatus = orderStatus;
	}
}

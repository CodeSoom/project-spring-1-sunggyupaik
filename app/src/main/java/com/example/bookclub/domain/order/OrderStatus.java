package com.example.bookclub.domain.order;

import com.example.bookclub.common.EnumMapperType;

public enum OrderStatus implements EnumMapperType {
	INIT("주문시작"),
	ORDER_COMPLETE("주문완료"),
	DELIVERY_PREPARE("배송준비"),
	IN_DELIVERY("배송중"),
	DELIVERY_COMPLETE("배송완료");

	private final String orderStatus;

	OrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Override
	public String getCode() {
		return name();
	}

	@Override
	public String getTitle() {
		return orderStatus;
	}
}

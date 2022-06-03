package com.example.bookclub.domain.Item;

import com.example.bookclub.common.EnumMapperType;

public enum OrderStatus implements EnumMapperType {
	PREPARE("판매준비중"),
	ON_SALE("판매중"),
	END_OF_SALE("판매종료");

	private String orderStatus;

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

package com.example.bookclub.domain.order.item;

import com.example.bookclub.common.EnumMapperType;

public enum DeliveryStatus implements EnumMapperType {
	BEFORE_DELIVERY("배송전"),
	DELIVERY_PREPARE("배송준비중"),
	DELIVERING("배송중"),
	COMPLETE_DELIVERY("배송완료");

	private final String deliveryStatus;

	DeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	@Override
	public String getCode() {
		return name();
	}

	@Override
	public String getTitle() {
		return this.deliveryStatus;
	}
}

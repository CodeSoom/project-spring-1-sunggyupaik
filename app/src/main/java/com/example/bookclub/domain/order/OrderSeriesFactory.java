package com.example.bookclub.domain.order;

import com.example.bookclub.dto.OrderDto;

public interface OrderSeriesFactory {
	OrderDto.OrderCreateResponse createOrder(Order order , OrderDto.OrderCreateRequest orderCreateRequest);
}

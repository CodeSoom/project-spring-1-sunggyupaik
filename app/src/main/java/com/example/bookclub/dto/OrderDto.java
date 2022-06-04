package com.example.bookclub.dto;


import com.example.bookclub.domain.order.Order;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class OrderDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class OrderCreateResultDto {
		private Long id;

		private Long accountId;

		private String payMethod;

		@Builder
		@QueryProjection
		public OrderCreateResultDto(Long id, Long accountId, String payMethod) {
			this.id = id;
			this.accountId = accountId;
			this.payMethod = payMethod;
		}

		public static OrderCreateResultDto of(Order order) {
			return OrderCreateResultDto.builder()
					.id(order.getId())
					.accountId(order.getAccountId())
					.payMethod(order.getPayMethod())
					.build();
		}
	}
}

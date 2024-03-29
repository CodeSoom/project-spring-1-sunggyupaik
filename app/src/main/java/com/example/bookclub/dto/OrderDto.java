package com.example.bookclub.dto;


import com.example.bookclub.domain.order.Order;
import com.example.bookclub.domain.order.deliveryaddress.DeliveryAddress;
import com.example.bookclub.domain.order.item.OrderItem;
import com.example.bookclub.domain.order.item.OrderItemOption;
import com.example.bookclub.domain.order.item.OrderItemOptionGroup;
import com.example.bookclub.domain.order.payment.PayMethod;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class OrderDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class OrderCreateResponse {
		private Long id;

		private Long accountId;

		private String payMethod;

		@Builder
		@QueryProjection
		public OrderCreateResponse(Long id, Long accountId, String payMethod) {
			this.id = id;
			this.accountId = accountId;
			this.payMethod = payMethod;
		}

		public static OrderCreateResponse of(Order order) {
			return OrderCreateResponse.builder()
					.id(order.getId())
					.accountId(order.getAccountId())
					.payMethod(order.getPayMethod())
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class OrderCreateRequest {
		@NotNull(message = "accountId 는 필수값입니다")
		private Long accountId;

		@NotBlank(message = "payMethod 는 필수값입니다")
		private String payMethod;

		@NotBlank(message = "receiverName 는 필수값입니다")
		private String receiverName;

		@NotBlank(message = "receiverPhone 는 필수값입니다")
		private String receiverPhone;

		@NotBlank(message = "receiverZipcode 는 필수값입니다")
		private String receiverZipcode;

		@NotBlank(message = "receiverAddress1 는 필수값입니다")
		private String receiverAddress1;

		@NotBlank(message = "receiverAddress2 는 필수값입니다")
		private String receiverAddress2;

		@NotBlank(message = "etcMessage 는 필수값입니다")
		private String etcMessage;

		private List<OrderItemCreateRequest> orderItemCreateRequests;

		@Builder
		public OrderCreateRequest(Long accountId, String receiverName, String receiverPhone, String receiverZipcode,
								  String receiverAddress1, String receiverAddress2, String etcMessage,
								  List<OrderItemCreateRequest> orderItemCreateRequests) {
			this.accountId = accountId;
			this.receiverName = receiverName;
			this.receiverPhone = receiverPhone;
			this.receiverZipcode = receiverZipcode;
			this.receiverAddress1 = receiverAddress1;
			this.receiverAddress2 = receiverAddress2;
			this.etcMessage = etcMessage;
			this.orderItemCreateRequests = orderItemCreateRequests;
		}

		public Order toEntity() {
			DeliveryAddress deliveryAddress = DeliveryAddress.builder()
					.receiverName(this.receiverName)
					.receiverPhone(this.receiverPhone)
					.receiverZipcode(this.receiverZipcode)
					.receiverAddress1(this.receiverAddress1)
					.receiverAddress2(this.receiverAddress2)
					.etcMessage(this.etcMessage)
					.build();

			return Order.builder()
					.accountId(this.accountId)
					.payMethod(this.payMethod)
					.deliveryAddress(deliveryAddress)
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class OrderItemCreateRequest {
		@NotNull(message = "accountId 는 필수값입니다")
		private Long itemId;

		@NotBlank(message = "count 는 필수값입니다")
		private Integer count;

		@NotBlank(message = "price 는 필수값입니다")
		private Long price;

		@NotBlank(message = "name 는 필수값입니다")
		private String name;

		private List<OrderItemOptionGroupCreateRequest> orderItemOptionGroupCreateRequests;

		@Builder
		public OrderItemCreateRequest(Long itemId, Integer count, Long price, String name,
									  List<OrderItemOptionGroupCreateRequest> orderItemOptionGroupCreateRequests) {
			this.itemId = itemId;
			this.count = count;
			this.price = price;
			this.name = name;
			this.orderItemOptionGroupCreateRequests = orderItemOptionGroupCreateRequests;
		}

		public OrderItem toEntity() {
			return OrderItem.builder()
					.itemId(this.itemId)
					.count(this.count)
					.price(this.price)
					.name(this.name)
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class OrderItemOptionGroupCreateRequest {
		@NotBlank(message = "ordering 는 필수값입니다")
		private Integer ordering;

		@NotBlank(message = "name 는 필수값입니다")
		private String name;

		private List<OrderItemOptionCreateRequest> orderItemOptionCreateRequests;

		@Builder
		public OrderItemOptionGroupCreateRequest(Integer ordering, String name,
												 List<OrderItemOptionCreateRequest> orderItemOptionCreateRequests) {
			this.ordering = ordering;
			this.name = name;
			this.orderItemOptionCreateRequests = orderItemOptionCreateRequests;
		}

		public OrderItemOptionGroup toEntity() {
			return OrderItemOptionGroup.builder()
					.ordering(this.ordering)
					.name(this.name)
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class OrderItemOptionCreateRequest {
		@NotBlank(message = "ordering 는 필수값입니다")
		private Integer ordering;

		@NotBlank(message = "name 는 필수값입니다")
		private String name;

		@NotBlank(message = "price 는 필수값입니다")
		private Long price;

		private List<OrderItemOptionCreateRequest> orderItemOptionCreateRequests;

		@Builder
		public OrderItemOptionCreateRequest(Integer ordering, String name, Long price,
											List<OrderItemOptionCreateRequest> orderItemOptionCreateRequests) {
			this.ordering = ordering;
			this.name = name;
			this.orderItemOptionCreateRequests = orderItemOptionCreateRequests;
		}

		public OrderItemOption toEntity() {
			return OrderItemOption.builder()
					.ordering(this.ordering)
					.name(this.name)
					.price(this.price)
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class OrderDetailResponse {
		private Long id;

		private Long accountId;

		private String payMethod;

		private Long priceAmount;

		private DeliveryAddress deliveryAddress;

		private String orderStatus;

		private String orderStatusName;

		private List<OrderItem> orderItems;

		@Builder
		public OrderDetailResponse(Long id, Long accountId, String payMethod, Long priceAmount,
								   DeliveryAddress deliveryAddress, String orderStatus, String orderStatusName,
								   List<OrderItem> orderItems) {
			this.id = id;
			this.accountId = accountId;
			this.payMethod = payMethod;
			this.priceAmount = priceAmount;
			this.deliveryAddress = deliveryAddress;
			this.orderStatus = orderStatus;
			this.orderStatusName = orderStatusName;
			this.orderItems = orderItems;
		}

		public static OrderDetailResponse of(Order order, List<OrderItem> orderItems) {
			return OrderDetailResponse.builder()
					.payMethod(order.getPayMethod())
					.priceAmount(order.calculatePriceAmount())
					.deliveryAddress(order.getDeliveryAddress())
					.orderStatus(order.getOrderStatus().getCode())
					.orderStatusName(order.getOrderStatus().getTitle())
					.orderItems(orderItems)
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class OrderPayRequest {
		private Long orderId;

		private PayMethod payMethod;

		private Long amount;

		@Builder
		public OrderPayRequest(Long orderId, PayMethod payMethod, Long amount) {
			this.orderId = orderId;
			this.payMethod = payMethod;
			this.amount = amount;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class OrderPayResponse {
		private Long orderId;

		private String payMethod;

		private Long amount;

		@Builder
		public OrderPayResponse(Long orderId, String payMethod, Long amount) {
			this.orderId = orderId;
			this.payMethod = payMethod;
			this.amount = amount;
		}

		public static OrderPayResponse of(Order order) {
			return OrderPayResponse.builder()
					.orderId(order.getId())
					.payMethod(order.getPayMethod())
					.amount(order.calculatePriceAmount())
					.build();
		}
	}
}

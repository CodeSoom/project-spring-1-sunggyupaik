package com.example.bookclub.domain.order.deliveryaddress;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryAddress {
	private String receiverName;
	private String receiverPhone;
	private String receiverZipcode;
	private String receiverAddress1;
	private String receiverAddress2;
	private String etcMessage;

	@Builder
	public DeliveryAddress(String receiverName, String receiverPhone, String receiverZipcode,
						   String receiverAddress1, String receiverAddress2, String etcMessage) {
		this.receiverName = receiverName;
		this.receiverPhone = receiverPhone;
		this.receiverZipcode = receiverZipcode;
		this.receiverAddress1 = receiverAddress1;
		this.receiverAddress2 = receiverAddress2;
		this.etcMessage = etcMessage;
	}
}

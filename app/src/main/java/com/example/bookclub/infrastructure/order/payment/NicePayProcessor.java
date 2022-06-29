package com.example.bookclub.infrastructure.order.payment;

import com.example.bookclub.common.exception.order.OrderNotFoundException;
import com.example.bookclub.domain.order.Order;
import com.example.bookclub.dto.OrderDto;
import com.example.bookclub.infrastructure.order.JpaOrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class NicePayProcessor {
	private final ObjectMapper objectMapper;
	private final RestTemplate restTemplate;
	private final JpaOrderRepository orderRepository;

	public NicePayProcessor(ObjectMapper objectMapper,
							RestTemplate restTemplate,
							JpaOrderRepository orderRepository) {
		this.objectMapper = objectMapper;
		this.restTemplate = restTemplate;
		this.orderRepository = orderRepository;
	}

	private String CLIENT_ID = "S2_299d7444916a46d29cffc772336f15e8";
	private String SECRET_KEY = "ea9e673332934016ba405238b7d60a9a";

	public void pay(OrderDto.OrderPayRequest orderPayRequest) throws JsonProcessingException {

	}

	public OrderDto.OrderPayResponse nicePaymentServerAuth(
			String tid, Long amount, Long orderId
	) throws JsonProcessingException {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException(orderId));

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + SECRET_KEY).getBytes()));
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> AuthenticationMap = new HashMap<>();
		AuthenticationMap.put("amount", String.valueOf(amount));

		HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(AuthenticationMap), headers);

		ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
				"https://sandbox-api.nicepay.co.kr/v1/payments/" + tid, request, JsonNode.class);

		JsonNode responseNode = responseEntity.getBody();
		String resultCode = responseNode.get("resultCode").asText();
		//model.addAttribute("resultMsg", responseNode.get("resultMsg").asText());
		if (resultCode.equalsIgnoreCase("0000")) {
			// 결제 성공 비즈니스 로직 구현
		} else {
			// 결제 실패 비즈니스 로직 구현
		}

		System.out.println(responseNode.toPrettyString());

		return OrderDto.OrderPayResponse.of(order);
	}
}

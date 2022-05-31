package com.example.bookclub.common.utils;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer {
	private final RabbitTemplate rabbitTemplate;

	public Producer(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void sendTo(String message) {
		this.rabbitTemplate.convertAndSend("CREATE_POST_QUEUE", message);
	}
}

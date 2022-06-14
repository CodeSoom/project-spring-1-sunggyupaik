package com.example.bookclub.common.util;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class DiaryProducer {
	private final RabbitTemplate rabbitTemplate;

	public DiaryProducer(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void sendTo(String message) {
		this.rabbitTemplate.convertAndSend("CREATE_DIARY_QUEUE", message);
	}
}

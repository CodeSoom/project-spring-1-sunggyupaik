package com.example.bookclub.dto;

import com.example.bookclub.domain.Item.Item;
import com.example.bookclub.domain.Item.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

public class ItemDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class ItemResultDto {
		private Long id;

		private String title;

		private String image;

		private String description;

		@JsonFormat(pattern = "yyyy-MM-dd")
		private LocalDate publishedDate;

		private String author;

		private Long price;

		@Enumerated(EnumType.STRING)
		private OrderStatus status;

		@Builder
		public ItemResultDto(Long id, String title, String image, String description, LocalDate publishedDate,
							 String author, Long price, OrderStatus status) {
			this.id = id;
			this.title = title;
			this.image = image;
			this.description = description;
			this.publishedDate = publishedDate;
			this.author = author;
			this.price = price;
			this.status = status;
		}

		public static ItemResultDto of(Item item) {
			return ItemResultDto.builder()
					.id(item.getId())
					.title(item.getTitle())
					.image(item.getImage())
					.description(item.getDescription())
					.publishedDate(item.getPublishedDate())
					.author(item.getAuthor())
					.price(item.getPrice())
					.status(item.getStatus())
					.build();
		}
	}
}

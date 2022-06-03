package com.example.bookclub.domain.Item;

import com.example.bookclub.common.AccountEntityListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@EntityListeners(value = { AccountEntityListener.class })
public class Item {
	@Id @GeneratedValue
	@Column(name = "ITEM_ID")
	private Long id;

	private String name;

	private String image;

	private String description;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate publishedDate;

	private String author;

	private String publisher;

	private Long price;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;
}

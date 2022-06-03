package com.example.bookclub.domain.Item;

import com.example.bookclub.common.AccountEntityListener;
import com.example.bookclub.common.BaseEntity;
import com.example.bookclub.domain.Item.itemoptiongroup.ItemOptionGroup;
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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@EntityListeners(value = { AccountEntityListener.class })
public class Item extends BaseEntity {
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

	@OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
	@Builder.Default
	@ToString.Exclude
	List<ItemOptionGroup> itemOptionGroups = new ArrayList<>();

	@Builder
	public Item(Long id, String name, String image, String description, LocalDate publishedDate,
				String author, String publisher, Long price, OrderStatus status, List<ItemOptionGroup> itemOptionGroups) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.description = description;
		this.publishedDate = publishedDate;
		this.author = author;
		this.publisher = publisher;
		this.price = price;
		this.status = status;
		this.itemOptionGroups = itemOptionGroups;
	}
}

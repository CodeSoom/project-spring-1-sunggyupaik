package com.example.bookclub.domain.Item.itemoption;

import com.example.bookclub.common.BaseEntity;
import com.example.bookclub.domain.Item.itemoptiongroup.ItemOptionGroup;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class ItemOption extends BaseEntity {
	@Id
	@GeneratedValue
	@Column(name = "ITEM_OPTION_ID")
	private Long id;

	private String name;

	private Integer ordering;

	private Long price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITEM_OPTION_GROUP_ID")
	@ToString.Exclude
	private ItemOptionGroup itemOptionGroup;

	@Builder
	public ItemOption(Long id, String name, Long price,
					  Integer ordering, ItemOptionGroup itemOptionGroup) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.ordering = ordering;
		this.itemOptionGroup = itemOptionGroup;
	}
}

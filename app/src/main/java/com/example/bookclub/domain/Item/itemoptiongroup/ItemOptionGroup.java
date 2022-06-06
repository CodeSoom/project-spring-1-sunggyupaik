package com.example.bookclub.domain.Item.itemoptiongroup;

import com.example.bookclub.common.BaseEntity;
import com.example.bookclub.domain.Item.Item;
import com.example.bookclub.domain.Item.itemoption.ItemOption;
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
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class ItemOptionGroup extends BaseEntity {
	@Id
	@GeneratedValue
	@Column(name = "ITEM_OPTION_GROUP_ID")
	private Long id;

	private String name;

	private Integer ordering;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITEM_ID")
	@ToString.Exclude
	private Item item;

	@OneToMany(mappedBy = "itemOptionGroup")
	@Builder.Default
	@ToString.Exclude
	private List<ItemOption> itemOptions = new ArrayList<>();

	@Builder
	public ItemOptionGroup(Long id, String name, Integer ordering,
						   Item item, List<ItemOption> itemOptions) {
		this.id = id;
		this.name = name;
		this.ordering = ordering;
		this.item = item;
		this.itemOptions = itemOptions;
	}
}

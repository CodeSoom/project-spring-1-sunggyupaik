package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageResultDto {
	private int currentPage;
	private int next;
	private int previous;
	private int totalPages;
	private List<Integer> pageNumbers;

	@Builder
	public PageResultDto(int currentPage, int next, int previous, int totalPages, List<Integer> pageNumbers) {
		this.currentPage = currentPage;
		this.next = next;
		this.previous = previous;
		this.totalPages = totalPages;
		this.pageNumbers = pageNumbers;
	}

	public static PageResultDto of(Page page) {
		int pageNumber = page.getPageable().getPageNumber() + 1;
		int size = page.getSize();
		int totalPages = page.getTotalPages();
		int totalElements = (int) page.getTotalElements();
		int pageSize = page.getPageable().getPageSize();

		int previous = pageNumber - 1;
		int next = Math.min(pageNumber + 1, totalPages);

		int start = pageNumber % size == 0 ?
				pageNumber - size + 1 : pageNumber - (pageNumber % size) + 1;
		int end = Math.min(start + pageSize - 1, totalPages);

		List<Integer> pageNumbers = new ArrayList<>();
		for(int i=start; i<=end; i++) {
			pageNumbers.add(i);
		}

		return PageResultDto.builder()
				.currentPage(pageNumber)
				.previous(previous)
				.next(next)
				.totalPages(totalPages)
				.pageNumbers(pageNumbers)
				.build();
	}
}

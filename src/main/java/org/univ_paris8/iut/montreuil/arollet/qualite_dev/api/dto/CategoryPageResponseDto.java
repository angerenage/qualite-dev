package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CategoryPageResponse", description = "Paginated categories result")
public class CategoryPageResponseDto {
	private List<CategoryResponseDto> items;
	@Schema(example = "0")
	private int page;
	@Schema(example = "20")
	private int size;
	@Schema(example = "12")
	private long totalItems;
	@Schema(example = "1")
	private int totalPages;

	public CategoryPageResponseDto() {
	}

	public CategoryPageResponseDto(List<CategoryResponseDto> items, int page, int size, long totalItems, int totalPages) {
		this.items = items;
		this.page = page;
		this.size = size;
		this.totalItems = totalItems;
		this.totalPages = totalPages;
	}

	public List<CategoryResponseDto> getItems() {
		return items;
	}

	public void setItems(List<CategoryResponseDto> items) {
		this.items = items;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public long getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(long totalItems) {
		this.totalItems = totalItems;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
}

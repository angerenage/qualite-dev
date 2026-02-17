package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.mappers;

import java.util.ArrayList;
import java.util.List;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.CategoryPageResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.CategoryResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.CreateCategoryRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.UpdateCategoryRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Category;

public class CategoryApiMapper {
	public String toLabel(CreateCategoryRequestDto dto) {
		if (dto == null) {
			return null;
		}
		return dto.getLabel();
	}

	public String toLabel(UpdateCategoryRequestDto dto) {
		if (dto == null) {
			return null;
		}
		return dto.getLabel();
	}

	public CategoryResponseDto toResponse(Category category) {
		if (category == null) {
			return null;
		}
		CategoryResponseDto dto = new CategoryResponseDto();
		dto.setId(category.getId());
		dto.setLabel(category.getLabel());
		return dto;
	}

	public CategoryPageResponseDto toPageResponse(List<Category> categories, int page, int size, long totalItems) {
		List<CategoryResponseDto> items = new ArrayList<>();
		if (categories != null) {
			for (Category category : categories) {
				items.add(toResponse(category));
			}
		}
		int totalPages = size > 0 ? (int) Math.ceil(totalItems / (double) size) : 0;
		return new CategoryPageResponseDto(items, page, size, totalItems, totalPages);
	}
}

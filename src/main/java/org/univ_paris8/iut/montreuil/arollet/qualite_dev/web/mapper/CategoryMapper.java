package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.mapper;

import org.mapstruct.Mapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.CategoryResponseDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDto toResponse(Category category);
}

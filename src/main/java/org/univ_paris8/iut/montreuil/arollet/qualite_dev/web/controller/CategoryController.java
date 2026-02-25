package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.service.CategoryService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.CategoryPageResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.CategoryResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.CreateCategoryRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.UpdateCategoryRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.mapper.CategoryMapper;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    public CategoryPageResponseDto list(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
        @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Category> categoryPage = categoryService.list(pageable, keyword);
        List<CategoryResponseDto> items = categoryPage.getContent().stream().map(categoryMapper::toResponse).toList();
        return new CategoryPageResponseDto(items, categoryPage.getNumber(), categoryPage.getSize(), categoryPage.getTotalElements(), categoryPage.getTotalPages());
    }

    @GetMapping("/{id}")
    public CategoryResponseDto detail(@PathVariable @Positive Long id) {
        return categoryMapper.toResponse(categoryService.detail(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(@Valid @RequestBody CreateCategoryRequestDto request) {
        Category created = categoryService.create(request.getLabel());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(categoryMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public CategoryResponseDto update(@PathVariable @Positive Long id, @Valid @RequestBody UpdateCategoryRequestDto request) {
        return categoryMapper.toResponse(categoryService.update(id, request.getLabel()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

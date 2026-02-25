package org.univ_paris8.iut.montreuil.arollet.qualite_dev.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.AppUserPrincipal;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.error.ApiException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<Category> list(Pageable pageable, String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return categoryRepository.findByLabelContainingIgnoreCase(keyword.trim(), pageable);
        }
        return categoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Category detail(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found for id=" + id));
    }

    @Transactional
    public Category create(String label) {
        requireAuthenticated();
        String normalized = normalizeLabel(label);
        categoryRepository.findByLabelIgnoreCase(normalized).ifPresent(c -> {
            throw new ApiException(HttpStatus.CONFLICT, "Category label already exists.");
        });
        Category category = new Category();
        category.setLabel(normalized);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, String label) {
        requireAuthenticated();
        Category category = detail(id);
        String normalized = normalizeLabel(label);
        categoryRepository.findByLabelIgnoreCase(normalized).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new ApiException(HttpStatus.CONFLICT, "Category label already exists.");
            }
        });
        category.setLabel(normalized);
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        requireAuthenticated();
        Category category = detail(id);
        try {
            categoryRepository.delete(category);
            categoryRepository.flush();
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.CONFLICT, "Category is used by existing annonces and cannot be deleted.");
        }
    }

    private String normalizeLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "label is required.");
        }
        String cleaned = label.trim();
        if (cleaned.length() > 128) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "label must contain at most 128 characters.");
        }
        return cleaned;
    }

    private void requireAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserPrincipal)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Authentication is required.");
        }
    }
}

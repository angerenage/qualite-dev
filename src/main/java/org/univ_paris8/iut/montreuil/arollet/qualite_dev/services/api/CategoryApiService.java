package org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.api;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.SessionFactory;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ApiException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.BusinessConflictException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.NotFoundException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories.CategoryRepository;

import jakarta.ws.rs.core.Response;

public class CategoryApiService {
	private final CategoryRepository categoryRepository;

	public CategoryApiService() {
		this(new CategoryRepository());
	}

	CategoryApiService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public List<Category> list(int page, int size, String keyword) {
		EntityManager em = SessionFactory.getEntityManager();
		try {
			if (hasKeyword(keyword)) {
				return categoryRepository.searchByKeyword(em, keyword, page, size);
			}
			return categoryRepository.findAll(em, page, size);
		} finally {
			em.close();
		}
	}

	public long countAll(String keyword) {
		EntityManager em = SessionFactory.getEntityManager();
		try {
			if (hasKeyword(keyword)) {
				return categoryRepository.countByKeyword(em, keyword);
			}
			return categoryRepository.countAll(em);
		} finally {
			em.close();
		}
	}

	public Category getById(Long id) {
		validateId(id);
		EntityManager em = SessionFactory.getEntityManager();
		try {
			Category category = categoryRepository.findById(em, id);
			if (category == null) {
				throw new NotFoundException("Category not found for id=" + id);
			}
			return category;
		} finally {
			em.close();
		}
	}

	public Category create(String label, Long currentUserId) {
		String cleanedLabel = validateLabel(label);
		validateCurrentUserId(currentUserId);
		EntityManager em = SessionFactory.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			Category existing = categoryRepository.findByLabel(em, cleanedLabel);
			if (existing != null) {
				throw new BusinessConflictException("Category label already exists.");
			}

			Category category = new Category();
			category.setLabel(cleanedLabel);
			categoryRepository.create(em, category);
			tx.commit();
			return categoryRepository.findById(em, category.getId());
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			if (isConstraintViolation(e)) {
				throw new BusinessConflictException("Category label already exists.");
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public Category update(Long id, String label, Long currentUserId) {
		validateId(id);
		String cleanedLabel = validateLabel(label);
		validateCurrentUserId(currentUserId);
		EntityManager em = SessionFactory.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			Category category = categoryRepository.findById(em, id);
			if (category == null) {
				throw new NotFoundException("Category not found for id=" + id);
			}

			Category existing = categoryRepository.findByLabel(em, cleanedLabel);
			if (existing != null && !existing.getId().equals(id)) {
				throw new BusinessConflictException("Category label already exists.");
			}

			category.setLabel(cleanedLabel);
			tx.commit();
			return categoryRepository.findById(em, id);
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			if (isConstraintViolation(e)) {
				throw new BusinessConflictException("Category label already exists.");
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public void deleteCategory(Long id, Long currentUserId) {
		validateId(id);
		validateCurrentUserId(currentUserId);
		EntityManager em = SessionFactory.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			Category category = categoryRepository.findById(em, id);
			if (category == null) {
				throw new NotFoundException("Category not found for id=" + id);
			}

			boolean deleted = categoryRepository.deleteById(em, id);
			if (!deleted) {
				throw new NotFoundException("Category not found for id=" + id);
			}
			tx.commit();
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			if (isConstraintViolation(e)) {
				throw new BusinessConflictException("Category is used by existing annonces and cannot be deleted.");
			}
			throw e;
		} finally {
			em.close();
		}
	}

	private void validateId(Long id) {
		if (id == null || id <= 0) {
			throw new ApiException(Response.Status.BAD_REQUEST, "id must be a positive number.");
		}
	}

	private void validateCurrentUserId(Long currentUserId) {
		if (currentUserId == null || currentUserId <= 0) {
			throw new ApiException(Response.Status.UNAUTHORIZED, "Authentication is required.");
		}
	}

	private String validateLabel(String label) {
		String cleaned = clean(label);
		if (cleaned == null || cleaned.isEmpty()) {
			throw new ApiException(Response.Status.BAD_REQUEST, "label is required.");
		}
		if (cleaned.length() > 128) {
			throw new ApiException(Response.Status.BAD_REQUEST, "label must contain at most 128 characters.");
		}
		return cleaned;
	}

	private boolean hasKeyword(String keyword) {
		return keyword != null && !keyword.trim().isEmpty();
	}

	private boolean isConstraintViolation(RuntimeException e) {
		Throwable cause = e;
		while (cause != null) {
			if (cause.getClass().getName().contains("ConstraintViolationException")) {
				return true;
			}
			String message = cause.getMessage();
			if (message != null) {
				String lowered = message.toLowerCase();
				if (lowered.contains("constraint") || lowered.contains("duplicate") || lowered.contains("unique")) {
					return true;
				}
			}
			if (cause instanceof PersistenceException && cause.getCause() != null
					&& cause.getCause().getClass().getName().contains("ConstraintViolationException")) {
				return true;
			}
			cause = cause.getCause();
		}
		return false;
	}

	private String clean(String value) {
		return value == null ? null : value.trim();
	}
}

package org.univ_paris8.iut.montreuil.arollet.qualite_dev.services;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.SessionFactory;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories.CategoryRepository;

public class CategoryService {
	private final CategoryRepository categoryRepository = new CategoryRepository();

	public Category create(String label) {
		if (label == null || label.trim().isEmpty()) {
			throw new IllegalArgumentException("label is required.");
		}
		EntityManager em = SessionFactory.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Category category = new Category(label.trim());
			Category created = categoryRepository.create(em, category);
			tx.commit();
			return created;
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public List<Category> list(int page, int size) {
		EntityManager em = SessionFactory.getEntityManager();
		try {
			return categoryRepository.findAll(em, page, size);
		} finally {
			em.close();
		}
	}

	public Category findById(Long id) {
		EntityManager em = SessionFactory.getEntityManager();
		try {
			return categoryRepository.findById(em, id);
		} finally {
			em.close();
		}
	}
}

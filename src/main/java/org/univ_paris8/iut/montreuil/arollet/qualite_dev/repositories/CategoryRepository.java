package org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Category;

public class CategoryRepository {
	private static final int DEFAULT_PAGE_SIZE = 20;

	public Category create(EntityManager em, Category category) {
		em.persist(category);
		return category;
	}

	public Category update(EntityManager em, Category category) {
		return em.merge(category);
	}

	public boolean delete(EntityManager em, Category category) {
		if (category == null) {
			return false;
		}
		Category managed = em.contains(category) ? category : em.merge(category);
		em.remove(managed);
		return true;
	}

	public boolean deleteById(EntityManager em, Long id) {
		if (id == null) {
			return false;
		}
		Category managed = em.find(Category.class, id);
		if (managed == null) {
			return false;
		}
		em.remove(managed);
		return true;
	}

	public Category findById(EntityManager em, Long id) {
		if (id == null) {
			return null;
		}
		return em.find(Category.class, id);
	}

	public List<Category> findAll(EntityManager em, int page, int size) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Category> cq = cb.createQuery(Category.class);
		Root<Category> root = cq.from(Category.class);
		cq.select(root).orderBy(cb.desc(root.get("id")));
		TypedQuery<Category> query = em.createQuery(cq);
		applyPagination(query, page, size);
		return query.getResultList();
	}

	public List<Category> searchByKeyword(EntityManager em, String keyword, int page, int size) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return findAll(em, page, size);
		}
		String kw = "%" + keyword.trim().toLowerCase() + "%";
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Category> cq = cb.createQuery(Category.class);
		Root<Category> root = cq.from(Category.class);
		Predicate labelLike = cb.like(cb.lower(root.get("label")), kw);
		cq.select(root)
			.where(labelLike)
			.orderBy(cb.desc(root.get("id")));
		TypedQuery<Category> query = em.createQuery(cq);
		applyPagination(query, page, size);
		return query.getResultList();
	}

	private void applyPagination(TypedQuery<?> query, int page, int size) {
		int safePage = Math.max(0, page);
		int safeSize = size > 0 ? size : DEFAULT_PAGE_SIZE;
		query.setFirstResult(safePage * safeSize);
		query.setMaxResults(safeSize);
	}
}

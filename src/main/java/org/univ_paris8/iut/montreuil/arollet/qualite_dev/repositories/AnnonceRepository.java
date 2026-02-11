package org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.AnnonceStatus;

public class AnnonceRepository implements AnnonceRepositoryPort {
	private static final int DEFAULT_PAGE_SIZE = 20;

	public Annonce create(EntityManager em, Annonce annonce) {
		em.persist(annonce);
		return annonce;
	}

	public Annonce update(EntityManager em, Annonce annonce) {
		return em.merge(annonce);
	}

	public boolean delete(EntityManager em, Annonce annonce) {
		if (annonce == null) {
			return false;
		}
		Annonce managed = em.contains(annonce) ? annonce : em.merge(annonce);
		em.remove(managed);
		return true;
	}

	public boolean deleteById(EntityManager em, Long id) {
		if (id == null) {
			return false;
		}
		Annonce managed = em.find(Annonce.class, id);
		if (managed == null) {
			return false;
		}
		em.remove(managed);
		return true;
	}

	public Annonce findById(EntityManager em, Long id) {
		if (id == null) {
			return null;
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Annonce> cq = cb.createQuery(Annonce.class);
		Root<Annonce> root = cq.from(Annonce.class);
		root.fetch("author", JoinType.LEFT);
		root.fetch("category", JoinType.LEFT);
		cq.select(root)
			.where(cb.equal(root.get("id"), id))
			.distinct(true);
		TypedQuery<Annonce> query = em.createQuery(cq);
		List<Annonce> results = query.setMaxResults(1).getResultList();
		return results.isEmpty() ? null : results.get(0);
	}

	public List<Annonce> findAll(EntityManager em, int page, int size) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Annonce> cq = cb.createQuery(Annonce.class);
		Root<Annonce> root = cq.from(Annonce.class);
		root.fetch("author", JoinType.LEFT);
		root.fetch("category", JoinType.LEFT);
		cq.select(root)
			.distinct(true)
			.orderBy(cb.desc(root.get("id")));
		TypedQuery<Annonce> query = em.createQuery(cq);
		applyPagination(query, page, size);
		return query.getResultList();
	}

	public List<Annonce> searchByKeyword(EntityManager em, String keyword, int page, int size) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return findAll(em, page, size);
		}
		String kw = "%" + keyword.trim().toLowerCase() + "%";
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Annonce> cq = cb.createQuery(Annonce.class);
		Root<Annonce> root = cq.from(Annonce.class);
		root.fetch("author", JoinType.LEFT);
		root.fetch("category", JoinType.LEFT);
		Predicate titleLike = cb.like(cb.lower(root.get("title")), kw);
		Predicate descriptionLike = cb.like(cb.lower(root.get("description")), kw);
		Predicate adressLike = cb.like(cb.lower(root.get("adress")), kw);
		cq.select(root)
			.where(cb.or(titleLike, descriptionLike, adressLike))
			.distinct(true)
			.orderBy(cb.desc(root.get("id")));
		TypedQuery<Annonce> query = em.createQuery(cq);
		applyPagination(query, page, size);
		return query.getResultList();
	}

	public List<Annonce> findByCategoryAndStatus(EntityManager em, Long categoryId, AnnonceStatus status, int page, int size) {
		if (categoryId == null || status == null) {
			throw new IllegalArgumentException("categoryId and status are required.");
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Annonce> cq = cb.createQuery(Annonce.class);
		Root<Annonce> root = cq.from(Annonce.class);
		root.fetch("author", JoinType.LEFT);
		root.fetch("category", JoinType.LEFT);
		Predicate categoryMatch = cb.equal(root.get("category").get("id"), categoryId);
		Predicate statusMatch = cb.equal(root.get("status"), status);
		cq.select(root)
			.where(cb.and(categoryMatch, statusMatch))
			.distinct(true)
			.orderBy(cb.desc(root.get("id")));
		TypedQuery<Annonce> query = em.createQuery(cq);
		applyPagination(query, page, size);
		return query.getResultList();
	}

	private void applyPagination(TypedQuery<?> query, int page, int size) {
		int safePage = Math.max(0, page);
		int safeSize = size > 0 ? size : DEFAULT_PAGE_SIZE;
		query.setFirstResult(safePage * safeSize);
		query.setMaxResults(safeSize);
	}

	public long countAll(EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Annonce> root = cq.from(Annonce.class);
		cq.select(cb.count(root));
		TypedQuery<Long> query = em.createQuery(cq);
		return query.getSingleResult();
	}
}

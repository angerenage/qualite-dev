package org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories.api;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Annonce;

public class AnnonceApiRepository {
	private static final int DEFAULT_PAGE_SIZE = 20;
	private static final int MAX_PAGE_SIZE = 100;

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

	public long countAll(EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Annonce> root = cq.from(Annonce.class);
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
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

		List<Annonce> results = em.createQuery(cq).setMaxResults(1).getResultList();
		return results.isEmpty() ? null : results.get(0);
	}

	public Annonce create(EntityManager em, Annonce annonce) {
		em.persist(annonce);
		return annonce;
	}

	public boolean deleteById(EntityManager em, Long id) {
		if (id == null) {
			return false;
		}
		Annonce annonce = em.find(Annonce.class, id);
		if (annonce == null) {
			return false;
		}
		em.remove(annonce);
		return true;
	}

	private void applyPagination(TypedQuery<?> query, int page, int size) {
		int safePage = Math.max(0, page);
		int safeSize = size > 0 ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
		query.setFirstResult(safePage * safeSize);
		query.setMaxResults(safeSize);
	}
}

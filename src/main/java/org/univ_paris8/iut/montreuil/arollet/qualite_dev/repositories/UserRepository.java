package org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.User;

public class UserRepository {
	private static final int DEFAULT_PAGE_SIZE = 20;

	public User create(EntityManager em, User user) {
		em.persist(user);
		return user;
	}

	public User update(EntityManager em, User user) {
		return em.merge(user);
	}

	public boolean delete(EntityManager em, User user) {
		if (user == null) {
			return false;
		}
		User managed = em.contains(user) ? user : em.merge(user);
		em.remove(managed);
		return true;
	}

	public boolean deleteById(EntityManager em, Long id) {
		if (id == null) {
			return false;
		}
		User managed = em.find(User.class, id);
		if (managed == null) {
			return false;
		}
		em.remove(managed);
		return true;
	}

	public User findById(EntityManager em, Long id) {
		if (id == null) {
			return null;
		}
		return em.find(User.class, id);
	}

	public List<User> findAll(EntityManager em, int page, int size) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> root = cq.from(User.class);
		cq.select(root).orderBy(cb.desc(root.get("id")));
		TypedQuery<User> query = em.createQuery(cq);
		applyPagination(query, page, size);
		return query.getResultList();
	}

	public List<User> searchByKeyword(EntityManager em, String keyword, int page, int size) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return findAll(em, page, size);
		}
		String kw = "%" + keyword.trim().toLowerCase() + "%";
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> root = cq.from(User.class);
		Predicate usernameLike = cb.like(cb.lower(root.get("username")), kw);
		Predicate emailLike = cb.like(cb.lower(root.get("email")), kw);
		cq.select(root)
			.where(cb.or(usernameLike, emailLike))
			.orderBy(cb.desc(root.get("id")));
		TypedQuery<User> query = em.createQuery(cq);
		applyPagination(query, page, size);
		return query.getResultList();
	}

	public User findByLoginAndPassword(EntityManager em, String login, String password) {
		if (login == null || login.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			return null;
		}
		String normalized = login.trim().toLowerCase();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> root = cq.from(User.class);
		Predicate loginMatch = cb.or(
			cb.equal(cb.lower(root.get("username")), normalized),
			cb.equal(cb.lower(root.get("email")), normalized)
		);
		Predicate passwordMatch = cb.equal(root.get("password"), password);
		cq.select(root).where(cb.and(loginMatch, passwordMatch));
		TypedQuery<User> query = em.createQuery(cq);
		List<User> results = query.setMaxResults(1).getResultList();
		return results.isEmpty() ? null : results.get(0);
	}

	private void applyPagination(TypedQuery<?> query, int page, int size) {
		int safePage = Math.max(0, page);
		int safeSize = size > 0 ? size : DEFAULT_PAGE_SIZE;
		query.setFirstResult(safePage * safeSize);
		query.setMaxResults(safeSize);
	}
}

package org.univ_paris8.iut.montreuil.arollet.qualite_dev.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.SessionFactory;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.AnnonceStatus;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.User;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories.AnnonceRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories.AnnonceRepositoryPort;

public class AnnonceService {
	@FunctionalInterface
	public interface EntityManagerProvider {
		EntityManager getEntityManager();
	}

	private final AnnonceRepositoryPort annonceRepository;
	private final EntityManagerProvider entityManagerProvider;

	public AnnonceService() {
		this(new AnnonceRepository(), SessionFactory::getEntityManager);
	}

	public AnnonceService(AnnonceRepositoryPort annonceRepository, EntityManagerProvider entityManagerProvider) {
		if (annonceRepository == null || entityManagerProvider == null) {
			throw new IllegalArgumentException("annonceRepository and entityManagerProvider are required.");
		}
		this.annonceRepository = annonceRepository;
		this.entityManagerProvider = entityManagerProvider;
	}

	public Annonce create(Annonce annonce) {
		if (annonce == null) {
			throw new IllegalArgumentException("annonce is required.");
		}
		EntityManager em = entityManagerProvider.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			if (annonce.getDate() == null) {
				annonce.setDate(new Timestamp(System.currentTimeMillis()));
			}
			if (annonce.getAuthor() != null && annonce.getAuthor().getId() != null) {
				User managedAuthor = em.getReference(User.class, annonce.getAuthor().getId());
				annonce.setAuthor(managedAuthor);
			}
			if (annonce.getCategory() != null && annonce.getCategory().getId() != null) {
				Category managedCategory = em.getReference(Category.class, annonce.getCategory().getId());
				annonce.setCategory(managedCategory);
			}
			Annonce created = annonceRepository.create(em, annonce);
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

	public Annonce update(Annonce annonce) {
		if (annonce == null) {
			throw new IllegalArgumentException("annonce is required.");
		}
		EntityManager em = entityManagerProvider.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Annonce updated = annonceRepository.update(em, annonce);
			tx.commit();
			return updated;
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public boolean publish(Long id) {
		return updateStatus(id, AnnonceStatus.DRAFT, AnnonceStatus.ACTIVE);
	}

	public boolean archive(Long id) {
		return updateStatus(id, AnnonceStatus.ACTIVE, AnnonceStatus.ARCHIVED);
	}

	public boolean delete(Annonce annonce) {
		if (annonce == null) {
			return false;
		}
		EntityManager em = entityManagerProvider.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			boolean deleted = annonceRepository.delete(em, annonce);
			tx.commit();
			return deleted;
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public boolean deleteById(Long id) {
		if (id == null) {
			return false;
		}
		EntityManager em = entityManagerProvider.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			boolean deleted = annonceRepository.deleteById(em, id);
			if (!deleted) {
				tx.rollback();
				return false;
			}
			tx.commit();
			return true;
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public Annonce findById(Long id) {
		EntityManager em = entityManagerProvider.getEntityManager();
		try {
			return annonceRepository.findById(em, id);
		} finally {
			em.close();
		}
	}

	public List<Annonce> list(int page, int size) {
		EntityManager em = entityManagerProvider.getEntityManager();
		try {
			return annonceRepository.findAll(em, page, size);
		} finally {
			em.close();
		}
	}

	public List<Annonce> search(String keyword, int page, int size) {
		EntityManager em = entityManagerProvider.getEntityManager();
		try {
			return annonceRepository.searchByKeyword(em, keyword, page, size);
		} finally {
			em.close();
		}
	}

	public List<Annonce> listByCategoryAndStatus(Long categoryId, AnnonceStatus status, int page, int size) {
		EntityManager em = entityManagerProvider.getEntityManager();
		try {
			return annonceRepository.findByCategoryAndStatus(em, categoryId, status, page, size);
		} finally {
			em.close();
		}
	}

	public long countAll() {
		EntityManager em = entityManagerProvider.getEntityManager();
		try {
			return annonceRepository.countAll(em);
		} finally {
			em.close();
		}
	}

	private boolean updateStatus(Long id, AnnonceStatus expectedStatus, AnnonceStatus targetStatus) {
		if (id == null || expectedStatus == null || targetStatus == null) {
			throw new IllegalArgumentException("id, expectedStatus and targetStatus are required.");
		}
		EntityManager em = entityManagerProvider.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Annonce annonce = annonceRepository.findById(em, id);
			if (annonce == null) {
				throw new NoSuchElementException("Annonce introuvable: id=" + id);
			}
			if (annonce.getStatus() != expectedStatus) {
				throw new IllegalStateException("Transition invalide: " + annonce.getStatus() + " -> " + targetStatus);
			}
			annonce.setStatus(targetStatus);
			tx.commit();
			return true;
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}
}

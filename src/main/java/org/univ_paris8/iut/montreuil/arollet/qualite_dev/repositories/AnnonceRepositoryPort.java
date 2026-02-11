package org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories;

import java.util.List;

import javax.persistence.EntityManager;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.AnnonceStatus;

public interface AnnonceRepositoryPort {
	Annonce create(EntityManager em, Annonce annonce);

	Annonce update(EntityManager em, Annonce annonce);

	boolean delete(EntityManager em, Annonce annonce);

	boolean deleteById(EntityManager em, Long id);

	Annonce findById(EntityManager em, Long id);

	List<Annonce> findAll(EntityManager em, int page, int size);

	List<Annonce> searchByKeyword(EntityManager em, String keyword, int page, int size);

	List<Annonce> findByCategoryAndStatus(EntityManager em, Long categoryId, AnnonceStatus status, int page, int size);

	long countAll(EntityManager em);
}

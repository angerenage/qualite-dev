package org.univ_paris8.iut.montreuil.arollet.qualite_dev.services;

import javax.persistence.EntityManager;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.SessionFactory;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.User;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories.UserRepository;

public class UserService {
	private final UserRepository userRepository = new UserRepository();

	public User authenticate(String login, String password) {
		EntityManager em = SessionFactory.getEntityManager();
		try {
			return userRepository.findByLoginAndPassword(em, login, password);
		} finally {
			em.close();
		}
	}

	public User findById(Long id) {
		EntityManager em = SessionFactory.getEntityManager();
		try {
			return userRepository.findById(em, id);
		} finally {
			em.close();
		}
	}
}

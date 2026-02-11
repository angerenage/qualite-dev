package org.univ_paris8.iut.montreuil.arollet.qualite_dev;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class SessionFactory {
	private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("qualite-dev-pu");

	private SessionFactory() {
	}

	public static EntityManager getEntityManager() {
		return EMF.createEntityManager();
	}

	public static void close() {
		if (EMF.isOpen()) {
			EMF.close();
		}
	}
}

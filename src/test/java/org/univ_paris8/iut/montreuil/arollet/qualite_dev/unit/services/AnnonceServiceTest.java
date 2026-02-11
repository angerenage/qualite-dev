package org.univ_paris8.iut.montreuil.arollet.qualite_dev.unit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.AnnonceStatus;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories.AnnonceRepositoryPort;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.AnnonceService;

@ExtendWith(MockitoExtension.class)
class AnnonceServiceTest {
	@Mock
	private AnnonceRepositoryPort repository;

	@Mock
	private AnnonceService.EntityManagerProvider entityManagerProvider;

	@Mock
	private EntityManager entityManager;

	@Mock
	private EntityTransaction transaction;

	private AnnonceService service;

	@BeforeEach
	void setUp() {
		service = new AnnonceService(repository, entityManagerProvider);
	}

	@Test
	void shouldPublishAnnonceWhenCurrentStatusIsDraft() {
		when(entityManagerProvider.getEntityManager()).thenReturn(entityManager);
		when(entityManager.getTransaction()).thenReturn(transaction);

		Annonce annonce = new Annonce();
		annonce.setStatus(AnnonceStatus.DRAFT);
		when(repository.findById(entityManager, 10L)).thenReturn(annonce);

		boolean result = service.publish(10L);

		assertEquals(true, result);
		assertEquals(AnnonceStatus.ACTIVE, annonce.getStatus());
		verify(transaction).begin();
		verify(repository).findById(entityManager, 10L);
		verify(transaction).commit();
		verify(entityManager).close();
	}

	@Test
	void shouldThrowWhenTransitionIsInvalid() {
		when(entityManagerProvider.getEntityManager()).thenReturn(entityManager);
		when(entityManager.getTransaction()).thenReturn(transaction);
		when(transaction.isActive()).thenReturn(true);

		Annonce annonce = new Annonce();
		annonce.setStatus(AnnonceStatus.ARCHIVED);
		when(repository.findById(entityManager, 11L)).thenReturn(annonce);

		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.publish(11L));

		assertEquals("Transition invalide: ARCHIVED -> ACTIVE", ex.getMessage());
		verify(repository).findById(entityManager, 11L);
		verify(transaction).rollback();
		verify(transaction, never()).commit();
	}

	@Test
	void shouldThrowWhenAnnonceNotFound() {
		when(entityManagerProvider.getEntityManager()).thenReturn(entityManager);
		when(entityManager.getTransaction()).thenReturn(transaction);
		when(transaction.isActive()).thenReturn(true);

		when(repository.findById(entityManager, 99L)).thenReturn(null);

		NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> service.archive(99L));

		assertEquals("Annonce introuvable: id=99", ex.getMessage());
		verify(repository).findById(entityManager, 99L);
		verify(transaction).rollback();
		verify(transaction, never()).commit();
	}

	@Test
	void shouldFailFastWhenIdIsNullWithoutRepositoryCall() {
		assertThrows(IllegalArgumentException.class, () -> service.publish(null));

		verifyNoInteractions(repository);
		verifyNoInteractions(entityManagerProvider);
	}
}

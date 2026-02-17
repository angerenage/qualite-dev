package org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.api;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.SessionFactory;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ApiException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.BusinessConflictException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ForbiddenException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.NotFoundException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.AnnonceStatus;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.User;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories.CategoryRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories.UserRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories.api.AnnonceApiRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.api.commands.AnnonceUpsertCommand;

import jakarta.ws.rs.core.Response;

public class AnnonceApiService {
	private final AnnonceApiRepository annonceRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;

	public AnnonceApiService() {
		this(new AnnonceApiRepository(), new UserRepository(), new CategoryRepository());
	}

	AnnonceApiService(AnnonceApiRepository annonceRepository, UserRepository userRepository,
			CategoryRepository categoryRepository) {
		this.annonceRepository = annonceRepository;
		this.userRepository = userRepository;
		this.categoryRepository = categoryRepository;
	}

	public List<Annonce> list(int page, int size) {
		EntityManager em = SessionFactory.getEntityManager();
		try {
			return annonceRepository.findAll(em, page, size);
		} finally {
			em.close();
		}
	}

	public long countAll() {
		EntityManager em = SessionFactory.getEntityManager();
		try {
			return annonceRepository.countAll(em);
		} finally {
			em.close();
		}
	}

	public Annonce getById(Long id) {
		validateId(id);
		EntityManager em = SessionFactory.getEntityManager();
		try {
			Annonce annonce = annonceRepository.findById(em, id);
			if (annonce == null) {
				throw new NotFoundException("Annonce not found for id=" + id);
			}
			return annonce;
		} finally {
			em.close();
		}
	}

	public Annonce create(AnnonceUpsertCommand command, Long currentUserId) {
		validateCreateCommand(command);
		validateCurrentUserId(currentUserId);
		EntityManager em = SessionFactory.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			User author = userRepository.findById(em, currentUserId);
			if (author == null) {
				throw new ApiException(Response.Status.UNAUTHORIZED, "Authenticated user does not exist.");
			}

			Category category = categoryRepository.findById(em, command.getCategoryId());
			if (category == null) {
				throw new ApiException(Response.Status.BAD_REQUEST, "Unknown categoryId: " + command.getCategoryId());
			}

			Annonce annonce = new Annonce();
			annonce.setTitle(clean(command.getTitle()));
			annonce.setDescription(clean(command.getDescription()));
			annonce.setAdress(clean(command.getAdress()));
			annonce.setMail(clean(command.getMail()));
			annonce.setStatus(resolveStatus(command.getStatus(), AnnonceStatus.DRAFT));
			annonce.setDate(new Timestamp(System.currentTimeMillis()));
			annonce.setAuthor(author);
			annonce.setCategory(category);

			annonceRepository.create(em, annonce);
			tx.commit();
			return annonceRepository.findById(em, annonce.getId());
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			if (isOptimisticLockFailure(e)) {
				throw new BusinessConflictException("Concurrent modification detected.");
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public Annonce updateAnnonce(Long id, AnnonceUpsertCommand command, Long currentUserId) {
		validateId(id);
		validateUpdateCommand(command);
		validateCurrentUserId(currentUserId);
		EntityManager em = SessionFactory.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			Annonce annonce = annonceRepository.findById(em, id);
			if (annonce == null) {
				throw new NotFoundException("Annonce not found for id=" + id);
			}
			enforceOwnership(annonce, currentUserId);
			enforceUpdatableStatus(annonce);
			enforceVersionMatch(annonce, command.getVersion());
			AnnonceStatus requestedStatus = resolveStatus(command.getStatus(), annonce.getStatus());
			if (requestedStatus != annonce.getStatus()) {
				throw new BusinessConflictException(
						"Status cannot be changed via update endpoint. Use publish/archive actions.");
			}

			Category category = categoryRepository.findById(em, command.getCategoryId());
			if (category == null) {
				throw new ApiException(Response.Status.BAD_REQUEST, "Unknown categoryId: " + command.getCategoryId());
			}

			annonce.setTitle(clean(command.getTitle()));
			annonce.setDescription(clean(command.getDescription()));
			annonce.setAdress(clean(command.getAdress()));
			annonce.setMail(clean(command.getMail()));
			annonce.setCategory(category);

			tx.commit();
			return annonceRepository.findById(em, annonce.getId());
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			if (isOptimisticLockFailure(e)) {
				throw new BusinessConflictException("Concurrent modification detected.");
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public void deleteAnnonce(Long id, Long currentUserId) {
		validateId(id);
		validateCurrentUserId(currentUserId);
		EntityManager em = SessionFactory.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Annonce annonce = annonceRepository.findById(em, id);
			if (annonce == null) {
				throw new NotFoundException("Annonce not found for id=" + id);
			}
			enforceOwnership(annonce, currentUserId);
			if (annonce.getStatus() != AnnonceStatus.ARCHIVED) {
				throw new BusinessConflictException("Deletion is allowed only for ARCHIVED annonces.");
			}
			boolean deleted = annonceRepository.deleteById(em, id);
			if (!deleted) {
				throw new NotFoundException("Annonce not found for id=" + id);
			}
			tx.commit();
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			if (isOptimisticLockFailure(e)) {
				throw new BusinessConflictException("Concurrent modification detected.");
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public Annonce publishAnnonce(Long id, Long currentUserId) {
		validateId(id);
		validateCurrentUserId(currentUserId);
		EntityManager em = SessionFactory.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Annonce annonce = annonceRepository.findById(em, id);
			if (annonce == null) {
				throw new NotFoundException("Annonce not found for id=" + id);
			}
			enforceOwnership(annonce, currentUserId);
			if (annonce.getStatus() == AnnonceStatus.PUBLISHED) {
				throw new BusinessConflictException("Annonce is already PUBLISHED.");
			}
			if (annonce.getStatus() == AnnonceStatus.ARCHIVED) {
				throw new BusinessConflictException("ARCHIVED annonce cannot be published.");
			}

			annonce.setStatus(AnnonceStatus.PUBLISHED);
			tx.commit();
			return annonceRepository.findById(em, annonce.getId());
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			if (isOptimisticLockFailure(e)) {
				throw new BusinessConflictException("Concurrent modification detected.");
			}
			throw e;
		} finally {
			em.close();
		}
	}

	public Annonce archiveAnnonce(Long id, Long currentUserId) {
		validateId(id);
		validateCurrentUserId(currentUserId);
		EntityManager em = SessionFactory.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Annonce annonce = annonceRepository.findById(em, id);
			if (annonce == null) {
				throw new NotFoundException("Annonce not found for id=" + id);
			}
			enforceOwnership(annonce, currentUserId);
			if (annonce.getStatus() == AnnonceStatus.ARCHIVED) {
				throw new BusinessConflictException("Annonce is already ARCHIVED.");
			}

			annonce.setStatus(AnnonceStatus.ARCHIVED);
			tx.commit();
			return annonceRepository.findById(em, annonce.getId());
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			if (isOptimisticLockFailure(e)) {
				throw new BusinessConflictException("Concurrent modification detected.");
			}
			throw e;
		} finally {
			em.close();
		}
	}

	private void validateId(Long id) {
		if (id == null || id <= 0) {
			throw new ApiException(Response.Status.BAD_REQUEST, "id must be a positive number.");
		}
	}

	private void validateCreateCommand(AnnonceUpsertCommand command) {
		if (command == null) {
			throw new ApiException(Response.Status.BAD_REQUEST, "Request body is required.");
		}
		validateSharedCommandFields(command);
		AnnonceStatus status = resolveStatus(command.getStatus(), AnnonceStatus.DRAFT);
		if (status != AnnonceStatus.DRAFT) {
			throw new BusinessConflictException("A new annonce must be created with DRAFT status.");
		}
	}

	private void validateUpdateCommand(AnnonceUpsertCommand command) {
		if (command == null) {
			throw new ApiException(Response.Status.BAD_REQUEST, "Request body is required.");
		}
		validateSharedCommandFields(command);
		if (command.getCategoryId() == null || command.getCategoryId() <= 0) {
			throw new ApiException(Response.Status.BAD_REQUEST, "categoryId must be a positive number.");
		}
		if (command.getVersion() == null || command.getVersion() < 0) {
			throw new ApiException(Response.Status.BAD_REQUEST, "version must be >= 0.");
		}
	}

	private AnnonceStatus resolveStatus(String rawStatus, AnnonceStatus fallback) {
		String cleanStatus = clean(rawStatus);
		if (cleanStatus == null || cleanStatus.isEmpty()) {
			return fallback;
		}
		try {
			return AnnonceStatus.valueOf(cleanStatus.toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new ApiException(Response.Status.BAD_REQUEST,
					"status must be one of DRAFT, PUBLISHED, ARCHIVED.");
		}
	}

	private void validateSharedCommandFields(AnnonceUpsertCommand command) {
		requireText(command.getTitle(), "title");
		requireText(command.getDescription(), "description");
		requireText(command.getAdress(), "adress");
		requireText(command.getMail(), "mail");

		if (!clean(command.getMail()).contains("@")) {
			throw new ApiException(Response.Status.BAD_REQUEST, "mail must be a valid email address.");
		}
		if (command.getCategoryId() == null || command.getCategoryId() <= 0) {
			throw new ApiException(Response.Status.BAD_REQUEST, "categoryId must be a positive number.");
		}
	}

	private void validateCurrentUserId(Long currentUserId) {
		if (currentUserId == null || currentUserId <= 0) {
			throw new ApiException(Response.Status.UNAUTHORIZED, "Authentication is required.");
		}
	}

	private void enforceOwnership(Annonce annonce, Long currentUserId) {
		if (annonce.getAuthor() == null || annonce.getAuthor().getId() == null
				|| !annonce.getAuthor().getId().equals(currentUserId)) {
			throw new ForbiddenException("Only the author can modify this annonce.");
		}
	}

	private void enforceUpdatableStatus(Annonce annonce) {
		if (annonce.getStatus() == AnnonceStatus.PUBLISHED) {
			throw new BusinessConflictException("A PUBLISHED annonce cannot be modified.");
		}
		if (annonce.getStatus() == AnnonceStatus.ARCHIVED) {
			throw new BusinessConflictException("An ARCHIVED annonce cannot be modified.");
		}
	}

	private void enforceVersionMatch(Annonce annonce, Long expectedVersion) {
		if (!Objects.equals(annonce.getVersion(), expectedVersion)) {
			throw new BusinessConflictException("Version mismatch. Reload the annonce before updating.");
		}
	}

	private String requireText(String value, String field) {
		String cleaned = clean(value);
		if (cleaned == null || cleaned.isEmpty()) {
			throw new ApiException(Response.Status.BAD_REQUEST, field + " is required.");
		}
		return cleaned;
	}

	private boolean isOptimisticLockFailure(RuntimeException e) {
		if (e instanceof OptimisticLockException) {
			return true;
		}
		Throwable cause = e;
		while (cause != null) {
			if (cause instanceof OptimisticLockException) {
				return true;
			}
			if (cause instanceof RollbackException && cause.getCause() instanceof OptimisticLockException) {
				return true;
			}
			if (cause.getClass().getName().contains("StaleObjectStateException")) {
				return true;
			}
			cause = cause.getCause();
		}
		return false;
	}

	private String clean(String value) {
		return value == null ? null : value.trim();
	}
}

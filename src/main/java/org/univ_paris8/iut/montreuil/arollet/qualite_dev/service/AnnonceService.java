package org.univ_paris8.iut.montreuil.arollet.qualite_dev.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.User;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.repository.AnnonceRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.repository.UserRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.AppUserPrincipal;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.service.spec.AnnonceSpecifications;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.CreateAnnonceRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.PatchAnnonceRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.UpdateAnnonceRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.error.ApiException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.mapper.AnnonceMapper;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

@Service
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AnnonceMapper annonceMapper;

    public AnnonceService(
        AnnonceRepository annonceRepository,
        CategoryRepository categoryRepository,
        UserRepository userRepository,
        AnnonceMapper annonceMapper
    ) {
        this.annonceRepository = annonceRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.annonceMapper = annonceMapper;
    }

    @Transactional(readOnly = true)
    public Page<Annonce> list(
        String q,
        String rawStatus,
        Long categoryId,
        Long authorId,
        LocalDate fromDate,
        LocalDate toDate,
        Pageable pageable
    ) {
        AnnonceStatus status = parseStatus(rawStatus, null);
        Specification<Annonce> spec = AnnonceSpecifications.build(q, status, categoryId, authorId, fromDate, toDate);
        return annonceRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Annonce detail(Long id) {
        return annonceRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Annonce not found for id=" + id));
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Annonce create(CreateAnnonceRequestDto request) {
        User currentUser = currentUser();
        Category category = categoryOf(request.getCategoryId());

        AnnonceStatus status = parseStatus(request.getStatus(), AnnonceStatus.DRAFT);
        if (status != AnnonceStatus.DRAFT) {
            throw new ApiException(HttpStatus.CONFLICT, "A new annonce must be created with DRAFT status.");
        }

        Annonce annonce = new Annonce();
        annonceMapper.updateEntityFromCreate(request, annonce);
        annonce.setStatus(status);
        annonce.setDate(new Timestamp(System.currentTimeMillis()));
        annonce.setAuthor(currentUser);
        annonce.setCategory(category);
        return annonceRepository.save(annonce);
    }

    @Transactional
    @PreAuthorize("@annonceAuthorization.isAuthor(#id, authentication)")
    public Annonce update(Long id, UpdateAnnonceRequestDto request) {
        Annonce annonce = detail(id);
        ensureAuthor(annonce);
        ensureUpdatable(annonce);
        ensureVersion(annonce, request.getVersion());

        AnnonceStatus requestedStatus = parseStatus(request.getStatus(), null);
        if (requestedStatus != annonce.getStatus()) {
            throw new ApiException(HttpStatus.CONFLICT, "Status cannot be changed via update endpoint. Use publish/archive actions.");
        }

        annonceMapper.updateEntityFromUpdate(request, annonce);
        annonce.setCategory(categoryOf(request.getCategoryId()));
        return annonceRepository.save(annonce);
    }

    @Transactional
    @PreAuthorize("@annonceAuthorization.isAuthor(#id, authentication)")
    public Annonce patch(Long id, PatchAnnonceRequestDto request) {
        Annonce annonce = detail(id);
        ensureAuthor(annonce);
        ensureUpdatable(annonce);
        if (request.getVersion() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "version is required.");
        }
        ensureVersion(annonce, request.getVersion());

        if (request.getStatus() != null) {
            AnnonceStatus requestedStatus = parseStatus(request.getStatus(), annonce.getStatus());
            if (requestedStatus != annonce.getStatus()) {
                throw new ApiException(HttpStatus.CONFLICT, "Status cannot be changed via patch endpoint. Use publish/archive actions.");
            }
        }

        if (request.getCategoryId() != null) {
            annonce.setCategory(categoryOf(request.getCategoryId()));
        }
        annonceMapper.updateEntityFromPatch(request, annonce);
        return annonceRepository.save(annonce);
    }

    @Transactional
    @PreAuthorize("@annonceAuthorization.isAuthor(#id, authentication)")
    public void delete(Long id) {
        Annonce annonce = detail(id);
        ensureAuthor(annonce);
        if (annonce.getStatus() != AnnonceStatus.ARCHIVED) {
            throw new ApiException(HttpStatus.CONFLICT, "Deletion is allowed only for ARCHIVED annonces.");
        }
        annonceRepository.delete(annonce);
    }

    @Transactional
    @PreAuthorize("@annonceAuthorization.isAuthor(#id, authentication)")
    public Annonce publish(Long id) {
        Annonce annonce = detail(id);
        ensureAuthor(annonce);
        if (annonce.getStatus() == AnnonceStatus.PUBLISHED) {
            throw new ApiException(HttpStatus.CONFLICT, "Annonce is already PUBLISHED.");
        }
        if (annonce.getStatus() == AnnonceStatus.ARCHIVED) {
            throw new ApiException(HttpStatus.CONFLICT, "ARCHIVED annonce cannot be published.");
        }
        annonce.setStatus(AnnonceStatus.PUBLISHED);
        return annonceRepository.save(annonce);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Annonce archive(Long id) {
        Annonce annonce = detail(id);
        if (annonce.getStatus() == AnnonceStatus.ARCHIVED) {
            throw new ApiException(HttpStatus.CONFLICT, "Annonce is already ARCHIVED.");
        }
        annonce.setStatus(AnnonceStatus.ARCHIVED);
        return annonceRepository.save(annonce);
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserPrincipal principal)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Authentication is required.");
        }
        return userRepository.findById(principal.getUserId())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Authenticated user does not exist."));
    }

    private void ensureAuthor(Annonce annonce) {
        if (!Objects.equals(annonce.getAuthor().getId(), currentUser().getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only the author can modify this annonce.");
        }
    }

    private void ensureUpdatable(Annonce annonce) {
        if (annonce.getStatus() == AnnonceStatus.PUBLISHED) {
            throw new ApiException(HttpStatus.CONFLICT, "A PUBLISHED annonce cannot be modified.");
        }
        if (annonce.getStatus() == AnnonceStatus.ARCHIVED) {
            throw new ApiException(HttpStatus.CONFLICT, "An ARCHIVED annonce cannot be modified.");
        }
    }

    private void ensureVersion(Annonce annonce, Long expectedVersion) {
        if (!Objects.equals(annonce.getVersion(), expectedVersion)) {
            throw new ApiException(HttpStatus.CONFLICT, "Version mismatch. Reload the annonce before updating.");
        }
    }

    private Category categoryOf(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "categoryId must be a positive number.");
        }
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Unknown categoryId: " + categoryId));
    }

    private AnnonceStatus parseStatus(String rawStatus, AnnonceStatus fallback) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return fallback;
        }
        try {
            return AnnonceStatus.valueOf(rawStatus.trim());
        } catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "status must be one of DRAFT, PUBLISHED, ARCHIVED.");
        }
    }

}


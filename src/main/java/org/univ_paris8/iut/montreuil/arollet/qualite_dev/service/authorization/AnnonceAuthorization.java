package org.univ_paris8.iut.montreuil.arollet.qualite_dev.service.authorization;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.repository.AnnonceRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.AppUserPrincipal;

@Component("annonceAuthorization")
public class AnnonceAuthorization {

    private final AnnonceRepository annonceRepository;

    public AnnonceAuthorization(AnnonceRepository annonceRepository) {
        this.annonceRepository = annonceRepository;
    }

    public boolean isAuthor(Long annonceId, Authentication authentication) {
        if (annonceId == null || authentication == null || !(authentication.getPrincipal() instanceof AppUserPrincipal principal)) {
            return false;
        }
        return annonceRepository.findById(annonceId)
            .map(Annonce::getAuthor)
            .map(author -> author.getId() != null && author.getId().equals(principal.getUserId()))
            .orElse(false);
    }
}

package org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Annonce;

import java.util.Optional;

public interface AnnonceRepository extends JpaRepository<Annonce, Long>, JpaSpecificationExecutor<Annonce> {

    @Override
    @EntityGraph(attributePaths = {"author", "category"})
    Optional<Annonce> findById(Long id);
}

package org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByLabelIgnoreCase(String label);

    Page<Category> findByLabelContainingIgnoreCase(String keyword, Pageable pageable);
}

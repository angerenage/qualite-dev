package org.univ_paris8.iut.montreuil.arollet.qualite_dev.service.spec;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.AnnonceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class AnnonceSpecifications {

    private AnnonceSpecifications() {
    }

    public static Specification<Annonce> build(
        String q,
        AnnonceStatus status,
        Long categoryId,
        Long authorId,
        LocalDate fromDate,
        LocalDate toDate
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (q != null && !q.isBlank()) {
                String keyword = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), keyword),
                    cb.like(cb.lower(root.get("description")), keyword)
                ));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (authorId != null) {
                predicates.add(cb.equal(root.get("author").get("id"), authorId));
            }
            if (fromDate != null) {
                LocalDateTime start = fromDate.atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get("date").as(LocalDateTime.class), start));
            }
            if (toDate != null) {
                LocalDateTime end = toDate.plusDays(1).atStartOfDay().minusNanos(1);
                predicates.add(cb.lessThanOrEqualTo(root.get("date").as(LocalDateTime.class), end));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}

package org.univ_paris8.iut.montreuil.arollet.qualite_dev.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.AnnonceMetaResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.error.ApiException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class MetaService {

    private static final Set<String> EXCLUDED_SORT_FIELDS = Set.of("author", "category");

    public AnnonceMetaResponseDto annonceMeta() {
        List<String> sortable = sortableFields();
        List<String> filterable = List.of("q", "status", "categoryId", "authorId", "fromDate", "toDate");
        return new AnnonceMetaResponseDto(filterable, sortable);
    }

    public String validateAndExtractSortProperty(String sort) {
        if (sort == null || sort.isBlank()) {
            return "id";
        }
        String property = sort.split(",")[0].trim();
        if (!sortableFields().contains(property)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid sort field: " + property);
        }
        return property;
    }

    private List<String> sortableFields() {
        return Arrays.stream(Annonce.class.getDeclaredFields())
            .map(Field::getName)
            .filter(name -> !EXCLUDED_SORT_FIELDS.contains(name))
            .toList();
    }
}

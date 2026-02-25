package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto;

import java.util.List;

public class AnnonceMetaResponseDto {

    private List<String> filterableFields;
    private List<String> sortableFields;

    public AnnonceMetaResponseDto() {
    }

    public AnnonceMetaResponseDto(List<String> filterableFields, List<String> sortableFields) {
        this.filterableFields = filterableFields;
        this.sortableFields = sortableFields;
    }

    public List<String> getFilterableFields() {
        return filterableFields;
    }

    public void setFilterableFields(List<String> filterableFields) {
        this.filterableFields = filterableFields;
    }

    public List<String> getSortableFields() {
        return sortableFields;
    }

    public void setSortableFields(List<String> sortableFields) {
        this.sortableFields = sortableFields;
    }
}

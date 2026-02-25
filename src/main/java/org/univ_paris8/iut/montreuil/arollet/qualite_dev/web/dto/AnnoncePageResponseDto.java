package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto;

import java.util.List;

public class AnnoncePageResponseDto {

    private List<AnnonceResponseDto> items;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;

    public AnnoncePageResponseDto() {
    }

    public AnnoncePageResponseDto(List<AnnonceResponseDto> items, int page, int size, long totalItems, int totalPages) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    public List<AnnonceResponseDto> getItems() {
        return items;
    }

    public void setItems(List<AnnonceResponseDto> items) {
        this.items = items;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}

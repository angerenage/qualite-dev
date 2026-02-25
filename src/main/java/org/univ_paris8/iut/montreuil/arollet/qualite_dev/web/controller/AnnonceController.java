package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.service.AnnonceService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.service.MetaService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.AnnoncePageResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.AnnonceResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.CreateAnnonceRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.PatchAnnonceRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.UpdateAnnonceRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.mapper.AnnonceMapper;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/annonces")
public class AnnonceController {

    private final AnnonceService annonceService;
    private final AnnonceMapper annonceMapper;
    private final MetaService metaService;

    public AnnonceController(AnnonceService annonceService, AnnonceMapper annonceMapper, MetaService metaService) {
        this.annonceService = annonceService;
        this.annonceMapper = annonceMapper;
        this.metaService = metaService;
    }

    @GetMapping
    public AnnoncePageResponseDto list(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) @Positive Long categoryId,
        @RequestParam(required = false) @Positive Long authorId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(defaultValue = "id,desc") String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        Page<Annonce> annoncePage = annonceService.list(q, status, categoryId, authorId, fromDate, toDate, pageable);
        List<AnnonceResponseDto> items = annoncePage.getContent().stream().map(annonceMapper::toResponse).toList();
        return new AnnoncePageResponseDto(items, annoncePage.getNumber(), annoncePage.getSize(), annoncePage.getTotalElements(), annoncePage.getTotalPages());
    }

    @GetMapping("/{id}")
    public AnnonceResponseDto detail(@PathVariable @Positive Long id) {
        return annonceMapper.toResponse(annonceService.detail(id));
    }

    @PostMapping
    public ResponseEntity<AnnonceResponseDto> create(@Valid @RequestBody CreateAnnonceRequestDto request) {
        Annonce created = annonceService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(annonceMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public AnnonceResponseDto update(@PathVariable @Positive Long id, @Valid @RequestBody UpdateAnnonceRequestDto request) {
        return annonceMapper.toResponse(annonceService.update(id, request));
    }

    @PatchMapping("/{id}")
    public AnnonceResponseDto patch(@PathVariable @Positive Long id, @Valid @RequestBody PatchAnnonceRequestDto request) {
        return annonceMapper.toResponse(annonceService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        annonceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    public AnnonceResponseDto publish(@PathVariable @Positive Long id) {
        return annonceMapper.toResponse(annonceService.publish(id));
    }

    @PostMapping("/{id}/archive")
    public AnnonceResponseDto archive(@PathVariable @Positive Long id) {
        return annonceMapper.toResponse(annonceService.archive(id));
    }

    private Pageable buildPageable(int page, int size, String sort) {
        String[] chunks = sort.split(",", 2);
        String property = metaService.validateAndExtractSortProperty(sort);
        Sort.Direction direction = chunks.length > 1 && "asc".equalsIgnoreCase(chunks[1].trim())
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}

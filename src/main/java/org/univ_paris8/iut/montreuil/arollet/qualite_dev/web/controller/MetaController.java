package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.service.MetaService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.AnnonceMetaResponseDto;

@RestController
@RequestMapping("/api/meta")
public class MetaController {

    private final MetaService metaService;

    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    @GetMapping("/annonces")
    public AnnonceMetaResponseDto annonceMeta() {
        return metaService.annonceMeta();
    }
}

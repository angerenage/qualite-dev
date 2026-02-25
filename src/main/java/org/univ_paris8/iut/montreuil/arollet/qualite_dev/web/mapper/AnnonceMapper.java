package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.AnnonceResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.CreateAnnonceRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.PatchAnnonceRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.UpdateAnnonceRequestDto;

@Mapper(componentModel = "spring")
public interface AnnonceMapper {

    @Mapping(target = "date", expression = "java(annonce.getDate() == null ? null : annonce.getDate().toInstant().toString())")
    @Mapping(target = "status", expression = "java(annonce.getStatus() == null ? null : annonce.getStatus().name())")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorUsername", source = "author.username")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryLabel", source = "category.label")
    AnnonceResponseDto toResponse(Annonce annonce);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromCreate(CreateAnnonceRequestDto source, @MappingTarget Annonce target);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromUpdate(UpdateAnnonceRequestDto source, @MappingTarget Annonce target);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromPatch(PatchAnnonceRequestDto source, @MappingTarget Annonce target);
}

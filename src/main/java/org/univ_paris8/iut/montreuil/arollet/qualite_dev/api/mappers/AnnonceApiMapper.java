package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.mappers;

import java.util.ArrayList;
import java.util.List;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.AnnoncePageResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.AnnonceResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.CreateAnnonceRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.UpdateAnnonceRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.api.commands.AnnonceUpsertCommand;

public class AnnonceApiMapper {
	public AnnonceUpsertCommand toCommand(CreateAnnonceRequestDto dto) {
		if (dto == null) {
			return null;
		}
		AnnonceUpsertCommand command = new AnnonceUpsertCommand();
		command.setTitle(dto.getTitle());
		command.setDescription(dto.getDescription());
		command.setAdress(dto.getAdress());
		command.setMail(dto.getMail());
		command.setStatus(dto.getStatus());
		command.setCategoryId(dto.getCategoryId());
		return command;
	}

	public AnnonceUpsertCommand toCommand(UpdateAnnonceRequestDto dto) {
		if (dto == null) {
			return null;
		}
		AnnonceUpsertCommand command = new AnnonceUpsertCommand();
		command.setTitle(dto.getTitle());
		command.setDescription(dto.getDescription());
		command.setAdress(dto.getAdress());
		command.setMail(dto.getMail());
		command.setStatus(dto.getStatus());
		command.setCategoryId(dto.getCategoryId());
		command.setVersion(dto.getVersion());
		return command;
	}

	public AnnonceResponseDto toResponse(Annonce annonce) {
		if (annonce == null) {
			return null;
		}
		AnnonceResponseDto dto = new AnnonceResponseDto();
		dto.setId(annonce.getId());
		dto.setTitle(annonce.getTitle());
		dto.setDescription(annonce.getDescription());
		dto.setAdress(annonce.getAdress());
		dto.setMail(annonce.getMail());
		dto.setDate(annonce.getDate() == null ? null : annonce.getDate().toInstant().toString());
		dto.setStatus(annonce.getStatus() == null ? null : annonce.getStatus().name());
		dto.setVersion(annonce.getVersion());

		if (annonce.getAuthor() != null) {
			dto.setAuthorId(annonce.getAuthor().getId());
			dto.setAuthorUsername(annonce.getAuthor().getUsername());
		}
		if (annonce.getCategory() != null) {
			dto.setCategoryId(annonce.getCategory().getId());
			dto.setCategoryLabel(annonce.getCategory().getLabel());
		}

		return dto;
	}

	public AnnoncePageResponseDto toPageResponse(List<Annonce> annonces, int page, int size, long totalItems) {
		List<AnnonceResponseDto> items = new ArrayList<>();
		if (annonces != null) {
			for (Annonce annonce : annonces) {
				items.add(toResponse(annonce));
			}
		}
		int totalPages = size > 0 ? (int) Math.ceil(totalItems / (double) size) : 0;
		return new AnnoncePageResponseDto(items, page, size, totalItems, totalPages);
	}
}

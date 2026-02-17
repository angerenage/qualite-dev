package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AnnonceResponse", description = "Annonce details exposed by REST API")
public class AnnonceResponseDto {
	@Schema(example = "1")
	private Long id;
	@Schema(example = "Annonce 1")
	private String title;
	@Schema(example = "Desc 1")
	private String description;
	@Schema(example = "Paris 1")
	private String adress;
	@Schema(example = "a1@example.com")
	private String mail;
	@Schema(example = "2025-01-01T10:00:00Z")
	private String date;
	@Schema(example = "DRAFT")
	private String status;
	@Schema(example = "0")
	private Long version;
	@Schema(example = "1")
	private Long authorId;
	@Schema(example = "alice")
	private String authorUsername;
	@Schema(example = "1")
	private Long categoryId;
	@Schema(example = "Housing")
	private String categoryLabel;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public String getAuthorUsername() {
		return authorUsername;
	}

	public void setAuthorUsername(String authorUsername) {
		this.authorUsername = authorUsername;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryLabel() {
		return categoryLabel;
	}

	public void setCategoryLabel(String categoryLabel) {
		this.categoryLabel = categoryLabel;
	}
}

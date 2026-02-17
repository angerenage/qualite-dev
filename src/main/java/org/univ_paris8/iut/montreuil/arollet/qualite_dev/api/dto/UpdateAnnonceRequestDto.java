package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UpdateAnnonceRequest", description = "Payload to update an existing annonce")
public class UpdateAnnonceRequestDto {
	@Schema(example = "Studio meublé - disponibilité immédiate")
	@NotBlank(message = "title is required.")
	@Size(max = 64, message = "title must contain at most 64 characters.")
	private String title;

	@Schema(example = "Studio 25m2 rénové, disponible immédiatement.")
	@NotBlank(message = "description is required.")
	@Size(max = 256, message = "description must contain at most 256 characters.")
	private String description;

	@Schema(example = "Montreuil")
	@NotBlank(message = "adress is required.")
	@Size(max = 64, message = "adress must contain at most 64 characters.")
	private String adress;

	@Schema(example = "owner@example.com")
	@NotBlank(message = "mail is required.")
	@Email(message = "mail must be a valid email address.")
	@Size(max = 64, message = "mail must contain at most 64 characters.")
	private String mail;

	@Schema(example = "DRAFT")
	@Pattern(regexp = "^(?i)(DRAFT|PUBLISHED|ARCHIVED)$", message = "status must be one of DRAFT, PUBLISHED, ARCHIVED.")
	private String status;

	@Schema(example = "1")
	@NotNull(message = "categoryId is required.")
	@Positive(message = "categoryId must be a positive number.")
	private Long categoryId;

	@Schema(example = "0")
	@NotNull(message = "version is required.")
	@PositiveOrZero(message = "version must be >= 0.")
	private Long version;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}

package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CreateAnnonceRequestDto {

    @NotBlank(message = "title is required.")
    @Size(max = 64, message = "title must contain at most 64 characters.")
    private String title;

    @NotBlank(message = "description is required.")
    @Size(max = 256, message = "description must contain at most 256 characters.")
    private String description;

    @NotBlank(message = "address is required.")
    @Size(max = 64, message = "address must contain at most 64 characters.")
    private String address;

    @NotBlank(message = "mail is required.")
    @Email(message = "mail must be a valid email address.")
    @Size(max = 64, message = "mail must contain at most 64 characters.")
    private String mail;

    @Pattern(regexp = "^(DRAFT|PUBLISHED|ARCHIVED)$", message = "status must be one of DRAFT, PUBLISHED, ARCHIVED.")
    private String status;

    @NotNull(message = "categoryId is required.")
    @Positive(message = "categoryId must be a positive number.")
    private Long categoryId;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}


package org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.Timestamp;

@Entity
@Table(name = "annonce")
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 64)
    @Column(nullable = false, length = 64)
    private String title;

    @NotBlank
    @Size(max = 256)
    @Column(nullable = false, length = 256)
    private String description;

    @NotBlank
    @Size(max = 64)
    @Column(nullable = false, length = 64)
    private String address;

    @NotBlank
    @Email
    @Size(max = 64)
    @Column(nullable = false, length = 64)
    private String mail;

    @NotNull
    @Column(nullable = false)
    private Timestamp date;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AnnonceStatus status = AnnonceStatus.DRAFT;

    @Version
    @Column(nullable = false)
    private Long version;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public AnnonceStatus getStatus() {
        return status;
    }

    public void setStatus(AnnonceStatus status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}


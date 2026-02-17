package org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
	private String adress;

	@NotBlank
	@Email
	@Size(max = 64)
	@Column(nullable = false, length = 64)
	private String mail;

	@NotNull
	@Column(nullable = false, updatable = false)
	private java.sql.Timestamp date;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
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

	public Annonce() {

	}

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

	public java.sql.Timestamp getDate() {
		return date;
	}

	public void setDate(java.sql.Timestamp date) {
		this.date = date;
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

	public AnnonceStatus getStatus() {
		return status;
	}

	public void setStatus(AnnonceStatus status) {
		this.status = status;
	}

	public Long getVersion() {
		return version;
	}
}

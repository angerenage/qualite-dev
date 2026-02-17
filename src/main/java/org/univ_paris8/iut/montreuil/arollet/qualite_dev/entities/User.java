package org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false, unique = true)
	private String username;

	@NotBlank
	@Email
	@Column(nullable = false, unique = true)
	private String email;

	@NotBlank
	@Column(name = "password_hash", nullable = false, length = 512)
	private String passwordHash;

	@Column(nullable = false, length = 128)
	private String roles = "USER";

	@Column(name = "created_at", nullable = false)
	private java.sql.Timestamp createdAt;

	public User() {

	}

	public User(String username, String email, String passwordHash) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public java.sql.Timestamp getCreatedAt() {
		return createdAt;
	}
}

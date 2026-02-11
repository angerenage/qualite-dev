package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web;

import java.io.Serializable;

public class SessionUser implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Long id;
	private final String username;

	public SessionUser(Long id, String username) {
		this.id = id;
		this.username = username;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}
}

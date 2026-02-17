package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions;

import jakarta.ws.rs.core.Response;

public class NotFoundException extends ApiException {
	private static final long serialVersionUID = 1L;

	public NotFoundException(String message) {
		super(Response.Status.NOT_FOUND, message);
	}
}

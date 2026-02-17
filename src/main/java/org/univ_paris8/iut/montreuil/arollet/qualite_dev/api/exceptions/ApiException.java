package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions;

import jakarta.ws.rs.core.Response;

public class ApiException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final Response.Status status;

	public ApiException(Response.Status status, String message) {
		super(message);
		this.status = status;
	}

	public Response.Status getStatus() {
		return status;
	}
}

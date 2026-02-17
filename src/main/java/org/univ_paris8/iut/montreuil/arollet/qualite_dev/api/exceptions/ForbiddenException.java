package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions;

import jakarta.ws.rs.core.Response;

public class ForbiddenException extends ApiException {
	private static final long serialVersionUID = 1L;

	public ForbiddenException(String message) {
		super(Response.Status.FORBIDDEN, message);
	}
}

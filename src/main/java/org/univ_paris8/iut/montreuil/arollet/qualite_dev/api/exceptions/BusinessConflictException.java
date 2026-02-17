package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions;

import jakarta.ws.rs.core.Response;

public class BusinessConflictException extends ApiException {
	private static final long serialVersionUID = 1L;

	public BusinessConflictException(String message) {
		super(Response.Status.CONFLICT, message);
	}
}

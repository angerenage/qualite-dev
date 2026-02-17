package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {
	@Override
	public Response toResponse(ApiException exception) {
		Response.Status status = exception.getStatus();
		String message = exception.getMessage() == null || exception.getMessage().trim().isEmpty() ? "Request failed."
				: exception.getMessage();
		ErrorResponseDto error = ErrorResponseFactory.of(status, message);
		return Response.status(status).type(MediaType.APPLICATION_JSON).entity(error).build();
	}
}

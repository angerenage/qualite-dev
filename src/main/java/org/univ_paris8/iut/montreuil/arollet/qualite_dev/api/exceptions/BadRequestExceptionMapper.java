package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
	@Override
	public Response toResponse(BadRequestException exception) {
		String message = "Invalid request.";
		if (exception != null && exception.getMessage() != null
				&& exception.getMessage().toLowerCase().contains("json")) {
			message = "Malformed JSON request body.";
		}

		ErrorResponseDto error = ErrorResponseFactory.of(Response.Status.BAD_REQUEST, message);
		return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(error).build();
	}
}

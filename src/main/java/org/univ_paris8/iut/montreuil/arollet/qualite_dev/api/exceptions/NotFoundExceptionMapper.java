package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
	@Override
	public Response toResponse(NotFoundException exception) {
		ErrorResponseDto error = ErrorResponseFactory.of(Response.Status.NOT_FOUND, "Resource not found.");
		return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(error).build();
	}
}

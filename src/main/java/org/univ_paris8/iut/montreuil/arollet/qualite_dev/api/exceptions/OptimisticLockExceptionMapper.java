package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions;

import javax.persistence.OptimisticLockException;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class OptimisticLockExceptionMapper implements ExceptionMapper<OptimisticLockException> {
	@Override
	public Response toResponse(OptimisticLockException exception) {
		ErrorResponseDto error = ErrorResponseFactory.of(Response.Status.CONFLICT, "Concurrent modification detected.");
		return Response.status(Response.Status.CONFLICT).type(MediaType.APPLICATION_JSON).entity(error).build();
	}
}

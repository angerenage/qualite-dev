package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericExceptionMapper.class);

	@Override
	public Response toResponse(Exception exception) {
		LOGGER.error("Unhandled API exception: {}", exception == null ? "unknown" : exception.getClass().getSimpleName());
		ErrorResponseDto error = ErrorResponseFactory.of(Response.Status.INTERNAL_SERVER_ERROR, "Unexpected server error.");
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(error)
				.build();
	}
}

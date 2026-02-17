package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
	@Override
	public Response toResponse(ConstraintViolationException exception) {
		List<String> messages = buildMessages(exception);
		ErrorResponseDto error = ErrorResponseFactory.of(Response.Status.BAD_REQUEST, messages);
		return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(error).build();
	}

	private List<String> buildMessages(ConstraintViolationException exception) {
		if (exception == null || exception.getConstraintViolations() == null
				|| exception.getConstraintViolations().isEmpty()) {
			return List.of("Request validation failed.");
		}
		return exception.getConstraintViolations().stream().sorted(Comparator.comparing(v -> v.getPropertyPath().toString()))
				.map(v -> v.getMessage()).collect(Collectors.toList());
	}
}

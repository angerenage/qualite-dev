package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ErrorResponseFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.ENTITY_CODER - 100)
public class JsonSyntaxValidationFilter implements ContainerRequestFilter {
	private static final Set<String> METHODS_WITH_BODY = Set.of("POST", "PUT", "PATCH");
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		if (!METHODS_WITH_BODY.contains(requestContext.getMethod())) {
			return;
		}
		if (requestContext.getMediaType() == null
				|| !requestContext.getMediaType().isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
			return;
		}
		if (!requestContext.hasEntity()) {
			return;
		}

		byte[] body = readAllBytes(requestContext.getEntityStream());
		requestContext.setEntityStream(new ByteArrayInputStream(body));
		if (body.length == 0) {
			return;
		}

		try {
			OBJECT_MAPPER.readTree(body);
		} catch (JsonProcessingException ex) {
			ErrorResponseDto error = ErrorResponseFactory.of(Response.Status.BAD_REQUEST, "Malformed JSON request body.");
			Response response = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
					.entity(error).build();
			requestContext.abortWith(response);
			requestContext.setEntityStream(new ByteArrayInputStream(new byte[0]));
		}
	}

	private byte[] readAllBytes(InputStream stream) throws IOException {
		return stream.readAllBytes();
	}
}

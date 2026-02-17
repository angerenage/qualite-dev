package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.validation.ValidationFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ApiExceptionMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.BadRequestExceptionMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ConstraintViolationExceptionMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.GenericExceptionMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.NotFoundExceptionMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.OptimisticLockExceptionMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.security.JsonSyntaxValidationFilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class RestJerseyTestSupport extends JerseyTest {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	protected final ResourceConfig configure() {
		ResourceConfig config = new ResourceConfig();
		config.register(JacksonFeature.class);
		config.register(ValidationFeature.class);
		config.register(ConstraintViolationExceptionMapper.class);
		config.register(BadRequestExceptionMapper.class);
		config.register(NotFoundExceptionMapper.class);
		config.register(OptimisticLockExceptionMapper.class);
		config.register(ApiExceptionMapper.class);
		config.register(GenericExceptionMapper.class);
		config.register(JsonSyntaxValidationFilter.class);
		registerResources(config);
		return config;
	}

	protected abstract void registerResources(ResourceConfig config);

	protected Map<String, Object> asJsonMap(String json) {
		try {
			return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
			});
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to parse JSON: " + json, ex);
		}
	}

	@SuppressWarnings("unchecked")
	protected void assertStandardErrorPayload(int expectedStatus, String expectedError, jakarta.ws.rs.core.Response response,
			Map<String, Object> errorPayload) {
		assertEquals(expectedStatus, response.getStatus());
		assertEquals(2, errorPayload.size());
		assertTrue(errorPayload.containsKey("error"));
		assertTrue(errorPayload.containsKey("messages"));
		assertEquals(expectedError, errorPayload.get("error"));
		assertNotNull(errorPayload.get("messages"));
		List<Object> messages = (List<Object>) errorPayload.get("messages");
		assertFalse(messages.isEmpty());
	}
}

package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;

class HelloParamsResourceIT extends RestJerseyTestSupport {
	@Override
	protected void registerResources(ResourceConfig config) {
		config.register(HelloWorldResource.class);
		config.register(ParamsResource.class);
	}

	@Test
	void shouldReturnHelloWorldPayload() {
		Response response = target("helloWorld").request().get();
		Map<String, Object> payload = asJsonMap(response.readEntity(String.class));

		assertEquals(200, response.getStatus());
		assertEquals("Hello World", payload.get("message"));
		assertEquals("MasterAnnonce REST backend", payload.get("service"));
		assertTrue(payload.containsKey("timestamp"));
	}

	@Test
	void shouldReturnQueryParamsPayload() {
		Response response = target("params").queryParam("q", "bike").queryParam("page", 3).request().get();
		Map<String, Object> payload = asJsonMap(response.readEntity(String.class));

		assertEquals(200, response.getStatus());
		assertEquals("bike", payload.get("query"));
		assertEquals(3, ((Number) payload.get("page")).intValue());
	}

	@Test
	void shouldReturnPathAndQueryParamsPayload() {
		Response response = target("params/15").queryParam("verbose", true).request().get();
		Map<String, Object> payload = asJsonMap(response.readEntity(String.class));

		assertEquals(200, response.getStatus());
		assertEquals(15, ((Number) payload.get("pathId")).intValue());
		assertEquals(Boolean.TRUE, payload.get("verbose"));
	}
}

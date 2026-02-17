package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;

class SimpleLoadIT extends RestJerseyTestSupport {
	@Override
	protected void registerResources(ResourceConfig config) {
		config.register(HelloWorldResource.class);
		config.register(ParamsResource.class);
	}

	@Test
	void shouldHandleSimpleBurstWithoutErrors() {
		for (int i = 0; i < 100; i++) {
			Response hello = target("helloWorld").request().get();
			Response params = target("params").queryParam("q", "load").queryParam("page", i % 10).request().get();

			assertEquals(200, hello.getStatus());
			assertEquals(200, params.getStatus());
		}
	}
}

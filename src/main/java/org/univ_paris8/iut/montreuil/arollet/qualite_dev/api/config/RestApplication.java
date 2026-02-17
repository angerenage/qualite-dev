package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.config;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.validation.ValidationFeature;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ApiExceptionMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.BadRequestExceptionMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ConstraintViolationExceptionMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.GenericExceptionMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.NotFoundExceptionMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.OptimisticLockExceptionMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.logging.RequestResponseLoggingFilter;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.security.AuthTokenFilter;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.security.JsonSyntaxValidationFilter;
import org.slf4j.bridge.SLF4JBridgeHandler;

import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/api")
@OpenAPIDefinition(info = @Info(title = "MasterAnnonce REST API", version = "1.0.0", description = "Layered Jakarta EE REST backend for annonces and authentication.", contact = @Contact(name = "MasterAnnonce Team")),
		security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "UUID")
public class RestApplication extends ResourceConfig {
	private static final AtomicBoolean JUL_BRIDGE_INSTALLED = new AtomicBoolean(false);

	public RestApplication() {
		packages("org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.resources");
		register(JacksonFeature.class);
		register(ValidationFeature.class);
		register(ConstraintViolationExceptionMapper.class);
		register(BadRequestExceptionMapper.class);
		register(NotFoundExceptionMapper.class);
		register(OptimisticLockExceptionMapper.class);
		register(ApiExceptionMapper.class);
		register(GenericExceptionMapper.class);
		register(RequestResponseLoggingFilter.class);
		register(JsonSyntaxValidationFilter.class);
		register(AuthTokenFilter.class);
		configureJulBridge();
		configureOpenApi();
	}

	private void configureJulBridge() {
		if (JUL_BRIDGE_INSTALLED.compareAndSet(false, true)) {
			SLF4JBridgeHandler.removeHandlersForRootLogger();
			SLF4JBridgeHandler.install();
		}
	}

	private void configureOpenApi() {
		OpenAPI openAPI = new OpenAPI();
		openAPI.info(new io.swagger.v3.oas.models.info.Info().title("MasterAnnonce REST API").version("1.0.0")
				.description("Layered Jakarta EE REST backend for annonces and authentication.")
				.license(new License().name("Internal project")));

		SwaggerConfiguration config = new SwaggerConfiguration().openAPI(openAPI).prettyPrint(true)
				.readAllResources(true)
				.resourcePackages(Set.of("org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.resources"));
		try {
			new JaxrsOpenApiContextBuilder<>().openApiConfiguration(config).buildContext(true);
		} catch (OpenApiConfigurationException ex) {
			throw new IllegalStateException("Failed to initialize OpenAPI context.", ex);
		}

		register(OpenApiResource.class);
		register(AcceptHeaderOpenApiResource.class);
	}
}

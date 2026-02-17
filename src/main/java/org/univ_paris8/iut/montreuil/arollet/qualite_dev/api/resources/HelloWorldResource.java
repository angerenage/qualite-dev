package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.resources;

import java.time.OffsetDateTime;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.HelloWorldResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/helloWorld")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Health")
public class HelloWorldResource {
	@GET
	@Operation(summary = "Health-check endpoint")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Service heartbeat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HelloWorldResponseDto.class))),
			@ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response helloWorld() {
		HelloWorldResponseDto payload = new HelloWorldResponseDto("Hello World", "MasterAnnonce REST backend",
				OffsetDateTime.now().toString());
		return Response.ok(payload).build();
	}
}

package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.resources;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ParamsResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/params")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Examples")
public class ParamsResource {
	@GET
	@Operation(summary = "QueryParam example")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "QueryParam payload", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParamsResponseDto.class))),
			@ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response queryParamExample(@QueryParam("q") @DefaultValue("all") String query,
			@Parameter(description = "Page index", example = "0")
			@QueryParam("page") @DefaultValue("0") int page) {
		ParamsResponseDto payload = new ParamsResponseDto(null, query, page, null,
				"QueryParam example on GET /api/params");
		return Response.ok(payload).build();
	}

	@GET
	@Path("/{resourceId}")
	@Operation(summary = "PathParam and QueryParam example")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Path/query payload", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParamsResponseDto.class))),
			@ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response pathAndQueryExample(@PathParam("resourceId") Long resourceId,
			@QueryParam("verbose") @DefaultValue("false") boolean verbose) {
		ParamsResponseDto payload = new ParamsResponseDto(resourceId, null, null, verbose,
				"PathParam + QueryParam example on GET /api/params/{resourceId}");
		return Response.ok(payload).build();
	}
}

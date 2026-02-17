package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.resources;

import java.net.URI;
import java.util.List;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.AnnoncePageResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.AnnonceResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.CreateAnnonceRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.UpdateAnnonceRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ApiException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.mappers.AnnonceApiMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.auth.AuthenticatedUser;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.api.AnnonceApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

@Path("/annonces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Annonces")
public class AnnonceResource {
	private final AnnonceApiService annonceService;
	private final AnnonceApiMapper annonceMapper;

	public AnnonceResource() {
		this(new AnnonceApiService(), new AnnonceApiMapper());
	}

	AnnonceResource(AnnonceApiService annonceService, AnnonceApiMapper annonceMapper) {
		this.annonceService = annonceService;
		this.annonceMapper = annonceMapper;
	}

	@GET
	@Operation(summary = "List annonces with pagination")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Paged annonces", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnoncePageResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response list(@QueryParam("page") @DefaultValue("0") @Min(value = 0, message = "page must be >= 0.") int page,
			@Parameter(description = "Page size from 1 to 100", example = "20")
			@QueryParam("size") @DefaultValue("20") @Min(value = 1, message = "size must be >= 1.") @Max(value = 100, message = "size must be <= 100.") int size) {
		List<Annonce> annonces = annonceService.list(page, size);
		long totalItems = annonceService.countAll();
		AnnoncePageResponseDto response = annonceMapper.toPageResponse(annonces, page, size, totalItems);
		return Response.ok(response).build();
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Get annonce details")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Annonce details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnonceResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Annonce not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response detail(@PathParam("id") @NotNull(message = "id is required.") @Positive(message = "id must be a positive number.") Long id) {
		Annonce annonce = annonceService.getById(id);
		AnnonceResponseDto response = annonceMapper.toResponse(annonce);
		return Response.ok(response).build();
	}

	@POST
	@Operation(summary = "Create a new annonce")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Annonce created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnonceResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response create(@Valid @NotNull(message = "Request body is required.") CreateAnnonceRequestDto request,
			@Context UriInfo uriInfo, @Context SecurityContext securityContext) {
		Long currentUserId = extractAuthenticatedUserId(securityContext);
		Annonce created = annonceService.create(annonceMapper.toCommand(request), currentUserId);
		AnnonceResponseDto response = annonceMapper.toResponse(created);
		URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
		return Response.created(location).entity(response).build();
	}

	@PUT
	@Path("/{id}")
	@Operation(summary = "Update an annonce")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Annonce updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnonceResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "403", description = "Authenticated user is not author", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Annonce not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "Business conflict or optimistic lock conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response update(@PathParam("id") @NotNull(message = "id is required.") @Positive(message = "id must be a positive number.") Long id,
			@Valid @NotNull(message = "Request body is required.") UpdateAnnonceRequestDto request,
			@Context SecurityContext securityContext) {
		Long currentUserId = extractAuthenticatedUserId(securityContext);
		Annonce updated = annonceService.updateAnnonce(id, annonceMapper.toCommand(request), currentUserId);
		AnnonceResponseDto response = annonceMapper.toResponse(updated);
		return Response.ok(response).build();
	}

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Delete an annonce")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Annonce deleted"),
			@ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "403", description = "Authenticated user is not author", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Annonce not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "Business conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response delete(@PathParam("id") @NotNull(message = "id is required.") @Positive(message = "id must be a positive number.") Long id,
			@Context SecurityContext securityContext) {
		Long currentUserId = extractAuthenticatedUserId(securityContext);
		annonceService.deleteAnnonce(id, currentUserId);
		return Response.noContent().build();
	}

	@POST
	@Path("/{id}/publish")
	@Operation(summary = "Publish an annonce")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Annonce published", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnonceResponseDto.class))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "403", description = "Authenticated user is not author", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Annonce not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "Business conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response publish(@PathParam("id") @NotNull(message = "id is required.") @Positive(message = "id must be a positive number.") Long id,
			@Context SecurityContext securityContext) {
		Long currentUserId = extractAuthenticatedUserId(securityContext);
		Annonce published = annonceService.publishAnnonce(id, currentUserId);
		return Response.ok(annonceMapper.toResponse(published)).build();
	}

	@POST
	@Path("/{id}/archive")
	@Operation(summary = "Archive an annonce")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Annonce archived", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnonceResponseDto.class))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "403", description = "Authenticated user is not author", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Annonce not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "Business conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response archive(@PathParam("id") @NotNull(message = "id is required.") @Positive(message = "id must be a positive number.") Long id,
			@Context SecurityContext securityContext) {
		Long currentUserId = extractAuthenticatedUserId(securityContext);
		Annonce archived = annonceService.archiveAnnonce(id, currentUserId);
		return Response.ok(annonceMapper.toResponse(archived)).build();
	}

	private Long extractAuthenticatedUserId(SecurityContext securityContext) {
		if (securityContext == null || !(securityContext.getUserPrincipal() instanceof AuthenticatedUser)) {
			throw new ApiException(Response.Status.UNAUTHORIZED, "Authentication is required.");
		}
		return ((AuthenticatedUser) securityContext.getUserPrincipal()).getUserId();
	}
}

package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.resources;

import java.net.URI;
import java.util.List;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.CategoryPageResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.CategoryResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.CreateCategoryRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.UpdateCategoryRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ApiException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.mappers.CategoryApiMapper;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.auth.AuthenticatedUser;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.api.CategoryApiService;

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

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Categories")
public class CategoryResource {
	private final CategoryApiService categoryService;
	private final CategoryApiMapper categoryMapper;

	public CategoryResource() {
		this(new CategoryApiService(), new CategoryApiMapper());
	}

	CategoryResource(CategoryApiService categoryService, CategoryApiMapper categoryMapper) {
		this.categoryService = categoryService;
		this.categoryMapper = categoryMapper;
	}

	@GET
	@Operation(summary = "List categories with pagination")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Paged categories", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryPageResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response list(@QueryParam("page") @DefaultValue("0") @Min(value = 0, message = "page must be >= 0.") int page,
			@Parameter(description = "Page size from 1 to 100", example = "20")
			@QueryParam("size") @DefaultValue("20") @Min(value = 1, message = "size must be >= 1.") @Max(value = 100, message = "size must be <= 100.") int size,
			@Parameter(description = "Optional case-insensitive search keyword on label", example = "hous")
			@QueryParam("keyword") String keyword) {
		List<Category> categories = categoryService.list(page, size, keyword);
		long totalItems = categoryService.countAll(keyword);
		CategoryPageResponseDto response = categoryMapper.toPageResponse(categories, page, size, totalItems);
		return Response.ok(response).build();
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Get category details")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Category details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response detail(@PathParam("id") @NotNull(message = "id is required.") @Positive(message = "id must be a positive number.") Long id) {
		Category category = categoryService.getById(id);
		return Response.ok(categoryMapper.toResponse(category)).build();
	}

	@POST
	@Operation(summary = "Create a new category")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Category created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "Business conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response create(@Valid @NotNull(message = "Request body is required.") CreateCategoryRequestDto request,
			@Context UriInfo uriInfo, @Context SecurityContext securityContext) {
		Long currentUserId = extractAuthenticatedUserId(securityContext);
		Category created = categoryService.create(categoryMapper.toLabel(request), currentUserId);
		CategoryResponseDto response = categoryMapper.toResponse(created);
		URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
		return Response.created(location).entity(response).build();
	}

	@PUT
	@Path("/{id}")
	@Operation(summary = "Update a category")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Category updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "Business conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response update(@PathParam("id") @NotNull(message = "id is required.") @Positive(message = "id must be a positive number.") Long id,
			@Valid @NotNull(message = "Request body is required.") UpdateCategoryRequestDto request,
			@Context SecurityContext securityContext) {
		Long currentUserId = extractAuthenticatedUserId(securityContext);
		Category updated = categoryService.update(id, categoryMapper.toLabel(request), currentUserId);
		return Response.ok(categoryMapper.toResponse(updated)).build();
	}

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Delete a category")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Category deleted"),
			@ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "Business conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response delete(@PathParam("id") @NotNull(message = "id is required.") @Positive(message = "id must be a positive number.") Long id,
			@Context SecurityContext securityContext) {
		Long currentUserId = extractAuthenticatedUserId(securityContext);
		categoryService.deleteCategory(id, currentUserId);
		return Response.noContent().build();
	}

	private Long extractAuthenticatedUserId(SecurityContext securityContext) {
		if (securityContext == null || !(securityContext.getUserPrincipal() instanceof AuthenticatedUser)) {
			throw new ApiException(Response.Status.UNAUTHORIZED, "Authentication is required.");
		}
		return ((AuthenticatedUser) securityContext.getUserPrincipal()).getUserId();
	}
}

package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.resources;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.LoginRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.LoginResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.auth.AuthLoginResult;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication")
public class AuthResource {
	private final AuthService authService;

	public AuthResource() {
		this(AuthService.getInstance());
	}

	AuthResource(AuthService authService) {
		this.authService = authService;
	}

	@POST
	@Operation(summary = "Authenticate user and issue stateless bearer token")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Authenticated user token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Malformed request or validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))) })
	public Response login(@Valid @NotNull(message = "Request body is required.") LoginRequestDto request) {
		AuthLoginResult loginResult = authService.authenticate(request.getUsername(), request.getPassword());
		LoginResponseDto response = new LoginResponseDto(loginResult.getToken(), loginResult.getExpiresIn());
		return Response.ok(response).build();
	}
}

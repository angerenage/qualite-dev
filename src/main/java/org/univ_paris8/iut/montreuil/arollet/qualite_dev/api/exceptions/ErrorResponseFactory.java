package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions;

import java.util.Collections;
import java.util.List;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;

import jakarta.ws.rs.core.Response;

public final class ErrorResponseFactory {
	private ErrorResponseFactory() {
	}

	public static ErrorResponseDto of(Response.Status status, String message) {
		return new ErrorResponseDto(toErrorCode(status), Collections.singletonList(message));
	}

	public static ErrorResponseDto of(Response.Status status, List<String> messages) {
		return new ErrorResponseDto(toErrorCode(status), messages);
	}

	private static String toErrorCode(Response.Status status) {
		if (status == null) {
			return "INTERNAL_ERROR";
		}
		switch (status) {
			case BAD_REQUEST:
				return "VALIDATION_ERROR";
			case NOT_FOUND:
				return "NOT_FOUND";
			case CONFLICT:
				return "CONFLICT";
			case UNAUTHORIZED:
				return "UNAUTHORIZED";
			case FORBIDDEN:
				return "FORBIDDEN";
			case INTERNAL_SERVER_ERROR:
				return "INTERNAL_ERROR";
			default:
				return "INTERNAL_ERROR";
		}
	}
}

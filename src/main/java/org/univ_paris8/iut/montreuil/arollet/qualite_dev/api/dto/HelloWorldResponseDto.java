package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HelloWorldResponse", description = "Simple service heartbeat payload")
public class HelloWorldResponseDto {
	@Schema(example = "Hello World")
	private String message;
	@Schema(example = "MasterAnnonce REST backend")
	private String service;
	@Schema(example = "2026-02-25T09:00:00+01:00")
	private String timestamp;

	public HelloWorldResponseDto() {
	}

	public HelloWorldResponseDto(String message, String service, String timestamp) {
		this.message = message;
		this.service = service;
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}

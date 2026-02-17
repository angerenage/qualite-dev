package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponseDto {
	private String error;
	private List<String> messages = new ArrayList<>();

	public ErrorResponseDto() {
	}

	public ErrorResponseDto(String error, List<String> messages) {
		this.error = error;
		setMessages(messages);
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages == null ? new ArrayList<>() : new ArrayList<>(messages);
	}
}

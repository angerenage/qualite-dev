package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.error;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final List<String> messages;

    public ApiException(HttpStatus status, String message) {
        this(status, List.of(message));
    }

    public ApiException(HttpStatus status, List<String> messages) {
        super(messages == null || messages.isEmpty() ? status.getReasonPhrase() : messages.get(0));
        this.status = status;
        this.messages = messages == null ? List.of() : List.copyOf(messages);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<String> getMessages() {
        return messages;
    }
}

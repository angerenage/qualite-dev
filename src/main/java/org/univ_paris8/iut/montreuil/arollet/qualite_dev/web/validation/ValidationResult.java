package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ValidationResult {
	private final Map<String, String> fieldErrors = new LinkedHashMap<>();
	private final List<String> globalErrors = new ArrayList<>();

	public void addFieldError(String field, String message) {
		if (field == null || message == null || message.trim().isEmpty()) {
			return;
		}
		fieldErrors.putIfAbsent(field, message);
	}

	public void addGlobalError(String message) {
		if (message == null || message.trim().isEmpty()) {
			return;
		}
		globalErrors.add(message);
	}

	public boolean hasErrors() {
		return !fieldErrors.isEmpty() || !globalErrors.isEmpty();
	}

	public Map<String, String> getFieldErrors() {
		return Collections.unmodifiableMap(fieldErrors);
	}

	public List<String> getGlobalErrors() {
		return Collections.unmodifiableList(globalErrors);
	}
}

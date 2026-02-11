package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class FlashUtil {
	private static final String FLASH_SUCCESS = "flashSuccess";
	private static final String FLASH_ERROR = "flashError";

	private FlashUtil() {
	}

	public static void setSuccess(HttpSession session, String message) {
		if (session != null && message != null && !message.trim().isEmpty()) {
			session.setAttribute(FLASH_SUCCESS, message);
		}
	}

	public static void setError(HttpSession session, String message) {
		if (session != null && message != null && !message.trim().isEmpty()) {
			session.setAttribute(FLASH_ERROR, message);
		}
	}

	public static void consumeToRequest(HttpServletRequest request) {
		if (request == null) {
			return;
		}
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}

		Object success = session.getAttribute(FLASH_SUCCESS);
		if (success != null) {
			request.setAttribute(FLASH_SUCCESS, success);
			session.removeAttribute(FLASH_SUCCESS);
		}

		Object error = session.getAttribute(FLASH_ERROR);
		if (error != null) {
			request.setAttribute(FLASH_ERROR, error);
			session.removeAttribute(FLASH_ERROR);
		}
	}
}

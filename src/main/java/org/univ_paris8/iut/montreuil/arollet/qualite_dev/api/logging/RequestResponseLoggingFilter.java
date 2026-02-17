package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.logging;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION - 10)
public class RequestResponseLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
	private static final String REQUEST_ID_PROPERTY = RequestResponseLoggingFilter.class.getName() + ".requestId";
	private static final String START_TIME_PROPERTY = RequestResponseLoggingFilter.class.getName() + ".startNs";
	private static final String REQUEST_ID_HEADER = "X-Request-Id";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String requestId = UUID.randomUUID().toString();
		String path = "/" + requestContext.getUriInfo().getPath(false);

		requestContext.setProperty(REQUEST_ID_PROPERTY, requestId);
		requestContext.setProperty(START_TIME_PROPERTY, System.nanoTime());

		MDC.put("requestId", requestId);
		MDC.put("path", path);
		MDC.put("status", "in-progress");

		LOGGER.info("event=request_received method={} path={}", requestContext.getMethod(), path);
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		String requestId = readRequestId(requestContext);
		String path = "/" + requestContext.getUriInfo().getPath(false);
		int status = responseContext.getStatus();
		long durationMs = computeDurationMs(requestContext.getProperty(START_TIME_PROPERTY));

		responseContext.getHeaders().putSingle(REQUEST_ID_HEADER, requestId);

		MDC.put("requestId", requestId);
		MDC.put("path", path);
		MDC.put("status", String.valueOf(status));
		LOGGER.info("event=request_completed method={} path={} status={} durationMs={}", requestContext.getMethod(),
				path, status, durationMs);
		MDC.clear();
	}

	private String readRequestId(ContainerRequestContext requestContext) {
		Object value = requestContext.getProperty(REQUEST_ID_PROPERTY);
		return value == null ? "unknown" : value.toString();
	}

	private long computeDurationMs(Object startNsProperty) {
		if (!(startNsProperty instanceof Long)) {
			return -1L;
		}
		long elapsedNs = System.nanoTime() - (Long) startNsProperty;
		return elapsedNs / 1_000_000L;
	}
}

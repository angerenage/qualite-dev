package org.univ_paris8.iut.montreuil.arollet.qualite_dev.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthFilter implements Filter {
	private static final String AUTH_USER = "authUser";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
			chain.doFilter(request, response);
			return;
		}

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String contextPath = httpRequest.getContextPath();
		String path = httpRequest.getRequestURI().substring(contextPath.length());

		if (isPublicPath(path)) {
			chain.doFilter(request, response);
			return;
		}

		HttpSession session = httpRequest.getSession(false);
		if (session != null && session.getAttribute(AUTH_USER) != null) {
			chain.doFilter(request, response);
			return;
		}

		httpResponse.sendRedirect(contextPath + "/login");
	}

	@Override
	public void destroy() {
	}

	private boolean isPublicPath(String path) {
		if (path == null) {
			return true;
		}
		if (path.equals("/") || path.equals("/index.jsp")) {
			return true;
		}
		if (path.equals("/login") || path.equals("/logout")) {
			return true;
		}
		if (path.startsWith("/css/")
				|| path.startsWith("/js/")
				|| path.startsWith("/images/")
				|| path.startsWith("/assets/")
				|| path.equals("/favicon.ico")) {
			return true;
		}
		return false;
	}
}

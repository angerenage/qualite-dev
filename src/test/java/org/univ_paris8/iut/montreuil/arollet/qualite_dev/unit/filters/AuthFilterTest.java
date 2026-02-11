package org.univ_paris8.iut.montreuil.arollet.qualite_dev.unit.filters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.filters.AuthFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class AuthFilterTest {
	@Test
	void shouldRedirectToLoginWhenNoSession() throws Exception {
		AuthFilter filter = new AuthFilter();
		HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = org.mockito.Mockito.mock(HttpServletResponse.class);
		FilterChain chain = org.mockito.Mockito.mock(FilterChain.class);

		when(request.getContextPath()).thenReturn("/app");
		when(request.getRequestURI()).thenReturn("/app/annonces");
		when(request.getSession(false)).thenReturn(null);

		filter.doFilter(request, response, chain);

		verify(response).sendRedirect("/app/login");
		verify(chain, never()).doFilter(any(), any());
	}

	@Test
	void shouldCallChainWhenSessionIsAuthenticated() throws Exception {
		AuthFilter filter = new AuthFilter();
		HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = org.mockito.Mockito.mock(HttpServletResponse.class);
		FilterChain chain = org.mockito.Mockito.mock(FilterChain.class);
		HttpSession session = org.mockito.Mockito.mock(HttpSession.class);

		when(request.getContextPath()).thenReturn("/app");
		when(request.getRequestURI()).thenReturn("/app/annonces");
		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("authUser")).thenReturn(new Object());

		filter.doFilter(request, response, chain);

		verify(chain).doFilter(request, response);
		verify(response, never()).sendRedirect("/app/login");
	}

	@Test
	void shouldSkipFilterForPublicLoginAndStaticPaths() throws Exception {
		AuthFilter filter = new AuthFilter();
		HttpServletRequest loginRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
		HttpServletResponse loginResponse = org.mockito.Mockito.mock(HttpServletResponse.class);
		FilterChain loginChain = org.mockito.Mockito.mock(FilterChain.class);

		when(loginRequest.getContextPath()).thenReturn("/app");
		when(loginRequest.getRequestURI()).thenReturn("/app/login");

		filter.doFilter(loginRequest, loginResponse, loginChain);
		verify(loginChain).doFilter(loginRequest, loginResponse);

		HttpServletRequest staticRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
		HttpServletResponse staticResponse = org.mockito.Mockito.mock(HttpServletResponse.class);
		FilterChain staticChain = org.mockito.Mockito.mock(FilterChain.class);

		when(staticRequest.getContextPath()).thenReturn("/app");
		when(staticRequest.getRequestURI()).thenReturn("/app/css/style.css");

		filter.doFilter(staticRequest, staticResponse, staticChain);
		verify(staticChain).doFilter(staticRequest, staticResponse);
	}
}

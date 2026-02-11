package org.univ_paris8.iut.montreuil.arollet.qualite_dev.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.User;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.AnnonceService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.CategoryService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.UserService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.SessionUser;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class ServletWebLayerTest {
	@Test
	void loginShouldRedirectOnSuccess() throws Exception {
		StubUserService userService = new StubUserService();
		User user = new User();
		user.setId(1L);
		user.setUsername("admin");
		userService.authResult = user;

		Login servlet = new Login(userService);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);

		when(request.getParameter("login")).thenReturn("admin");
		when(request.getParameter("password")).thenReturn("secret");
		when(request.getSession(true)).thenReturn(session);
		when(request.getContextPath()).thenReturn("/app");

		servlet.doPost(request, response);

		verify(session).setAttribute(anyString(), any(SessionUser.class));
		verify(response).sendRedirect("/app/annonces");
		assertEquals("admin", userService.lastLogin);
		assertEquals("secret", userService.lastPassword);
	}

	@Test
	void loginShouldForwardOnAuthenticationFailure() throws Exception {
		StubUserService userService = new StubUserService();
		userService.authResult = null;
		Login servlet = new Login(userService);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);

		when(request.getParameter("login")).thenReturn("admin");
		when(request.getParameter("password")).thenReturn("bad");
		when(request.getSession(true)).thenReturn(session);
		when(request.getRequestDispatcher("/WEB-INF/jsp/login.jsp")).thenReturn(dispatcher);

		servlet.doPost(request, response);

		verify(dispatcher).forward(request, response);
		verify(response, never()).sendRedirect(anyString());
	}

	@Test
	void createAnnonceShouldForwardOnValidationError() throws Exception {
		StubAnnonceService annonceService = new StubAnnonceService();
		StubCategoryService categoryService = new StubCategoryService();
		StubUserService userService = new StubUserService();
		AnnonceList servlet = new AnnonceList(annonceService, categoryService, userService);

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);

		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("authUser")).thenReturn(new SessionUser(1L, "admin"));
		when(request.getParameter("title")).thenReturn("");
		when(request.getParameter("description")).thenReturn("");
		when(request.getParameter("adress")).thenReturn("");
		when(request.getParameter("mail")).thenReturn("");
		when(request.getParameter("categoryId")).thenReturn("");
		when(request.getRequestDispatcher("/WEB-INF/jsp/annonces/form.jsp")).thenReturn(dispatcher);

		servlet.doPost(request, response);

		verify(dispatcher).forward(request, response);
		verify(response, never()).sendRedirect(anyString());
		assertFalse(annonceService.createCalled);
	}

	@Test
	void createAnnonceShouldRedirectOnSuccess() throws Exception {
		StubAnnonceService annonceService = new StubAnnonceService();
		StubCategoryService categoryService = new StubCategoryService();
		StubUserService userService = new StubUserService();
		AnnonceList servlet = new AnnonceList(annonceService, categoryService, userService);

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);

		User user = new User();
		user.setId(1L);
		user.setUsername("admin");
		userService.userById = user;

		Category category = new Category("Informatique");
		category.setId(2L);
		categoryService.categoryById = category;

		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("authUser")).thenReturn(new SessionUser(1L, "admin"));
		when(request.getContextPath()).thenReturn("/app");
		when(request.getParameter("title")).thenReturn("Titre");
		when(request.getParameter("description")).thenReturn("Description");
		when(request.getParameter("adress")).thenReturn("Paris");
		when(request.getParameter("mail")).thenReturn("mail@test.local");
		when(request.getParameter("categoryId")).thenReturn("2");

		servlet.doPost(request, response);

		assertTrue(annonceService.createCalled);
		verify(response).sendRedirect("/app/annonces/show?id=42");
	}

	@Test
	void publishShouldRedirectToDetail() throws Exception {
		StubAnnonceService annonceService = new StubAnnonceService();
		AnnonceStatus servlet = new AnnonceStatus(annonceService);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);

		when(request.getSession(true)).thenReturn(session);
		when(request.getParameter("id")).thenReturn("5");
		when(request.getServletPath()).thenReturn("/annonces/publish");
		when(request.getContextPath()).thenReturn("/app");

		servlet.doPost(request, response);

		assertEquals(Long.valueOf(5L), annonceService.lastPublishId);
		verify(response).sendRedirect("/app/annonces/show?id=5");
	}

	@Test
	void archiveShouldSetErrorWhenServiceThrows() throws Exception {
		StubAnnonceService annonceService = new StubAnnonceService();
		annonceService.archiveException = new IllegalStateException("Transition invalide");
		AnnonceStatus servlet = new AnnonceStatus(annonceService);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);

		when(request.getSession(true)).thenReturn(session);
		when(request.getParameter("id")).thenReturn("6");
		when(request.getServletPath()).thenReturn("/annonces/archive");
		when(request.getContextPath()).thenReturn("/app");

		servlet.doPost(request, response);

		assertEquals(Long.valueOf(6L), annonceService.lastArchiveId);
		verify(session).setAttribute("flashError", "Transition invalide");
		verify(response).sendRedirect("/app/annonces/show?id=6");
	}

	private static class StubUserService extends UserService {
		private User authResult;
		private User userById;
		private String lastLogin;
		private String lastPassword;

		@Override
		public User authenticate(String login, String password) {
			this.lastLogin = login;
			this.lastPassword = password;
			return authResult;
		}

		@Override
		public User findById(Long id) {
			return userById;
		}
	}

	private static class StubCategoryService extends CategoryService {
		private Category categoryById;

		@Override
		public Category findById(Long id) {
			return categoryById;
		}

		@Override
		public List<Category> list(int page, int size) {
			return Collections.emptyList();
		}
	}

	private static class StubAnnonceService extends AnnonceService {
		private boolean createCalled;
		private Long lastPublishId;
		private Long lastArchiveId;
		private RuntimeException archiveException;

		@Override
		public Annonce create(Annonce annonce) {
			createCalled = true;
			annonce.setId(42L);
			return annonce;
		}

		@Override
		public boolean publish(Long id) {
			lastPublishId = id;
			return true;
		}

		@Override
		public boolean archive(Long id) {
			lastArchiveId = id;
			if (archiveException != null) {
				throw archiveException;
			}
			return true;
		}
	}
}

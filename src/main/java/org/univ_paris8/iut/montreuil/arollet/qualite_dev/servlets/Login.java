package org.univ_paris8.iut.montreuil.arollet.qualite_dev.servlets;

import java.io.IOException;
import java.util.Collections;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.User;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.UserService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.FlashUtil;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.SessionUser;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.forms.LoginForm;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.validation.FormValidator;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.validation.ValidationResult;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class Login extends HttpServlet {
	private static final String AUTH_USER = "authUser";
	private final FormValidator validator = new FormValidator();
	private final UserService userService;

	public Login() {
		this(new UserService());
	}

	Login(UserService userService) {
		this.userService = userService;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession existingSession = request.getSession(false);
		if (existingSession != null && existingSession.getAttribute(AUTH_USER) != null) {
			response.sendRedirect(request.getContextPath() + "/annonces");
			return;
		}
		FlashUtil.consumeToRequest(request);
		if (request.getAttribute("form") == null) {
			request.setAttribute("form", new LoginForm());
		}
		if (request.getAttribute("fieldErrors") == null) {
			request.setAttribute("fieldErrors", Collections.emptyMap());
		}
		if (request.getAttribute("globalErrors") == null) {
			request.setAttribute("globalErrors", Collections.emptyList());
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		LoginForm form = new LoginForm();
		form.setLogin(safeTrim(request.getParameter("login")));
		form.setPassword(safeTrim(request.getParameter("password")));

		ValidationResult validation = validator.validateLogin(form);
		if (validation.hasErrors()) {
			forwardWithErrors(request, response, form, validation);
			return;
		}

		User user = userService.authenticate(form.getLogin(), form.getPassword());
		if (user == null) {
			validation.addGlobalError("Identifiants invalides.");
			forwardWithErrors(request, response, form, validation);
			return;
		}

		HttpSession session = request.getSession(true);
		session.setAttribute(AUTH_USER, new SessionUser(user.getId(), user.getUsername()));

		response.sendRedirect(request.getContextPath() + "/annonces");
	}

	private void forwardWithErrors(HttpServletRequest request, HttpServletResponse response, LoginForm form,
			ValidationResult validationResult) throws ServletException, IOException {
		request.setAttribute("form", form);
		request.setAttribute("fieldErrors", validationResult.getFieldErrors());
		request.setAttribute("globalErrors", validationResult.getGlobalErrors());
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
		dispatcher.forward(request, response);
	}

	private String safeTrim(String value) {
		return value == null ? "" : value.trim();
	}
}

package org.univ_paris8.iut.montreuil.arollet.qualite_dev.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.CategoryService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.FlashUtil;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.forms.AnnonceForm;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/annonces/new")
public class AnnonceAdd extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		loadCategories(request);
		FlashUtil.consumeToRequest(request);
		request.setAttribute("form", new AnnonceForm());
		request.setAttribute("fieldErrors", Collections.emptyMap());
		request.setAttribute("globalErrors", Collections.emptyList());
		request.setAttribute("editMode", Boolean.FALSE);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/annonces/form.jsp");
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Utilisez POST /annonces pour creer une annonce.");
	}

	private void loadCategories(HttpServletRequest request) {
		CategoryService categoryService = new CategoryService();
		List<Category> categories = categoryService.list(0, 100);
		request.setAttribute("categories", categories);
	}
}

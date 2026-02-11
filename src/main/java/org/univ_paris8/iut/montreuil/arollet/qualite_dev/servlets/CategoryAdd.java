package org.univ_paris8.iut.montreuil.arollet.qualite_dev.servlets;

import java.io.IOException;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.CategoryService;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/CategoryAdd")
public class CategoryAdd extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("/CategoryAdd.jsp");
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String label = safeTrim(request.getParameter("label"));
		if (label.isEmpty()) {
			request.setAttribute("error", "Le libelle est obligatoire.");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/CategoryAdd.jsp");
			dispatcher.forward(request, response);
			return;
		}

		try {
			CategoryService categoryService = new CategoryService();
			Category created = categoryService.create(label);
			if (created != null) {
				request.setAttribute("success", "Categorie enregistree.");
			} else {
				request.setAttribute("error", "Erreur lors de l'enregistrement.");
			}
		} catch (RuntimeException e) {
			request.setAttribute("error", "Erreur lors de l'enregistrement.");
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("/CategoryAdd.jsp");
		dispatcher.forward(request, response);
	}

	private String safeTrim(String value) {
		return value == null ? "" : value.trim();
	}
}

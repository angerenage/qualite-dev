package org.univ_paris8.iut.montreuil.arollet.qualite_dev.servlets;

import java.io.IOException;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.AnnonceService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AnnonceDelete")
public class AnnonceDelete extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idParam = request.getParameter("id");
		if (idParam == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametre id manquant.");
			return;
		}

		Long id;
		try {
			id = Long.parseLong(idParam);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametre id invalide.");
			return;
		}

		try {
			AnnonceService annonceService = new AnnonceService();
			boolean deleted = annonceService.deleteById(id);
			if (!deleted) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Annonce introuvable.");
				return;
			}
			response.sendRedirect(request.getContextPath() + "/annonces");
		} catch (RuntimeException e) {
			throw new ServletException("Impossible de se connecter a la base.", e);
		}
	}
}

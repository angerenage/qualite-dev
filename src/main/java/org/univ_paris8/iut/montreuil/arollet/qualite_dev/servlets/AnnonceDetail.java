package org.univ_paris8.iut.montreuil.arollet.qualite_dev.servlets;

import java.io.IOException;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.AnnonceService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.FlashUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/annonces/show")
public class AnnonceDetail extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idParam = request.getParameter("id");
		HttpSession session = request.getSession(true);
		if (idParam == null || idParam.trim().isEmpty()) {
			FlashUtil.setError(session, "Parametre id manquant.");
			response.sendRedirect(request.getContextPath() + "/annonces");
			return;
		}

		Long id;
		try {
			id = Long.parseLong(idParam.trim());
		} catch (NumberFormatException e) {
			FlashUtil.setError(session, "Parametre id invalide.");
			response.sendRedirect(request.getContextPath() + "/annonces");
			return;
		}

		try {
			AnnonceService annonceService = new AnnonceService();
			Annonce annonce = annonceService.findById(id);
			if (annonce == null) {
				FlashUtil.setError(session, "Annonce introuvable.");
				response.sendRedirect(request.getContextPath() + "/annonces");
				return;
			}

			FlashUtil.consumeToRequest(request);
			request.setAttribute("annonce", annonce);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/annonces/show.jsp");
			dispatcher.forward(request, response);
		} catch (RuntimeException e) {
			throw new ServletException("Impossible de se connecter a la base.", e);
		}
	}
}

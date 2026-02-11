package org.univ_paris8.iut.montreuil.arollet.qualite_dev.servlets;

import java.io.IOException;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.AnnonceService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.FlashUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/annonces/publish", "/annonces/archive"})
public class AnnonceStatus extends HttpServlet {
	private final AnnonceService annonceService;

	public AnnonceStatus() {
		this(new AnnonceService());
	}

	AnnonceStatus(AnnonceService annonceService) {
		this.annonceService = annonceService;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		Long id = parseLong(request.getParameter("id"));
		if (id == null) {
			FlashUtil.setError(session, "Parametre id invalide.");
			response.sendRedirect(request.getContextPath() + "/annonces");
			return;
		}

		String path = request.getServletPath();
		try {
			if ("/annonces/publish".equals(path)) {
				annonceService.publish(id);
				FlashUtil.setSuccess(session, "Annonce publiee.");
			} else if ("/annonces/archive".equals(path)) {
				annonceService.archive(id);
				FlashUtil.setSuccess(session, "Annonce archivee.");
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		} catch (RuntimeException e) {
			FlashUtil.setError(session, e.getMessage());
		}
		response.sendRedirect(request.getContextPath() + "/annonces/show?id=" + id);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Methode GET non autorisee.");
	}

	private Long parseLong(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}

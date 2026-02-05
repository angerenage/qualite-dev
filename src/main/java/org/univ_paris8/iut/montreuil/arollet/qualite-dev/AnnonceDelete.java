package fr.iut.univparis8.arollet;

import java.io.IOException;
import java.sql.Connection;
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
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre id manquant.");
			return;
		}

		int id;
		try {
			id = Integer.parseInt(idParam);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre id invalide.");
			return;
		}

		try {
			Connection connection = ConnectionDB.getInstance();
			AnnonceDAO annonceDAO = new AnnonceDAO(connection);
			boolean deleted = annonceDAO.deleteById(id);
			if (!deleted) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Annonce introuvable.");
				return;
			}
			response.sendRedirect(request.getContextPath() + "/AnnonceList");
		} catch (ClassNotFoundException e) {
			throw new ServletException("Impossible de se connecter à la base.", e);
		}
	}
}

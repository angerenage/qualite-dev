package fr.iut.univparis8.arollet;

import java.io.IOException;
import java.sql.Connection;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AnnonceUpdate")
public class AnnonceUpdate extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idParam = request.getParameter("id");
		if (idParam == null) {
			request.setAttribute("error", "Paramètre id manquant.");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/AnnonceUpdate.jsp");
			dispatcher.forward(request, response);
			return;
		}

		int id;
		try {
			id = Integer.parseInt(idParam);
		} catch (NumberFormatException e) {
			request.setAttribute("error", "Paramètre id invalide.");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/AnnonceUpdate.jsp");
			dispatcher.forward(request, response);
			return;
		}

		try {
			Connection connection = ConnectionDB.getInstance();
			AnnonceDAO annonceDAO = new AnnonceDAO(connection);
			Annonce annonce = annonceDAO.find(id);

			if (annonce == null) {
				request.setAttribute("error", "Annonce introuvable.");
			} else {
				request.setAttribute("annonce", annonce);
			}

			RequestDispatcher dispatcher = request.getRequestDispatcher("/AnnonceUpdate.jsp");
			dispatcher.forward(request, response);
		} catch (ClassNotFoundException e) {
			throw new ServletException("Impossible de se connecter à la base.", e);
		}
	}
}

package fr.iut.univparis8.arollet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AnnonceList")
public class AnnonceList extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Connection connection = ConnectionDB.getInstance();
			AnnonceDAO annonceDAO = new AnnonceDAO(connection);
			List<Annonce> annonces = annonceDAO.findAll();

			request.setAttribute("annonces", annonces);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/AnnonceList.jsp");
			dispatcher.forward(request, response);
		} catch (ClassNotFoundException e) {
			throw new ServletException("Impossible de se connecter à la base.", e);
		}
	}
}

package fr.iut.univparis8.arollet;

import java.io.IOException;
import java.sql.Connection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AnnonceAdd")
public class AnnonceAdd extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
	  throws ServletException, IOException {
	RequestDispatcher dispatcher = request.getRequestDispatcher("/AnnonceAdd.jsp");
	dispatcher.forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
	  throws ServletException, IOException {
	request.setCharacterEncoding("UTF-8");

	String title = request.getParameter("title").trim();
	String description = request.getParameter("description").trim();
	String adress = request.getParameter("adress").trim();
	String mail = request.getParameter("mail").trim();

	if (title.isBlank() || description.isBlank() || adress.isBlank() || mail.isBlank()) {
	  request.setAttribute("error", "Tous les champs sont obligatoires.");
	  RequestDispatcher dispatcher = request.getRequestDispatcher("/AnnonceAdd.jsp");
	  dispatcher.forward(request, response);
	  return;
	}

	try {
	  Connection connection = ConnectionDB.getInstance();
	  AnnonceDAO annonceDAO = new AnnonceDAO(connection);
	  Annonce annonce = new Annonce(title, description, adress, mail);

	  boolean created = annonceDAO.create(annonce);
	  if (created) {
		request.setAttribute("success", "Annonce enregistrée.");
	  } else {
		request.setAttribute("error", "Erreur lors de l'enregistrement.");
	  }
	  RequestDispatcher dispatcher = request.getRequestDispatcher("/AnnonceAdd.jsp");
	  dispatcher.forward(request, response);
	} catch (ClassNotFoundException e) {
	  throw new ServletException("Impossible de se connecter à la base.", e);
	}
  }
}

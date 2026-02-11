package org.univ_paris8.iut.montreuil.arollet.qualite_dev.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.AnnonceService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.CategoryService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.FlashUtil;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.forms.AnnonceForm;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.validation.FormValidator;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.validation.ValidationResult;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/annonces/edit", "/annonces/update"})
public class AnnonceUpdate extends HttpServlet {
	private final FormValidator validator = new FormValidator();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!"/annonces/edit".equals(request.getServletPath())) {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		HttpSession session = request.getSession(true);
		Long id = parseLong(request.getParameter("id"));
		if (id == null) {
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

			loadCategories(request);
			FlashUtil.consumeToRequest(request);
			request.setAttribute("form", toForm(annonce));
			request.setAttribute("fieldErrors", Collections.emptyMap());
			request.setAttribute("globalErrors", Collections.emptyList());
			request.setAttribute("editMode", Boolean.TRUE);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/annonces/form.jsp");
			dispatcher.forward(request, response);
		} catch (RuntimeException e) {
			throw new ServletException("Impossible de se connecter a la base.", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!"/annonces/update".equals(request.getServletPath())) {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession(true);

		AnnonceForm form = new AnnonceForm();
		form.setId(safeTrim(request.getParameter("id")));
		form.setTitle(safeTrim(request.getParameter("title")));
		form.setDescription(safeTrim(request.getParameter("description")));
		form.setAdress(safeTrim(request.getParameter("adress")));
		form.setMail(safeTrim(request.getParameter("mail")));
		form.setCategoryId(safeTrim(request.getParameter("categoryId")));

		ValidationResult validation = validator.validateAnnonce(form, true);
		if (validation.hasErrors()) {
			forwardUpdateWithErrors(request, response, form, validation.getFieldErrors(), validation.getGlobalErrors());
			return;
		}

		Long id = Long.parseLong(form.getId());
		Long categoryId = Long.parseLong(form.getCategoryId());

		try {
			AnnonceService annonceService = new AnnonceService();
			Annonce annonce = annonceService.findById(id);
			if (annonce == null) {
				FlashUtil.setError(session, "Annonce introuvable.");
				response.sendRedirect(request.getContextPath() + "/annonces");
				return;
			}

			CategoryService categoryService = new CategoryService();
			Category category = categoryService.findById(categoryId);
			if (category == null) {
				forwardUpdateWithErrors(request, response, form,
						Collections.singletonMap("categoryId", "Categorie introuvable."), Collections.emptyList());
				return;
			}

			annonce.setTitle(form.getTitle());
			annonce.setDescription(form.getDescription());
			annonce.setAdress(form.getAdress());
			annonce.setMail(form.getMail());
			annonce.setCategory(category);

			annonceService.update(annonce);
			FlashUtil.setSuccess(session, "Annonce mise a jour.");
			response.sendRedirect(request.getContextPath() + "/annonces/show?id=" + id);
		} catch (RuntimeException e) {
			forwardUpdateWithErrors(request, response, form, Collections.emptyMap(),
					Collections.singletonList("Erreur lors de la mise a jour."));
		}
	}

	private void loadCategories(HttpServletRequest request) {
		CategoryService categoryService = new CategoryService();
		List<Category> categories = categoryService.list(0, 100);
		request.setAttribute("categories", categories);
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

	private String safeTrim(String value) {
		return value == null ? "" : value.trim();
	}

	private AnnonceForm toForm(Annonce annonce) {
		AnnonceForm form = new AnnonceForm();
		form.setId(String.valueOf(annonce.getId()));
		form.setTitle(annonce.getTitle());
		form.setDescription(annonce.getDescription());
		form.setAdress(annonce.getAdress());
		form.setMail(annonce.getMail());
		if (annonce.getCategory() != null && annonce.getCategory().getId() != null) {
			form.setCategoryId(String.valueOf(annonce.getCategory().getId()));
		}
		return form;
	}

	private void forwardUpdateWithErrors(HttpServletRequest request, HttpServletResponse response, AnnonceForm form,
			Map<String, String> fieldErrors, List<String> globalErrors) throws ServletException, IOException {
		loadCategories(request);
		request.setAttribute("form", form);
		request.setAttribute("fieldErrors", fieldErrors);
		request.setAttribute("globalErrors", globalErrors);
		request.setAttribute("editMode", Boolean.TRUE);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/annonces/form.jsp");
		dispatcher.forward(request, response);
	}
}

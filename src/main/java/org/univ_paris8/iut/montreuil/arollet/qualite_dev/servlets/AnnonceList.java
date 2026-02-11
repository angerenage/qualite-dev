package org.univ_paris8.iut.montreuil.arollet.qualite_dev.servlets;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.AnnonceStatus;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.User;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.AnnonceService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.CategoryService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.UserService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.FlashUtil;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.SessionUser;
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

@WebServlet("/annonces")
public class AnnonceList extends HttpServlet {
	private static final String AUTH_USER = "authUser";
	private final FormValidator validator = new FormValidator();
	private final AnnonceService annonceService;
	private final CategoryService categoryService;
	private final UserService userService;

	public AnnonceList() {
		this(new AnnonceService(), new CategoryService(), new UserService());
	}

	AnnonceList(AnnonceService annonceService, CategoryService categoryService, UserService userService) {
		this.annonceService = annonceService;
		this.categoryService = categoryService;
		this.userService = userService;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			int page = parseInt(request.getParameter("page"), 1);
			int size = parseInt(request.getParameter("size"), 10);
			page = Math.max(1, page);
			size = Math.max(1, Math.min(size, 50));

			long totalItems = annonceService.countAll();
			int totalPages = totalItems > 0 ? (int) Math.ceil(totalItems / (double) size) : 0;
			if (totalPages > 0 && page > totalPages) {
				page = totalPages;
			}

			int pageIndex = page - 1;
			if (pageIndex < 0) {
				pageIndex = 0;
			}

			List<Annonce> annonces = annonceService.list(pageIndex, size);

			FlashUtil.consumeToRequest(request);
			request.setAttribute("annonces", annonces);
			request.setAttribute("currentPage", page);
			request.setAttribute("pageSize", size);
			request.setAttribute("totalPages", totalPages);
			request.setAttribute("totalItems", totalItems);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/annonces/list.jsp");
			dispatcher.forward(request, response);
		} catch (RuntimeException e) {
			throw new ServletException("Impossible de se connecter a la base.", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);
		if (session == null || !(session.getAttribute(AUTH_USER) instanceof SessionUser)) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		String title = safeTrim(request.getParameter("title"));
		String description = safeTrim(request.getParameter("description"));
		String adress = safeTrim(request.getParameter("adress"));
		String mail = safeTrim(request.getParameter("mail"));
		String categoryIdParam = safeTrim(request.getParameter("categoryId"));

		AnnonceForm form = new AnnonceForm();
		form.setTitle(title);
		form.setDescription(description);
		form.setAdress(adress);
		form.setMail(mail);
		form.setCategoryId(categoryIdParam);

		ValidationResult validation = validator.validateAnnonce(form, false);
		if (validation.hasErrors()) {
			forwardCreateWithErrors(request, response, form, validation.getFieldErrors(), validation.getGlobalErrors());
			return;
		}

		Long categoryId = Long.parseLong(categoryIdParam);

		SessionUser sessionUser = (SessionUser) session.getAttribute(AUTH_USER);
		User user = userService.findById(sessionUser.getId());
		if (user == null) {
			FlashUtil.setError(session, "Utilisateur introuvable.");
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		Category category = categoryService.findById(categoryId);
		if (category == null) {
			forwardCreateWithErrors(request, response, form, Collections.singletonMap("categoryId", "Categorie introuvable."),
					Collections.emptyList());
			return;
		}

		try {
			Annonce annonce = new Annonce(form.getTitle(), form.getDescription(), form.getAdress(), form.getMail(), user,
					category, AnnonceStatus.DRAFT);
			annonce.setDate(new Timestamp(System.currentTimeMillis()));
			Annonce created = annonceService.create(annonce);
			if (created == null || created.getId() == null) {
				forwardCreateWithErrors(request, response, form, Collections.emptyMap(),
						Collections.singletonList("Erreur lors de l'enregistrement."));
				return;
			}

			FlashUtil.setSuccess(session, "Annonce enregistree.");
			response.sendRedirect(request.getContextPath() + "/annonces/show?id=" + created.getId());
		} catch (RuntimeException e) {
			forwardCreateWithErrors(request, response, form, Collections.emptyMap(),
					Collections.singletonList("Erreur lors de l'enregistrement."));
		}
	}

	private int parseInt(String value, int defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private String safeTrim(String value) {
		return value == null ? "" : value.trim();
	}

	private void forwardCreateWithErrors(HttpServletRequest request, HttpServletResponse response, AnnonceForm form,
			Map<String, String> fieldErrors, List<String> globalErrors) throws ServletException, IOException {
		request.setAttribute("categories", categoryService.list(0, 100));
		request.setAttribute("form", form);
		request.setAttribute("fieldErrors", fieldErrors);
		request.setAttribute("globalErrors", globalErrors);
		request.setAttribute("editMode", Boolean.FALSE);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/annonces/form.jsp");
		dispatcher.forward(request, response);
	}
}

package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.validation;

import java.util.regex.Pattern;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.forms.AnnonceForm;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.forms.LoginForm;

public class FormValidator {
	private static final int MAX_TITLE_LENGTH = 64;
	private static final int MAX_DESCRIPTION_LENGTH = 256;
	private static final int MAX_ADRESS_LENGTH = 64;
	private static final int MAX_MAIL_LENGTH = 64;
	private static final int MAX_LOGIN_LENGTH = 128;

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
	private static final Pattern POSITIVE_NUMBER_PATTERN = Pattern.compile("^[1-9][0-9]*$");

	public ValidationResult validateLogin(LoginForm form) {
		ValidationResult result = new ValidationResult();
		if (form == null) {
			result.addGlobalError("Formulaire invalide.");
			return result;
		}

		String login = safeTrim(form.getLogin());
		String password = safeTrim(form.getPassword());

		if (login.isEmpty()) {
			result.addFieldError("login", "L'identifiant est obligatoire.");
		} else if (login.length() > MAX_LOGIN_LENGTH) {
			result.addFieldError("login", "L'identifiant est trop long.");
		}

		if (password.isEmpty()) {
			result.addFieldError("password", "Le mot de passe est obligatoire.");
		}

		return result;
	}

	public ValidationResult validateAnnonce(AnnonceForm form, boolean requireId) {
		ValidationResult result = new ValidationResult();
		if (form == null) {
			result.addGlobalError("Formulaire invalide.");
			return result;
		}

		if (requireId) {
			String id = safeTrim(form.getId());
			if (id.isEmpty()) {
				result.addFieldError("id", "Identifiant manquant.");
			} else if (!POSITIVE_NUMBER_PATTERN.matcher(id).matches()) {
				result.addFieldError("id", "Identifiant invalide.");
			}
		}

		String title = safeTrim(form.getTitle());
		if (title.isEmpty()) {
			result.addFieldError("title", "Le titre est obligatoire.");
		} else if (title.length() > MAX_TITLE_LENGTH) {
			result.addFieldError("title", "Le titre doit contenir au maximum 64 caracteres.");
		}

		String description = safeTrim(form.getDescription());
		if (description.isEmpty()) {
			result.addFieldError("description", "La description est obligatoire.");
		} else if (description.length() > MAX_DESCRIPTION_LENGTH) {
			result.addFieldError("description", "La description doit contenir au maximum 256 caracteres.");
		}

		String adress = safeTrim(form.getAdress());
		if (adress.isEmpty()) {
			result.addFieldError("adress", "L'adresse est obligatoire.");
		} else if (adress.length() > MAX_ADRESS_LENGTH) {
			result.addFieldError("adress", "L'adresse doit contenir au maximum 64 caracteres.");
		}

		String mail = safeTrim(form.getMail());
		if (mail.isEmpty()) {
			result.addFieldError("mail", "L'email est obligatoire.");
		} else if (mail.length() > MAX_MAIL_LENGTH) {
			result.addFieldError("mail", "L'email doit contenir au maximum 64 caracteres.");
		} else if (!EMAIL_PATTERN.matcher(mail).matches()) {
			result.addFieldError("mail", "Le format de l'email est invalide.");
		}

		String categoryId = safeTrim(form.getCategoryId());
		if (categoryId.isEmpty()) {
			result.addFieldError("categoryId", "La categorie est obligatoire.");
		} else if (!POSITIVE_NUMBER_PATTERN.matcher(categoryId).matches()) {
			result.addFieldError("categoryId", "La categorie selectionnee est invalide.");
		}

		return result;
	}

	private String safeTrim(String value) {
		return value == null ? "" : value.trim();
	}
}

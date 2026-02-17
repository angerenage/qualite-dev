function toInt(value, fallbackValue) {
  var parsed = Number(value);
  if (!Number.isFinite(parsed)) {
    return fallbackValue;
  }
  return Math.trunc(parsed);
}

function toPositiveLong(value) {
  var parsed = Number(value);
  if (!Number.isFinite(parsed)) {
    return null;
  }
  var longValue = Math.trunc(parsed);
  if (longValue <= 0) {
    return null;
  }
  return longValue;
}

function isBlank(value) {
  return value == null || String(value).trim() === "";
}

function normalizeExpiresIn(value) {
  var parsed = Number(value);
  if (!Number.isFinite(parsed)) {
    return 0;
  }
  return Math.max(0, Math.trunc(parsed));
}

function resolveAuthToken(authState) {
  var token = "";
  if (authState && authState.token) {
    token = String(authState.token);
  }
  if (!token) {
    token = getStoredToken();
  }
  if (authState) {
    authState.token = token;
  }
  return token;
}

function ensureAuthToken(authState) {
  var token = resolveAuthToken(authState);
  if (!token) {
    if (authState) {
      authState.expiresIn = 0;
    }
    logoutAndRedirect();
    return "";
  }
  return token;
}

function authRequestHeaders(token) {
  return authHeaders(token);
}

function logoutFromState(authState) {
  if (authState) {
    authState.token = "";
    authState.expiresIn = 0;
  }
  logoutAndRedirect();
}

async function loginSubmit(authState, postFn) {
  authState.errors = [];

  var username = authState.username == null ? "" : String(authState.username).trim();
  var password = authState.password == null ? "" : String(authState.password);

  if (!username || !password) {
    authState.errors = ["username and password are required."];
    return false;
  }

  try {
    var response = await postFn("api/login", { username: username, password: password });
    var payload = await parseJsonBody(response);

    if (!response.ok) {
      authState.token = "";
      authState.expiresIn = 0;
      clearStoredAuthToken();
      authState.errors = extractMessages(payload, "Echec de la connexion.");
      return false;
    }

    var token = payload && payload.token ? String(payload.token) : "";
    var expiresIn = payload && payload.expiresIn != null ? normalizeExpiresIn(payload.expiresIn) : 0;

    if (!token) {
      authState.token = "";
      authState.expiresIn = 0;
      clearStoredAuthToken();
      authState.errors = ["Token JWT manquant dans la reponse."];
      return false;
    }

    authState.username = username;
    authState.password = "";
    authState.token = token;
    authState.expiresIn = expiresIn;
    storeAuthToken(token, expiresIn);
    return true;
  } catch (error) {
    authState.token = "";
    authState.expiresIn = 0;
    clearStoredAuthToken();
    authState.errors = ["Erreur reseau pendant la connexion."];
    return false;
  }
}

async function handleLoginSubmit(event, authState, postFn) {
  if (event && typeof event.preventDefault === "function") {
    event.preventDefault();
  }
  var success = await loginSubmit(authState, postFn);
  if (success) {
    window.location.href = "index.html";
  }
  return success;
}

async function loadAnnonceList(authState, listState, getFn) {
  listState.loading = true;
  listState.errors = [];

  var token = ensureAuthToken(authState);
  if (!token) {
    listState.loading = false;
    return false;
  }

  var page = Math.max(0, toInt(listState.page, 0));
  var size = Math.max(1, toInt(listState.size, 5));
  listState.page = page;
  listState.size = size;

  try {
    var response = await getFn(
      "api/annonces?page=" + page + "&size=" + size,
      authRequestHeaders(token)
    );
    var payload = await parseJsonBody(response);

    if (handleUnauthorized(response)) {
      listState.loading = false;
      return false;
    }

    if (!response.ok) {
      listState.items = [];
      listState.totalItems = 0;
      listState.totalPages = 0;
      listState.errors = extractMessages(payload, "Impossible de charger les annonces.");
      listState.loading = false;
      return false;
    }

    listState.items = payload && Array.isArray(payload.items) ? payload.items : [];
    listState.totalItems = payload && payload.totalItems != null ? toInt(payload.totalItems, 0) : 0;
    listState.totalPages = payload && payload.totalPages != null ? toInt(payload.totalPages, 0) : 0;
    listState.loading = false;
    return true;
  } catch (error) {
    listState.items = [];
    listState.totalItems = 0;
    listState.totalPages = 0;
    listState.errors = ["Erreur reseau pendant le chargement des annonces."];
    listState.loading = false;
    return false;
  }
}

async function publishAnnonceFromList(annonceId, authState, listState, postFn) {
  listState.errors = [];
  var token = ensureAuthToken(authState);
  if (!token) {
    return false;
  }

  try {
    var response = await postFn(
      "api/annonces/" + annonceId + "/publish",
      undefined,
      authRequestHeaders(token)
    );
    var payload = await parseJsonBody(response);

    if (handleUnauthorized(response)) {
      return false;
    }

    if (!response.ok) {
      listState.errors = extractMessages(payload, "Impossible de publier.");
      return false;
    }

    return true;
  } catch (error) {
    listState.errors = ["Erreur reseau pendant la publication."];
    return false;
  }
}

async function archiveAnnonceFromList(annonceId, authState, listState, postFn) {
  listState.errors = [];
  var token = ensureAuthToken(authState);
  if (!token) {
    return false;
  }

  try {
    var response = await postFn(
      "api/annonces/" + annonceId + "/archive",
      undefined,
      authRequestHeaders(token)
    );
    var payload = await parseJsonBody(response);

    if (handleUnauthorized(response)) {
      return false;
    }

    if (!response.ok) {
      listState.errors = extractMessages(payload, "Impossible d'archiver.");
      return false;
    }

    return true;
  } catch (error) {
    listState.errors = ["Erreur reseau pendant l'archivage."];
    return false;
  }
}

async function deleteAnnonceFromList(annonceId, authState, listState, delFn) {
  listState.errors = [];
  var token = ensureAuthToken(authState);
  if (!token) {
    return false;
  }

  try {
    var response = await delFn(
      "api/annonces/" + annonceId,
      authRequestHeaders(token)
    );
    var payload = await parseJsonBody(response);

    if (handleUnauthorized(response)) {
      return false;
    }

    if (!response.ok) {
      listState.errors = extractMessages(payload, "Impossible de supprimer.");
      return false;
    }

    return true;
  } catch (error) {
    listState.errors = ["Erreur reseau pendant la suppression."];
    return false;
  }
}

async function loadAnnonceDetail(authState, routeState, detailState, getFn) {
  detailState.loading = true;
  detailState.errors = [];
  detailState.actionErrors = [];
  detailState.success = "";

  var annonceId = toPositiveLong(routeState.id);
  if (!annonceId) {
    detailState.annonce = null;
    detailState.errors = ["id is required"];
    detailState.loading = false;
    return false;
  }

  var token = ensureAuthToken(authState);
  if (!token) {
    detailState.loading = false;
    return false;
  }

  try {
    var response = await getFn(
      "api/annonces/" + annonceId,
      authRequestHeaders(token)
    );
    var payload = await parseJsonBody(response);

    if (handleUnauthorized(response)) {
      detailState.loading = false;
      return false;
    }

    if (!response.ok) {
      detailState.annonce = null;
      detailState.errors = extractMessages(payload, "Impossible de charger le detail.");
      detailState.loading = false;
      return false;
    }

    detailState.annonce = payload;
    detailState.loading = false;
    return true;
  } catch (error) {
    detailState.annonce = null;
    detailState.errors = ["Erreur reseau pendant le chargement du detail."];
    detailState.loading = false;
    return false;
  }
}

async function publishAnnonceFromDetail(authState, detailState, postFn) {
  detailState.actionErrors = [];
  var token = ensureAuthToken(authState);
  if (!token || !detailState.annonce || !detailState.annonce.id) {
    return false;
  }

  try {
    var response = await postFn(
      "api/annonces/" + detailState.annonce.id + "/publish",
      undefined,
      authRequestHeaders(token)
    );
    var payload = await parseJsonBody(response);

    if (handleUnauthorized(response)) {
      return false;
    }

    if (!response.ok) {
      detailState.actionErrors = extractMessages(payload, "Impossible de publier.");
      return false;
    }

    detailState.success = "Annonce publiee.";
    return true;
  } catch (error) {
    detailState.actionErrors = ["Erreur reseau pendant la publication."];
    return false;
  }
}

async function archiveAnnonceFromDetail(authState, detailState, postFn) {
  detailState.actionErrors = [];
  var token = ensureAuthToken(authState);
  if (!token || !detailState.annonce || !detailState.annonce.id) {
    return false;
  }

  try {
    var response = await postFn(
      "api/annonces/" + detailState.annonce.id + "/archive",
      undefined,
      authRequestHeaders(token)
    );
    var payload = await parseJsonBody(response);

    if (handleUnauthorized(response)) {
      return false;
    }

    if (!response.ok) {
      detailState.actionErrors = extractMessages(payload, "Impossible d'archiver.");
      return false;
    }

    detailState.success = "Annonce archivee.";
    return true;
  } catch (error) {
    detailState.actionErrors = ["Erreur reseau pendant l'archivage."];
    return false;
  }
}

async function deleteAnnonceFromDetail(authState, detailState, delFn) {
  detailState.actionErrors = [];
  var token = ensureAuthToken(authState);
  if (!token || !detailState.annonce || !detailState.annonce.id) {
    return false;
  }

  try {
    var response = await delFn(
      "api/annonces/" + detailState.annonce.id,
      authRequestHeaders(token)
    );
    var payload = await parseJsonBody(response);

    if (handleUnauthorized(response)) {
      return false;
    }

    if (!response.ok) {
      detailState.actionErrors = extractMessages(payload, "Impossible de supprimer.");
      return false;
    }

    return true;
  } catch (error) {
    detailState.actionErrors = ["Erreur reseau pendant la suppression."];
    return false;
  }
}

function collectCategoryOptions(items) {
  var map = new Map();
  if (Array.isArray(items)) {
    items.forEach(function (item) {
      if (!item) {
        return;
      }
      var rawId = item.categoryId != null ? item.categoryId : item.id;
      if (rawId == null) {
        return;
      }
      var id = Number(rawId);
      if (!Number.isFinite(id)) {
        return;
      }
      if (!map.has(id)) {
        var rawLabel = item.categoryLabel != null ? item.categoryLabel : item.label;
        var label = rawLabel ? String(rawLabel) : String(id);
        map.set(id, { id: id, label: label });
      }
    });
  }
  return Array.from(map.values()).sort(function (left, right) {
    return left.label.localeCompare(right.label);
  });
}

function ensureCategoryOption(options, id, label) {
  var copied = Array.isArray(options) ? options.slice() : [];
  var numericId = Number(id);
  if (!Number.isFinite(numericId)) {
    return copied;
  }
  var exists = copied.some(function (option) {
    return Number(option.id) === numericId;
  });
  if (!exists) {
    copied.push({
      id: numericId,
      label: label ? String(label) : String(numericId)
    });
  }
  return copied;
}

async function initAnnonceForm(authState, routeState, formState, getFn) {
  formState.loading = true;
  formState.errors = [];

  var token = ensureAuthToken(authState);
  if (!token) {
    formState.loading = false;
    return false;
  }

  try {
    var categoriesResponse = await getFn("api/categories?page=0&size=100", authRequestHeaders(token));
    var categoriesPayload = await parseJsonBody(categoriesResponse);

    if (handleUnauthorized(categoriesResponse)) {
      formState.loading = false;
      return false;
    }

    if (categoriesResponse.ok && categoriesPayload && Array.isArray(categoriesPayload.items)) {
      formState.categoryOptions = collectCategoryOptions(categoriesPayload.items);
    } else {
      formState.categoryOptions = [];
    }

    var annonceId = toPositiveLong(routeState.id);
    if (annonceId) {
      formState.editMode = true;

      var detailResponse = await getFn("api/annonces/" + annonceId, authRequestHeaders(token));
      var detailPayload = await parseJsonBody(detailResponse);

      if (handleUnauthorized(detailResponse)) {
        formState.loading = false;
        return false;
      }

      if (!detailResponse.ok) {
        formState.errors = extractMessages(detailPayload, "Impossible de charger l'annonce a modifier.");
        formState.loading = false;
        return false;
      }

      formState.id = detailPayload.id;
      formState.title = detailPayload.title;
      formState.description = detailPayload.description;
      formState.adress = detailPayload.adress;
      formState.mail = detailPayload.mail;
      formState.status = detailPayload.status;
      formState.version = detailPayload.version;
      formState.categoryId = detailPayload.categoryId == null ? "" : String(detailPayload.categoryId);
      formState.categoryOptions = ensureCategoryOption(
        formState.categoryOptions,
        detailPayload.categoryId,
        detailPayload.categoryLabel
      );
    } else {
      formState.editMode = false;
      formState.id = null;
      formState.title = "";
      formState.description = "";
      formState.adress = "";
      formState.mail = "";
      formState.status = "DRAFT";
      formState.version = 0;
      formState.categoryId = "";
      if (formState.categoryOptions.length > 0) {
        formState.categoryId = String(formState.categoryOptions[0].id);
      }
    }

    formState.loading = false;
    return true;
  } catch (error) {
    formState.errors = ["Erreur reseau pendant l'initialisation du formulaire."];
    formState.loading = false;
    return false;
  }
}

async function submitAnnonceForm(authState, formState, postFn, putFn) {
  formState.errors = [];

  var token = ensureAuthToken(authState);
  if (!token) {
    return "";
  }

  if (isBlank(formState.title)) {
    formState.errors = ["title is required."];
    return "";
  }

  if (isBlank(formState.description)) {
    formState.errors = ["description is required."];
    return "";
  }

  if (isBlank(formState.adress)) {
    formState.errors = ["adress is required."];
    return "";
  }

  if (isBlank(formState.mail)) {
    formState.errors = ["mail is required."];
    return "";
  }

  var categoryId = toPositiveLong(formState.categoryId);
  if (!categoryId) {
    formState.errors = ["categoryId is required."];
    return "";
  }

  var payload = {
    title: formState.title,
    description: formState.description,
    adress: formState.adress,
    mail: formState.mail,
    categoryId: categoryId,
    status: formState.status
  };

  try {
    var response;
    if (formState.editMode) {
      payload.version = toInt(formState.version, 0);
      response = await putFn(
        "api/annonces/" + Number(formState.id),
        payload,
        authRequestHeaders(token)
      );
    } else {
      response = await postFn("api/annonces", payload, authRequestHeaders(token));
    }

    var body = await parseJsonBody(response);

    if (handleUnauthorized(response)) {
      return "";
    }

    if (!response.ok) {
      formState.errors = extractMessages(body, "Enregistrement impossible.");
      return "";
    }

    if (body && body.id != null) {
      return "annonce.html?id=" + body.id;
    }

    if (formState.editMode && formState.id != null) {
      return "annonce.html?id=" + formState.id;
    }

    return "index.html";
  } catch (error) {
    formState.errors = ["Erreur reseau pendant l'enregistrement."];
    return "";
  }
}

async function validateCategoryCreation(authState, categoryFormState, postFn) {
  categoryFormState.errors = [];
  categoryFormState.success = "";

  var token = ensureAuthToken(authState);
  if (!token) {
    return false;
  }

  var label = categoryFormState.label == null ? "" : String(categoryFormState.label).trim();
  if (isBlank(label)) {
    categoryFormState.errors = ["label is required"];
    return false;
  }

  try {
    var response = await postFn(
      "api/categories",
      { label: label },
      authRequestHeaders(token)
    );
    var payload = await parseJsonBody(response);

    if (handleUnauthorized(response)) {
      return false;
    }

    if (!response.ok) {
      categoryFormState.errors = extractMessages(payload, "Impossible de creer la categorie.");
      return false;
    }

    categoryFormState.label = "";
    categoryFormState.success = "Categorie creee.";
    return true;
  } catch (error) {
    categoryFormState.errors = ["Erreur reseau pendant la creation de la categorie."];
    return false;
  }
}

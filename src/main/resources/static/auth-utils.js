const AUTH_TOKEN_STORAGE_KEY = "jwt_token";
const AUTH_EXPIRES_IN_STORAGE_KEY = "jwt_expires_in";

function getStoredToken() {
  try {
    return localStorage.getItem(AUTH_TOKEN_STORAGE_KEY) || "";
  } catch (e) {
    return "";
  }
}

function getStoredExpiresIn() {
  try {
    const rawValue = localStorage.getItem(AUTH_EXPIRES_IN_STORAGE_KEY);
    if (rawValue == null || rawValue === "") {
      return 0;
    }
    const parsed = Number(rawValue);
    return Number.isFinite(parsed) ? Math.trunc(parsed) : 0;
  } catch (e) {
    return 0;
  }
}

function storeAuthToken(token, expiresIn) {
  try {
    if (!token) {
      clearStoredAuthToken();
      return;
    }
    localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, String(token));
    if (expiresIn != null) {
      localStorage.setItem(AUTH_EXPIRES_IN_STORAGE_KEY, String(expiresIn));
    }
  } catch (e) {
    // Ignore storage failures silently to avoid breaking UI flow.
  }
}

function clearStoredAuthToken() {
  try {
    localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY);
    localStorage.removeItem(AUTH_EXPIRES_IN_STORAGE_KEY);
  } catch (e) {
    // Ignore storage failures silently to avoid breaking UI flow.
  }
}

function authHeaders(token) {
  if (!token) {
    return {};
  }
  return { Authorization: "Bearer " + token };
}

function redirectToLogin() {
  window.location.href = "login.html";
}

function requireAuthAndRedirect() {
  const token = getStoredToken();
  if (!token) {
    redirectToLogin();
    return "";
  }
  return token;
}

function logoutAndRedirect() {
  clearStoredAuthToken();
  redirectToLogin();
}

function handleUnauthorized(response) {
  if (response && response.status === 401) {
    logoutAndRedirect();
    return true;
  }
  return false;
}

async function parseJsonBody(response) {
  const text = await response.text();
  if (!text) {
    return null;
  }
  try {
    return JSON.parse(text);
  } catch (e) {
    return { error: "INVALID_JSON", messages: ["Invalid JSON response body."] };
  }
}

function extractMessages(payload, fallbackMessage) {
  if (payload && Array.isArray(payload.messages) && payload.messages.length > 0) {
    return payload.messages;
  }
  if (payload && typeof payload.message === "string" && payload.message.length > 0) {
    return [payload.message];
  }
  return [fallbackMessage];
}

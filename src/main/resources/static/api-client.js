async function request(method, url, body, extraHeaders) {
  var headers = {};
  if (extraHeaders && typeof extraHeaders === "object") {
    Object.assign(headers, extraHeaders);
  }

  var options = {
    method: method,
    headers: headers
  };

  if (body !== undefined) {
    options.body = JSON.stringify(body);
    if (!options.headers["Content-Type"]) {
      options.headers["Content-Type"] = "application/json";
    }
  }

  return fetch(url, options);
}

function get(url, headers) {
  return request("GET", url, undefined, headers);
}

function post(url, body, headers) {
  return request("POST", url, body, headers);
}

function put(url, body, headers) {
  return request("PUT", url, body, headers);
}

function del(url, headers) {
  return request("DELETE", url, undefined, headers);
}

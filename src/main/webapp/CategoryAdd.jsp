<%@ page contentType="text/html; charset=UTF-8" %>
<%
  String error = (String) request.getAttribute("error");
  String success = (String) request.getAttribute("success");
  String labelValue = request.getParameter("label") != null ? request.getParameter("label") : "";
%>
<html>
<head>
  <title>Ajout de categorie</title>
</head>
<body>
  <div>
    <a href="<%= request.getContextPath() %>/annonces">Liste</a> |
    <a href="<%= request.getContextPath() %>/annonces/new">Nouvelle annonce</a> |
    <a href="<%= request.getContextPath() %>/CategoryAdd">Nouvelle categorie</a> |
    <a href="<%= request.getContextPath() %>/logout">Deconnexion</a>
  </div>
  <h1>Nouvelle categorie</h1>
  <% if (error != null) { %>
    <p style="color:red;"><%= error %></p>
  <% } %>
  <% if (success != null) { %>
    <p style="color:green;"><%= success %></p>
  <% } %>

  <form method="post" action="CategoryAdd">
    <label for="label">Libelle :</label>
    <input id="label" name="label" type="text" value="<%= labelValue %>" />
    <br />
    <button type="submit">Enregistrer</button>
  </form>
</body>
</html>

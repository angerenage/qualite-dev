<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="fr.iut.univparis8.arollet.Annonce" %>
<%
  Annonce annonce = (Annonce) request.getAttribute("annonce");
  String error = (String) request.getAttribute("error");
%>
<html>
<head>
  <title>Modifier une annonce</title>
</head>
<body>
  <h1>Modifier une annonce</h1>
  <%
    if (error != null) {
  %>
    <p style="color:red;"><%= error %></p>
  <%
    }
  %>
  <%
    if (annonce != null) {
  %>
  <form method="post" action="AnnonceUpdate">
    <input type="hidden" name="id" value="<%= annonce.getId() %>" />
    <label for="title">Title :</label>
    <input id="title" name="title" type="text" value="<%= annonce.getTitle() %>" />
    <br />
    <label for="description">Description :</label>
    <textarea id="description" name="description"><%= annonce.getDescription() %></textarea>
    <br />
    <label for="adress">Adress :</label>
    <input id="adress" name="adress" type="text" value="<%= annonce.getAdress() %>" />
    <br />
    <label for="mail">Mail :</label>
    <input id="mail" name="mail" type="email" value="<%= annonce.getMail() %>" />
    <br />
    <button type="submit">Enregistrer</button>
  </form>
  <%
    }
  %>
</body>
</html>

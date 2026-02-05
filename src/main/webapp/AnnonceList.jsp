<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="fr.iut.univparis8.arollet.Annonce" %>
<%
  List<Annonce> annonces = (List<Annonce>) request.getAttribute("annonces");
%>
<html>
<head>
  <title>Liste des annonces</title>
</head>
<body>
  <h1>Liste des annonces</h1>
  <%
    if (annonces == null || annonces.isEmpty()) {
  %>
    <p>Aucune annonce.</p>
  <%
    } else {
  %>
    <table border="1" cellspacing="0" cellpadding="6">
      <thead>
        <tr>
          <th>ID</th>
          <th>Title</th>
          <th>Description</th>
          <th>Adress</th>
          <th>Mail</th>
        </tr>
      </thead>
      <tbody>
        <%
          for (Annonce annonce : annonces) {
        %>
          <tr>
            <td><%= annonce.getId() %></td>
            <td><%= annonce.getTitle() %></td>
            <td><%= annonce.getDescription() %></td>
            <td><%= annonce.getAdress() %></td>
            <td><%= annonce.getMail() %></td>
          </tr>
        <%
          }
        %>
      </tbody>
    </table>
  <%
    }
  %>
</body>
</html>

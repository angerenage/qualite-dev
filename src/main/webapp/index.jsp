<html>
<body>
<%
  String name = request.getParameter("name");
%>

<form method="get" action="index.jsp">
  <label for="name">Nom :</label>
  <input id="name" name="name" type="text" />
  <button type="submit">Valider</button>
</form>

<% if (name != null && !name.trim().isEmpty()) { %>
  <h2>Hello the World <%= name %></h2>
<% } %>
</body>
</html>

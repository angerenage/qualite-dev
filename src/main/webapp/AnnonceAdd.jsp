<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
  <title>Ajout d'annonce</title>
</head>
<body>
  <form method="post" action="">
    <label for="title">Title :</label>
    <input id="title" name="title" type="text" />
    <br />
    <label for="description">Description :</label>
    <textarea id="description" name="description"></textarea>
    <br />
    <label for="adress">Adress :</label>
    <input id="adress" name="adress" type="text" />
    <br />
    <label for="mail">Mail :</label>
    <input id="mail" name="mail" type="email" />
    <br />
    <button type="submit">Envoyer</button>
  </form>
</body>
</html>

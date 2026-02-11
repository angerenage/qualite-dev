<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
	<meta charset="UTF-8" />
	<title>Detail annonce</title>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jspf" />
	<h1>Detail annonce</h1>
	<jsp:include page="/WEB-INF/jsp/includes/flash.jspf" />

	<p><strong>ID :</strong> <c:out value="${annonce.id}" /></p>
	<p><strong>Titre :</strong> <c:out value="${annonce.title}" /></p>
	<p><strong>Description :</strong> <c:out value="${annonce.description}" /></p>
	<p><strong>Adresse :</strong> <c:out value="${annonce.adress}" /></p>
	<p><strong>Email :</strong> <c:out value="${annonce.mail}" /></p>
	<p><strong>Statut :</strong> <c:out value="${annonce.status}" /></p>
	<p><strong>Categorie :</strong> <c:out value="${annonce.category.label}" /></p>
	<p><strong>Auteur :</strong> <c:out value="${annonce.author.username}" /></p>
	<p><strong>Date :</strong> <c:out value="${annonce.date}" /></p>

	<a href="${pageContext.request.contextPath}/annonces/edit?id=${annonce.id}">Modifier</a>
	<c:if test="${annonce.status == 'DRAFT'}">
		<form method="post" action="${pageContext.request.contextPath}/annonces/publish?id=${annonce.id}" style="display:inline;">
			<button type="submit">Publier</button>
		</form>
	</c:if>
	<c:if test="${annonce.status == 'ACTIVE'}">
		<form method="post" action="${pageContext.request.contextPath}/annonces/archive?id=${annonce.id}" style="display:inline;">
			<button type="submit">Archiver</button>
		</form>
	</c:if>
	<c:if test="${annonce.status == 'ARCHIVED'}">
		<span>Annonce archivee (lecture seule).</span>
	</c:if>

	<jsp:include page="/WEB-INF/jsp/includes/footer.jspf" />
</body>
</html>

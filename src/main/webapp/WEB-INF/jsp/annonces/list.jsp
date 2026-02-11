<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
	<meta charset="UTF-8" />
	<title>Liste des annonces</title>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jspf" />
	<h1>Liste des annonces</h1>
	<jsp:include page="/WEB-INF/jsp/includes/flash.jspf" />

	<p>Total : <c:out value="${totalItems}" /></p>

	<c:choose>
		<c:when test="${empty annonces}">
			<p>Aucune annonce.</p>
		</c:when>
		<c:otherwise>
			<table border="1" cellspacing="0" cellpadding="6">
				<thead>
					<tr>
						<th>Titre</th>
						<th>Statut</th>
						<th>Date</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${annonces}" var="annonce">
						<tr>
							<td><c:out value="${annonce.title}" /></td>
							<td><c:out value="${annonce.status}" /></td>
							<td><c:out value="${annonce.date}" /></td>
							<td>
								<a href="${pageContext.request.contextPath}/annonces/show?id=${annonce.id}">Details</a>
								|
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
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:otherwise>
	</c:choose>

	<c:if test="${totalPages > 0}">
		<div>
			<c:if test="${currentPage > 1}">
				<a href="${pageContext.request.contextPath}/annonces?page=${currentPage - 1}&size=${pageSize}">Precedent</a>
			</c:if>
			<span>Page <c:out value="${currentPage}" /> / <c:out value="${totalPages}" /></span>
			<c:if test="${currentPage < totalPages}">
				<a href="${pageContext.request.contextPath}/annonces?page=${currentPage + 1}&size=${pageSize}">Suivant</a>
			</c:if>
		</div>
	</c:if>

	<jsp:include page="/WEB-INF/jsp/includes/footer.jspf" />
</body>
</html>

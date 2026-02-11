<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<html>
<head>
	<meta charset="UTF-8" />
	<title>
		<c:choose>
			<c:when test="${editMode}">Modifier une annonce</c:when>
			<c:otherwise>Nouvelle annonce</c:otherwise>
		</c:choose>
	</title>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jspf" />
	<h1>
		<c:choose>
			<c:when test="${editMode}">Modifier une annonce</c:when>
			<c:otherwise>Nouvelle annonce</c:otherwise>
		</c:choose>
	</h1>
	<jsp:include page="/WEB-INF/jsp/includes/flash.jspf" />

	<c:if test="${not empty globalErrors}">
		<div>
			<c:forEach items="${globalErrors}" var="msg">
				<p style="color: red;"><c:out value="${msg}" /></p>
			</c:forEach>
		</div>
	</c:if>

	<c:if test="${not empty fieldErrors.id}">
		<p style="color: red;"><c:out value="${fieldErrors.id}" /></p>
	</c:if>

	<c:choose>
		<c:when test="${editMode}">
			<form method="post" action="${pageContext.request.contextPath}/annonces/update">
				<input type="hidden" name="id" value="${fn:escapeXml(form.id)}" />
				<label for="title">Titre :</label>
				<input id="title" name="title" type="text" value="${fn:escapeXml(form.title)}" />
				<c:if test="${not empty fieldErrors.title}">
					<span style="color: red;"><c:out value="${fieldErrors.title}" /></span>
				</c:if>
				<br />
				<label for="description">Description :</label>
				<textarea id="description" name="description"><c:out value="${form.description}" /></textarea>
				<c:if test="${not empty fieldErrors.description}">
					<span style="color: red;"><c:out value="${fieldErrors.description}" /></span>
				</c:if>
				<br />
				<label for="adress">Adresse :</label>
				<input id="adress" name="adress" type="text" value="${fn:escapeXml(form.adress)}" />
				<c:if test="${not empty fieldErrors.adress}">
					<span style="color: red;"><c:out value="${fieldErrors.adress}" /></span>
				</c:if>
				<br />
				<label for="mail">Email :</label>
				<input id="mail" name="mail" type="email" value="${fn:escapeXml(form.mail)}" />
				<c:if test="${not empty fieldErrors.mail}">
					<span style="color: red;"><c:out value="${fieldErrors.mail}" /></span>
				</c:if>
				<br />
				<label for="categoryId">Categorie :</label>
				<select id="categoryId" name="categoryId">
					<option value="">-- Selectionner --</option>
					<c:forEach items="${categories}" var="category">
						<option value="${category.id}" <c:if test="${form.categoryId == category.id}">selected="selected"</c:if>>
							<c:out value="${category.label}" />
						</option>
					</c:forEach>
				</select>
				<c:if test="${not empty fieldErrors.categoryId}">
					<span style="color: red;"><c:out value="${fieldErrors.categoryId}" /></span>
				</c:if>
				<br />
				<button type="submit">Enregistrer</button>
			</form>
		</c:when>
		<c:otherwise>
			<form method="post" action="${pageContext.request.contextPath}/annonces">
				<label for="title">Titre :</label>
				<input id="title" name="title" type="text" value="${fn:escapeXml(form.title)}" />
				<c:if test="${not empty fieldErrors.title}">
					<span style="color: red;"><c:out value="${fieldErrors.title}" /></span>
				</c:if>
				<br />
				<label for="description">Description :</label>
				<textarea id="description" name="description"><c:out value="${form.description}" /></textarea>
				<c:if test="${not empty fieldErrors.description}">
					<span style="color: red;"><c:out value="${fieldErrors.description}" /></span>
				</c:if>
				<br />
				<label for="adress">Adresse :</label>
				<input id="adress" name="adress" type="text" value="${fn:escapeXml(form.adress)}" />
				<c:if test="${not empty fieldErrors.adress}">
					<span style="color: red;"><c:out value="${fieldErrors.adress}" /></span>
				</c:if>
				<br />
				<label for="mail">Email :</label>
				<input id="mail" name="mail" type="email" value="${fn:escapeXml(form.mail)}" />
				<c:if test="${not empty fieldErrors.mail}">
					<span style="color: red;"><c:out value="${fieldErrors.mail}" /></span>
				</c:if>
				<br />
				<label for="categoryId">Categorie :</label>
				<select id="categoryId" name="categoryId">
					<option value="">-- Selectionner --</option>
					<c:forEach items="${categories}" var="category">
						<option value="${category.id}" <c:if test="${form.categoryId == category.id}">selected="selected"</c:if>>
							<c:out value="${category.label}" />
						</option>
					</c:forEach>
				</select>
				<c:if test="${not empty fieldErrors.categoryId}">
					<span style="color: red;"><c:out value="${fieldErrors.categoryId}" /></span>
				</c:if>
				<br />
				<button type="submit">Enregistrer</button>
			</form>
		</c:otherwise>
	</c:choose>

	<jsp:include page="/WEB-INF/jsp/includes/footer.jspf" />
</body>
</html>

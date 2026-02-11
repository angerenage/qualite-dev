<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<html>
<head>
	<meta charset="UTF-8" />
	<title>Connexion</title>
</head>
<body>
	<h1>Connexion</h1>
	<jsp:include page="/WEB-INF/jsp/includes/flash.jspf" />
	<c:if test="${not empty globalErrors}">
		<div>
			<c:forEach items="${globalErrors}" var="msg">
				<p style="color: red;"><c:out value="${msg}" /></p>
			</c:forEach>
		</div>
	</c:if>
	<form method="post" action="${pageContext.request.contextPath}/login">
		<label for="login">Identifiant (email ou pseudo) :</label>
		<input id="login" name="login" type="text" value="${fn:escapeXml(form.login)}" />
		<c:if test="${not empty fieldErrors.login}">
			<span style="color: red;"><c:out value="${fieldErrors.login}" /></span>
		</c:if>
		<br />
		<label for="password">Mot de passe :</label>
		<input id="password" name="password" type="password" value="${fn:escapeXml(form.password)}" />
		<c:if test="${not empty fieldErrors.password}">
			<span style="color: red;"><c:out value="${fieldErrors.password}" /></span>
		</c:if>
		<br />
		<button type="submit">Se connecter</button>
	</form>
	<jsp:include page="/WEB-INF/jsp/includes/footer.jspf" />
</body>
</html>

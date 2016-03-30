<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8"%>
<style>
.message{
	font-size:20px;
}

</style>

<div class="message">
String to String Map
<hr>
          <c:forEach var="entry" items="${result}">
  <c:out value="${entry.key}"/>= <c:out value="${entry.value}"/><br/>
</c:forEach>
</div>

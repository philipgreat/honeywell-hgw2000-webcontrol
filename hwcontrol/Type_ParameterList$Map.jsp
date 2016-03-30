<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8"%>
<style>
.message{
	font-size:20px;
}
table{
	font-size:20px;
	text-align: left;
	/*border:1px solid #cccccc;*/
}
td{
	font-size:20px;
	text-align: left;
	border:1px solid #cccccc;
}
</style>

<div class="message">
String to String Map
<hr>

<table>
          <c:forEach var="entry" items="${result}">
          <tr>
          <td><c:out value="${entry.key.name}"/></td>
<td><c:forEach var="item" items="${entry.value}"><li><c:out value=""/>${item.value }</li></c:forEach> </td>
</tr>
</c:forEach>
</table>
</div>

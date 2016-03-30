<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8"%>
<style>
.message{
	font-size:20px;
}

</style>

<div class="message">
String List!
<hr/>
          <c:forEach var="item" items="${result}">
            <li>
            ${item}
           </li>
        </c:forEach>
           

  </div>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8"%>
<style>
.message{
	font-size:15px;
	padding-top: 20px;
	text-align: left;
}

</style>

<div class="message">
            <li>用户名: ${result.username}</li>
            <li>密＿码: ${result.password}</li>
            <li>地＿址: ${result.hostIPAddress}</li>
            <li>端＿口: ${result.port}</li>
 
</div>
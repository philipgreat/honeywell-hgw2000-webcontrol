<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8"%>
<style>
.message{
	font-size:15px;
	padding-top: 40px;
	text-align: left;
}

</style>

<div class="message">
            <li>发送到网关的命令: ${result.sentCommand}</li>
            <li>从网关回复的命令: ${result.receivedResponse}</li>
            <li>网关执行时间间隔: ${result.exectionTime}ms</li>
            <li>客户端得到的命令: ${result.webCommand}</li>
			<li>网关执行的错误码: ${result.errorCode}</li>
			
            
</div>
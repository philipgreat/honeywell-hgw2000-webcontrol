<%
   String ip = request.getRemoteAddr();
   session.setAttribute( "ip", ip );
%>
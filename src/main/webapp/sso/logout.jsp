<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
java.util.List<String> host = (java.util.List<String>) request.getAttribute("logout-host");
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>sso logout</title>
</head>
<body>
<%=host%> logout!
</body>
</html>
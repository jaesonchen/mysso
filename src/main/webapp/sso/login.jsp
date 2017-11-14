<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login Page</title>
</head>
<body>
  <form action="/sso/login" method="post">
	  userName: <input type="text" name="userName"/> <br>
	  password: <input type="text" name="password"/> <br>
	  service: <input type="text" name="callback" value="${callback}"/> <br>
	  rememberMe: <input type="checkbox" value="on" name="rememberMe" >
	  <input type="submit" value="submit"/>
  </form>
</body>
</html>
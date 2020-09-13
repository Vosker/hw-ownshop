<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Registration</title>
</head>
<body>
<h1>Please, provide your user details</h1>

<h3 style="color: red">${message}</h3>

<form method="post" action="${pageContext.request.contextPath}/registration">
    name: <input type="text" required="required" name="name">
    Please, provide your login: <input type="text" value="${prevLog}" name="login">
    Please, provide your password: <input type="password" name="psw">
    Please, repeat your password: <input type="password" name="psw-repeat">
    <button type="submit">Register</button>
</form>

<a href="${pageContext.request.contextPath}/">To the main page</a>
</body>
</html>

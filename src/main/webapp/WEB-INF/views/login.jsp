<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Authorization</title>
</head>
<body>
<h1>Please, login to your account</h1>
<br>
<a href="${pageContext.request.contextPath}/registration">Registration</a>
<p>${errorMessage}</p>
<form action="${pageContext.request.contextPath}/login" method="post">
        login: <input type="text" required="required" name="login">
        password: <input type="text" required="required" name="pwd">
    <button type="submit">Ok</button>
</form>
</body>
</html>

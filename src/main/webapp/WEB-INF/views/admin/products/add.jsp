<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add Product to the store</title>
</head>
<br>
<body>
<form method="post" action="${pageContext.request.contextPath}/admin/products/add">
    Product name: <input type="text" required="required" name="name">
    Product price: <input type="number" required="required" name="price">
    <button type="submit">add</button>
</form>
<br><a href="${pageContext.request.contextPath}/admin/products/adminAll">To all products</a>
<br />
<br />
<a href="${pageContext.request.contextPath}/">Return to the main page</a>
</body>
</html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Products in cart</title>
</head>
<body>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Price</th>
    </tr>
    <c:set var="userID" value="${userId}" />
    <c:set var="index" value="0" />
        <c:forEach var="product" items="${products}">
            <tr>
                <td>
                    <c:out value="${index = index + 1}"/>
                </td>
                <td>
                    <c:out value="${product.name}"/>
                </td>
                <td>
                    <c:out value="${product.price}"/>
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}
                    /shopping-cart/remove?index=${index - 1}">Remove product</a>
                </td>
            </tr>
        </c:forEach>
</table>
<br/>
<a href="${pageContext.request.contextPath}/products/all">Add more products</a>
<br/>
<br/>
<form method="post" action="${pageContext.request.contextPath}/orders/add">
    <br/>
    <button type="submit">Confirm order</button>
</form>
<br/>
<a href="${pageContext.request.contextPath}/">To the main page</a>
</body>
</html>

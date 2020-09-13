<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Orders</title>
</head>
<body>
<h1>All orders</h1>
<table border="1">
    <tr>
        <th>ID</th>
    </tr>
    <c:forEach var="order" items="${orders}">
        <tr>
            <td>
                <c:out value="${order.id}"/>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}
                /orders/info?id=${order.id}">Order details</a>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}
                /admin/orders/delete?id=${order.id}">Delete order</a>
            </td>
        </tr>
    </c:forEach>
</table>
<br/>
<br/>
<a href="${pageContext.request.contextPath}/">To the main page</a>
</body>
</html>
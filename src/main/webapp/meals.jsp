<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>Meal list</title>
</head>
<body>
<h2><a href="index.html">Home</a></h2>
<h2>Meals list</h2>

<table border="1">
    <caption>Meals list</caption>
    <tr>
        <th> DateTime </th>
        <th> Description </th>
        <th> Calories </th>
        <th> Exceed </th>
    </tr>
    <c:forEach var="meal" items="${meals}">
        <tr style="${meal.exceed ? "color: red" : "color: green"}">
            <c:set var="cleanedDateTime" value="${fn:replace(meal.dateTime, 'T', ' ')}"/>
            <td>${cleanedDateTime}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td>${meal.exceed}</td>
        </tr>
    </c:forEach>
</table>

</body>
</html>

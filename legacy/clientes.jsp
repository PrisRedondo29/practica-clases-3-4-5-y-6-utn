<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Clientes</title>
</head>
<body>
    <h1>Clientes</h1>

    <%-- Filtro: GET al mismo servlet; el valor de q vuelve en el request --%>
    <form method="get" action="${pageContext.request.contextPath}/clientes">
        <input type="text" name="q" value="${q}" placeholder="Buscar por nombre" />
        <button type="submit">Filtrar</button>
    </form>

    <c:choose>
        <c:when test="${empty clientes}">
            <p>No hay clientes.</p>
        </c:when>
        <c:otherwise>
            <table border="1">
                <tr>
                    <th>Nombre</th>
                    <th>Estado</th>
                    <th>Acción</th>
                </tr>
                <c:forEach var="c" items="${clientes}">
                    <tr>
                        <td><c:out value="${c.nombre}" /></td>
                        <td><c:out value="${c.estado}" /></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/clientes/detalle?id=${c.id}">Abrir</a>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
</body>
</html>

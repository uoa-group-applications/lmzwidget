<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--
    <p>Generated on: <%= new SimpleDateFormat("dd-MM-yyyy|HH:mm:ss").format(new Date())  %></p>
--%>
<c:set var="wrapAttributes" value="" />

<c:forEach items="${widgetDataElements}" var="widgetEntry">
    <c:set var="wrapAttributes" value="${wrapAttributes} data-${widgetEntry.key}=\"${widgetEntry.value}\"" />
</c:forEach>

<div class="widget" ${wrapAttributes}>
    <jsp:include page="${renderPagePath}" />
</div>
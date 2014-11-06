<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.NavigationLinks" scope="request"/>
<nav>
    <ul class="nav nav-sidebar">
        <c:forEach var="item" items="${entityModel.items}">
            <li> <%-- TODO: 'active' class --%>
                <a href="${item.url}" title="${item.alternateText}">${item.linkText}</a>
            </li>
        </c:forEach>
    </ul>
</nav>
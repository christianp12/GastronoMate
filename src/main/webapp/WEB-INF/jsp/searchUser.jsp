<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>My profile</title>
    <!-- External stylesheets for icons and fonts -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css"/>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400&family=Lato:wght@300;400&family=Playfair+Display+SC&display=swap" rel="stylesheet">
    <!-- Bootstrap stylesheet -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"  crossorigin="anonymous"/>
    <!-- Custom stylesheet -->
    <link rel="stylesheet" href="/css/main.css"/>
</head>
<body>

<%--Navbar component--%>
<%@ include file="components/normalNavbar.txt" %>

<main class="container mt-4 text-center" style="margin-bottom: 180px">
        <h1 class="mb-4">Users</h1>
        <div class="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3">
            <c:forEach var="user" items="${users}">
                <div class="card pt-3">
                    <a style="text-decoration: none; color: inherit; cursor: pointer" onclick="userFind('${user.username}')">
                        <c:choose>
                            <c:when test="${!empty user.profilePictureUrl}">
                                <img src="/uploads/${user.profilePictureUrl}" alt="" class="rounded-circle card-img" style="width: 50px; height: 50px;">
                            </c:when>
                            <c:otherwise>
                                <img src="/images/userLogo.svg" alt="" class="rounded-circle card-img" style="width: 50px; height: 50px;">
                            </c:otherwise>
                        </c:choose>
                        <div class="card-body">
                            <span class="ps-2"><c:out value="${user.username}"/></span>
                        </div>
                    </a>
                </div>
            </c:forEach>
        </div>


</main>

<%@ include file="components/footer.txt" %>
<script>

    function userFind(username) {
        let form = document.createElement('form');
        form.method = 'get';
        form.action = '/user/profile';
        let input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'username';
        input.value = username;
        form.appendChild(input);

        //submit form
        document.body.appendChild(form);
        form.submit();
    }
</script>
<!-- External scripts for jQuery, Bootstrap, and custom JavaScript files -->
<script src="https://code.jquery.com/jquery-3.6.4.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
<script src="/js/Main.js"></script>
<script src="/js/StarScript.js"></script>
</body>
</html>



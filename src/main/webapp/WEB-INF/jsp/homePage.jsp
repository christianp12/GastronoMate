<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Homepage</title>
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

<!-- Overlay and modal for displaying error messages -->
<div id="overlay" class="overlay"></div>
<div id="modal" class="myAlert">
    <div id="modalContent" class="myAlertBody">
        <h4>Error</h4>
        <p class="pt-2" id="modalText"></p>
        <button class="btn btn-danger float-end" onclick="closeModal()">Close</button>
    </div>
</div>

<main class="container mt-4" style="margin-bottom: 180px">
    <div class="recipe-cards text-center">
        <h1 class="mb-4">Recipes</h1>
        <!-- Loop through recipe entries -->
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
            <c:forEach var="item" items="${recipes.entries}">
                    <div class="col p-3">
                        <div class="card h-100 custom-card pt-4">
                            <div>
                                <!-- Display recipe author's username-->
                                <h3 style="padding-bottom: 10px">${item.getKey().authorUsername}</h3>
                                <!-- Display recipe author's profile picture -->
                                <c:choose>
                                    <c:when test="${!empty item.getKey().authorProfilePictureUrl}">
                                        <c:choose>
                                            <c:when test="${fn:startsWith(item.getKey().authorProfilePictureUrl, 'http')}">
                                                <a onclick="userFind('${item.getKey().authorUsername}')" style="cursor: pointer">
                                                    <img src="${item.getKey().authorProfilePictureUrl}" class="rounded-circle" style="width: 50px; height: 50px;" alt="">
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a onclick="userFind('${item.getKey().authorUsername}')" style="cursor: pointer">
                                                    <img src="/uploads/${item.getKey().authorProfilePictureUrl}" class="rounded-circle" style="width: 50px; height: 50px;" alt="">
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <a onclick="userFind('${item.getKey().authorUsername}')" style="cursor: pointer">
                                            <img src="/images/userLogo.svg" class="rounded-circle" style="width: 50px; height: 50px;" alt="">
                                        </a>
                                    </c:otherwise>
                                </c:choose>

                            </div>
                            <div class="card-body d-flex flex-column">
                                <!-- Display recipe title -->
                                <h4 style="font-family: 'Playfair Display SC', serif; font-weight: 500;" class="card-title info"><c:out value="${item.getKey().title}" /></h4>

                                <!-- Display recipe image -->
                                <c:choose>
                                    <c:when test="${empty item.getKey().pictureUrl}">
                                        <a href="/recipe/<c:out value="${item.getKey().getRecipeId()}"/>">
                                            <img src="/images/GastronomateMateLogo.svg" class="card-img-top img-fluid p-3 rounded" style="height: 200px; border-radius: 15px" alt="">
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <c:choose>
                                            <c:when test="${fn:startsWith(item.getKey().pictureUrl, 'http')}">
                                                <a href="/recipe/<c:out value="${item.getKey().getRecipeId()}"/>">
                                                    <img src="${item.getKey().pictureUrl}" class="card-img-top img-fluid p-3 rounded" alt="">
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="/recipe/<c:out value="${item.getKey().getRecipeId()}"/>">
                                                    <img src="/uploads/${item.getKey().pictureUrl}" class="card-img-top img-fluid p-3 rounded" alt="">
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>


                                <c:choose>
                                    <c:when test="${item.getValue() == true}">
                                        <img id="like-button${item.getKey().recipeId}" class="pe-2" src="/images/FilledLike.svg" height="40rm" alt="Like button" onclick='homePageToggleLike("<c:out value="${item.getKey().recipeId}"/>")'/>
                                    </c:when>
                                    <c:otherwise>
                                        <img id="like-button${item.getKey().recipeId}" class="pe-2" src="/images/EmptyLike.svg" height="40rm" alt="Like button" onclick='homePageToggleLike("<c:out value="${item.getKey().recipeId}"/>")'/>
                                    </c:otherwise>
                                </c:choose>

                                <%--Recipe like button--%>
                                <span id="recipeLiked${item.getKey().recipeId}" class="d-none"><c:out value="${item.getValue()}"/></span>


                                <!-- Display up to 3 keywords for the recipe -->
                                <div class="list-group mt-3  list-group-horizontal">
                                    <c:forEach var="keyword" items="${item.getKey().keywords}" varStatus="status">
                                        <c:if test="${status.index < 3}">
                                            <a class="list-group-item list-group-item-action text-center p-1" href="/search/${keyword}">${keyword}</a>
                                        </c:if>
                                    </c:forEach>
                                </div>

                            </div>
                        </div>
                    </div>
            </c:forEach>
        </div>

        <!-- Pagination buttons -->
        <div class="d-flex justify-content-between position-fixed bottom-0 end-0" style="margin-bottom: 120px; margin-right: 80px">
            <div class="d-flex gap-2">
                <!-- Left arrow to decrease the page -->
                <button class="btn custom-btn-color" onclick="prevPage(${recipes.currentPage})">
                    &lt; Previous
                </button>
                <!-- Right arrow to increase the page -->
                <button class="btn custom-btn-color" onclick="nextPage(${recipes.currentPage}, ${recipes.numberOfPages})">
                    Next &gt;
                </button>
            </div>
        </div>

    </div>

</main>

<!-- Included footer component -->
<%@ include file="components/footer.txt"%>

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

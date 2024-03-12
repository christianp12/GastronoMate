<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Profile: ${user.username}</title>
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

<div id="overlay" class="overlay"></div>
<div id="modal" class="myAlert" style="display: none">
    <div id="modalContent" class="myAlertBody">
        <p class="pt-2" id="modalText"></p>
        <button class="btn btn-danger float-end" onclick="closeModal()">Close</button>
    </div>
</div>

<main class="container mt-4" style="margin-bottom: 150px">
    <div class="container p-2" style="background-color: var(--light-green); border-radius: 20px">
        <div class="row p-3">
            <div class="col-3 align-items-center column-gap-3 gap-5">
                <h2><c:out value="${user.username}"/></h2>

                <c:choose>
                    <c:when test="${isFollowed}">
                        <button id="followBtn" class="btn custom-btn-color" onclick="FollowUser('${user.username}')">Unfollow</button>
                    </c:when>
                    <c:otherwise>
                        <button id="followBtn" class="btn custom-btn-color" onclick="FollowUser('${user.username}')">Follow</button>
                    </c:otherwise>
                </c:choose>

            </div>
            <div class="col column-gap-3 pe-0 text-end ms-3">
                <a class="btn custom-btn-color" href="#recipes">Recipes: ${user.recipes.size()}</a>
                <button class="btn custom-btn-color" onclick="findFollowers('${user.username}')" >Followers: ${user.followers}</button>
                <button class="btn custom-btn-color" onclick="findFollowed('${user.username}')">Followed: ${user.followed}</button>
            </div>
        </div>

        <div class="row pb-3">
            <div class="col-1 text-start">

                <%----%>
                    <c:choose>
                        <c:when test="${!empty user.profilePictureUrl }">
                            <c:choose>
                                <c:when test="${fn:startsWith(user.profilePictureUrl, 'http')}">
                                    <div class="flex-grow-1">
                                        <img src="${user.profilePictureUrl}" class="card-img-top img-fluid" style="border-radius: 15px" alt="">
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="flex-grow-1">
                                        <img src="/uploads/${user.profilePictureUrl}" class="card-img-top rounded-circle" style="margin-left: 15px; width: 100px; height: 100px;" alt="">
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <img src="/images/userLogo.svg" alt="" class="rounded-circle" style="margin-left: 15px; width: 100px; height: 100px;">
                        </c:otherwise>
                    </c:choose>
                <%----%>

            </div>
            <div class="col-6 ms-5 text-start">
                <c:if test="${!empty user.description}">
                    <h5 class="">Description</h5>
                    <p>
                        <c:out value="${user.description}"/>
                    </p>
                </c:if>
            </div>
        </div>
    </div>


    <div class="container-fluid" style="margin-top: 80px">
        <div class="row">
            <div class="col">
                <h2>Recipes</h2>
                <div id="recipes" class="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3">
                    <c:forEach var="recipe" items="${user.recipes}">
                        <div class="col p-3">
                            <div class="card custom-card h-100">
                                <div class="card-body d-flex flex-column">
                                    <a href="/recipe/${recipe.recipeId}" style="text-decoration: none; color: black">
                                        <h4 style="font-family: 'Playfair Display SC', serif; font-weight: 500;" class="card-title info"><c:out value="${recipe.title}" /></h4>
                                        <c:choose>
                                            <c:when test="${!empty recipe.pictureUrl}">
                                                <c:choose>
                                                    <c:when test="${fn:startsWith(recipe.pictureUrl, 'http')}">
                                                        <div class="flex-grow-1">
                                                            <img src="${recipe.pictureUrl}" class="card-img-top img-fluid" style="object-fit: cover; border-radius: 15px; max-height: 150px" alt="">
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="flex-grow-1">
                                                            <img src="/uploads/${recipe.pictureUrl}" class="card-img-top img-fluid" style="object-fit: cover; border-radius: 15px;" alt="">
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="flex-grow-1">
                                                    <img src="/images/GastronomateMateLogo.svg" class="card-img-top img-fluid" style="object-fit: cover; border-radius: 15px" alt="">
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </a>
                                    <div class="list-group mt-3  list-group-horizontal">
                                        <c:forEach var="keyword" items="${recipe.keywords}" varStatus="status">
                                            <c:if test="${status.index < 4}">
                                                <a class="list-group-item list-group-item-action text-center" href="/search/${keyword}">${keyword}</a>
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

            </div>
        </div>
    </div>

</main>

<%@ include file="components/footer.txt" %>

<script>
    let isFollowed = ${isFollowed};
    let username = '${user.username}';

    function FollowUser(username) {
        if (isFollowed === false) {
            fetch("/user/" + username + "/follow", { method: 'GET' })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Error occurred during following, please try again later.");
                    }
                    return response;
                })
                .then(() => {
                    document.getElementById('followBtn').textContent = 'Unfollow';
                    isFollowed = true;

                })
                .catch(error => {
                    displayModal(error.message);
                });

        } else {
            fetch("/user/" + username + "/unfollow", { method: 'GET' })
                .then(response => {

                    if (!response.ok) {
                        throw new Error("Error occurred during unfollowing please try again later.");
                    }
                    return response;
                })
                .then(() => {

                    document.getElementById('followBtn').textContent = 'Follow';
                    isFollowed = false;

                })
                .catch(error => {
                    displayModal(error.message);
                });
        }
    }

    function findFollowers(username) {
        //change the url to the user's followers
        //if the username contains a space at the end, replace it with %20
        if (username.endsWith(' ')) {
            username = username.slice(0, -1) + '%20';
        }
        window.location.href = "/user/" + username + "/followers";
    }
    function findFollowed(username) {
        //change the url to the user's followers
        //if the username contains a space at the end, replace it with %20
        if (username.endsWith(' ')) {
            username = username.slice(0, -1) + '%20';
        }
        window.location.href = "/user/" + username + "/followed";
    }

</script>

<!-- External scripts for jQuery, Bootstrap, and custom JavaScript files -->
<script src="https://code.jquery.com/jquery-3.6.4.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
<script src="/js/Main.js"></script>
<script src="/js/StarScript.js"></script>
</body>
</html>

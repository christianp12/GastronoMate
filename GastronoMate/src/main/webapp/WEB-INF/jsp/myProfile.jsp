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

    <style>
        .loader {
            border: 10px solid #f3f3f3;
            border-radius: 50%;
            border-top: 10px solid var(--green);
            width: 80px;
            height: 80px;
            animation: spin 2s linear infinite;
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    </style>

</head>
<body>

<%--Navbar component--%>
<%@ include file="components/normalNavbar.txt" %>


<!-- Overlay and modal for displaying messages -->
<div id="overlay" class="overlay"></div>
<div id="modal" class="myAlert">
    <div id="modalContent" class="myAlertBody">
        <p class="pt-2" id="modalText"></p>
        <button class="btn btn-danger float-end" onclick="closeModal()">Close</button>
    </div>
</div>

<!-- Overlay and modal for deleting account -->
<div id="deleteModal" class="myAlert">
    <div id="deleteModalContent" class="myAlertBody">
        <h4>Input error</h4>
        <p class="pt-2" id="deleteModalText"></p>
        <div class="d-flex gap-3">
            <a href="/user/myProfile/delete" class="btn custom-btn-color float-end">Delete account</a>
            <button class="btn btn-danger float-end" onclick="closeDeleteModal()">Close</button>
        </div>
    </div>
</div>


<main class="container mt-4 min-vh-100" style="margin-bottom: 120px">
    <div class="container" style="background-color: var(--light-green); border-radius: 20px">
        <div class="row p-3">
            <div class="col-12 d-flex align-items-center gap-1">
                <p class="medium-title"><c:out value="${user.username}"/>
                <a class="ms-2 fs-5" href="/user/myProfile/edit"><img src="/images/pencil.png" width="30px" alt=""></a>
                <img  onclick="displayDeleteModal('Are you sure to delete your account?')" src="/images/trash-bin.png" width="30px" alt="">
            </div>
            <div class="pt-2 col-6">
                <p class="small-title"><c:out value="${user.fullName}"/></p>
                <p class="smaller-title"><c:out value="${user.email}"/></p>
                <p class="smaller-title"><c:out value="${user.address.city}"/> <c:out value=" ${user.address.state}"/> <c:out value=" ${user.address.country}"/></p>
            </div>
            <div class="col-6 pe-0 text-end ">
                <a class="btn custom-btn-color mx-1" href="#recipes">Recipes: ${user.recipes.size()}</a>
                <a class="btn custom-btn-color mx-1" href="/user/${user.username}/followers">Followers: ${user.followers}</a>
                <a class="btn custom-btn-color mx-1" href="/user/${user.username}/followed">Followed: ${user.followed}</a>
            </div>
        </div>

        <div class="row pb-3">
            <div class="col-1 text-start">
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
            </div>

            <div class="col-6 ms-5 text-start">
                <c:if test="${!empty user.description }">
                    <h5 class="small-title">Description</h5>
                    <p><c:out value="${user.description}"/></p>
                </c:if>
            </div>
        </div>
    </div>

    <div class="container-fluid" style="margin-top: 80px">
        <div class="row">
            <div class="col-md-1"></div>
            <div class="col-md-7 me-3">
                <h2 id="recipes">Recipes</h2>
                <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 me-5">
                    <c:forEach var="recipe" items="${user.recipes}">
                        <div class="col p-3">
                            <div class="card custom-card h-100">
                                <div class="card-body d-flex flex-column">
                                    <a href="/recipe/${recipe.recipeId}" style="text-decoration: none; color: black">
                                        <h4 style="font-family: 'Playfair Display SC', serif; font-weight: 500;" class="card-title info"><c:out value="${recipe.title}"/></h4>
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
                                                            <img src="/uploads/${recipe.pictureUrl}" class="card-img-top img-fluid" style="object-fit: cover; border-radius: 15px; max-height: 150px" alt="">
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
                                            <c:if test="${status.index < 3}">
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
            <div class="col-md-3">

                <h2>Suggested users</h2>
                <button class="btn custom-btn-color" id="suggestedUserButton" onclick="showSuggestedUser()">Show</button>
                <div id="loader1" class="loader mx-auto mt-5" style="display: none"></div>
                <ul id="listSuggestedUser" class="list-group pt-3" style="display: none"></ul>

                <h2 class="mt-5 pt-4">Suggested recipes</h2>
                <button class="btn custom-btn-color" id="suggestedRecipeButton" onclick="showSuggestedRecipe()">Show</button>
                <div id="loader2" class="loader mx-auto mt-5" style="display: none"></div>
                <ul id="listSuggestedRecipes" class="list-group pt-3" style="display: none"></ul>

            </div>

            <div class="col-md-1"></div>

        </div>
    </div>

</main>

<div style="height: 200px"></div>

<%@ include file="components/footer.txt" %>

<script>
    window.addEventListener('load', () => {

        let message = "${errorMessage}";
        if(message !== "" && message !== null && message !== undefined) {
            displayModal(message);
        }

        retriveUserSuggestion();
        retriveRecipeSuggestion();

    });

     function retriveUserSuggestion(){

         let suggestedUserUrl = "/user/suggestedUsers";

         fetch(suggestedUserUrl, { method: 'GET' })

             .then(response => {
                 if (!response.ok) {
                     throw new Error("Error occurred during retrieving suggestions, please try again later.");
                 }

                 return response.json();
             })
             .then(data => {

                 let listSuggestedUser = document.getElementById("listSuggestedUser");

                 data.forEach(suggestedUser => {
                     let li = document.createElement("li");
                     li.classList.add("list-group-item");
                     let a = document.createElement("a");
                     a.onclick = function() { userFind(suggestedUser.username) };
                     a.style.textDecoration = "none";
                     a.style.color = "inherit";
                     let div = document.createElement("div");
                     div.classList.add("d-flex","gap-2");
                     let img = document.createElement("img");
                     if(suggestedUser.profilePictureUrl !== null && suggestedUser.profilePictureUrl !== undefined && suggestedUser.profilePictureUrl !== ""){
                         if (suggestedUser.profilePictureUrl.startsWith("http")) {
                             img.src = suggestedUser.profilePictureUrl;
                         } else {
                             img.src = "/uploads/" + suggestedUser.profilePictureUrl;
                         }
                     } else {
                         img.src = "/images/userLogo.svg";
                     }
                     img.alt = "";
                     img.classList.add("rounded-circle");
                     img.style.width = "50px";
                     img.style.height = "50px";
                     let span = document.createElement("span");
                     span.innerText = suggestedUser.username;

                     div.appendChild(img);
                     div.appendChild(span);
                     a.appendChild(div);
                     li.appendChild(a);
                     listSuggestedUser.appendChild(li);
                 });

                 //stop the loader if it is still running
                 if(document.getElementById("loader1").style.display === "block"){
                     document.getElementById("loader1").style.display = "none";
                     listSuggestedUser.style.display = "block";
                 }

             })
             .catch(error => {
                 console.log(error);
             });

     }

    function retriveRecipeSuggestion(){

        let suggestedRecipeUrl = "/recipe/suggestedRecipes";

        fetch(suggestedRecipeUrl, { method: 'GET' })

            .then(response => {
                if (!response.ok) {
                    throw new Error("Error occurred during retrieving suggestions, please try again later.");
                }

                return response.json();  // Leggi il corpo della risposta come JSON
            })
            .then(data => {

                let listSuggestedRecipe = document.getElementById("listSuggestedRecipes");

                data.forEach(suggestedRecipe => {
                    let li = document.createElement("li");
                    li.classList.add("list-group-item");
                    let a = document.createElement("a");
                    a.href = "/recipe/" + suggestedRecipe.recipeId;
                    a.style.textDecoration = "none";
                    a.style.color = "inherit";
                    let div = document.createElement("div");
                    div.classList.add("d-flex","gap-2");
                    let img = document.createElement("img");
                    if(suggestedRecipe.pictureUrl !== null && suggestedRecipe.pictureUrl !== undefined && suggestedRecipe.pictureUrl !== ""){
                        if (suggestedRecipe.pictureUrl.startsWith("http")) {
                            img.src = suggestedRecipe.pictureUrl;
                        } else {
                            img.src = "/uploads/" + suggestedRecipe.pictureUrl;
                        }
                    } else {
                        img.src = "/images/GastronomateMateLogo.svg";
                    }
                    img.alt = "";
                    img.classList.add("rounded-circle");
                    img.style.width = "50px";
                    img.style.height = "50px";
                    let span = document.createElement("span");
                    span.innerText = suggestedRecipe.title;

                    div.appendChild(img);
                    div.appendChild(span);
                    a.appendChild(div);
                    li.appendChild(a);
                    listSuggestedRecipe.appendChild(li);
                });

                //stop the loader if it is still running
                if(document.getElementById("loader2").style.display === "block"){
                    document.getElementById("loader2").style.display = "none";
                    listSuggestedRecipe.style.display = "block";
                }

            })
            .catch(error => {
                console.log(error);
            });
    }

    function showSuggestedUser(){
         //hide the button
        document.getElementById("suggestedUserButton").style.display = "none";

        let suggestedUser = document.getElementById("listSuggestedUser");

        //while the list is empty, do nothing keep the loader
        if (suggestedUser.childElementCount === 0){
            document.getElementById("loader1").style.display = "block";
        }
        else
            //show the list
            suggestedUser.style.display = "block";
    }

    function showSuggestedRecipe(){
        //hide the button
        document.getElementById("suggestedRecipeButton").style.display = "none";

        let listSuggestedRecipe = document.getElementById("listSuggestedRecipes");

        //while the list is empty, do nothing keep the loader
        if (listSuggestedRecipe.childElementCount === 0){
            document.getElementById("loader2").style.display = "block";
        }
        else
            //show the list
            listSuggestedRecipe.style.display = "block";
    }

    function userFind(username){
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



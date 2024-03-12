
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="recipe" scope="request" type="javafx.util.Pair"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Recipe details</title>
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
        <h4 id="modalTitle">Error</h4>
        <p class="pt-2" id="modalText"></p>
        <button class="btn btn-danger float-end" onclick="closeModal()">Close</button>
    </div>
</div>

<c:if test="${recipe.getKey().authorUsername.equals(loggedUser.username)}">
    <%-- Modal for deleting a recipe --%>
    <div id="deleteModal" class="myAlert">
        <div id="deleteModalContent" class="myAlertBody">
            <h4>Delete recipe</h4>
            <p class="pt-2" id="deleteModalText"></p>
            <div class="d-flex gap-3">
                <a href="/recipe/${recipe.getKey().recipeId}/delete" class="btn custom-btn-color float-end">Delete recipe</a>
                <button class="btn btn-danger float-end" onclick="closeDeleteModal()">Close</button>
            </div>
        </div>
    </div>
</c:if>

<%-- Modal for adding a review --%>
<div id="reviewModal" class="reviewModal p-5">
    <h3 class="p-2">Add a review</h3>
    <form class="p-2" action="/review/add" method="post" id="addReview">
        <label for="reviewBody" class="form-label my-1">Review body</label>
        <textarea class="form-control" name="reviewText" id="reviewBody" style="resize:none; height: 120px" title="Only letters, numbers, and spaces are allowed"></textarea>
        <div class="row align-items-center">
            <div class="col pt-3 pb-2">
                <div id="review-star-rating-score">
                    <img onclick="setRating(1)" src="/images/EmptyStar.svg" class="star" id="reviewStar1" alt=""/>
                    <img onclick="setRating(2)" src="/images/EmptyStar.svg" class="star" id="reviewStar2" alt=""/>
                    <img onclick="setRating(3)" src="/images/EmptyStar.svg" class="star" id="reviewStar3" alt=""/>
                    <img onclick="setRating(4)" src="/images/EmptyStar.svg" class="star" id="reviewStar4" alt=""/>
                    <img onclick="setRating(5)" src="/images/EmptyStar.svg" class="star" id="reviewStar5" alt=""/>
                </div>
            </div>
        </div>
        <input type="hidden" id="ratingInput" name="reviewRating">
        <input type="hidden" name="title" value="${recipe.getKey().title}">
        <input type="hidden" name="recipeId" value="${recipe.getKey().recipeId}">
        <input type="hidden" name="recipeAuthorUsername" value="${recipe.getKey().authorUsername}">
        <input type="hidden" name="recipeAuthorProfilePictureUrl" value="${recipe.getKey().authorProfilePictureUrl}">
        <div class="mt-2 container">
            <a class="btn custom-btn-color" onclick="submitReviewForm()">Add review</a>
            <a class="btn btn-danger float-end" onclick="closeReviewModal()">Close</a>
        </div>
    </form>
</div>


<main id="main_container" class="container pt-5 mt-5" style="min-height: 80vh; margin-bottom: 200px">
    <div class="container-fluid">

        <div class="col-12 d-flex align-items-center gap-1">
            <span class="big-title"><c:out value="${recipe.getKey().title}"/></span>
            <c:if test="${recipe.getKey().authorUsername.equals(loggedUser.username)}">
                <a class="ms-2 fs-5" href="/recipe/${recipe.getKey().recipeId}/edit"><img src="/images/pencil.png" width="30px" alt=""></a>
                <img  onclick="displayDeleteModal('Are you sure to delete your recipe?')" src="/images/trash-bin.png" width="30px" alt="">
            </c:if>

        </div>


        <div class="row align-items-center">
            <div class="col pt-3 pb-2">
                <div id="star-rating-score">
                    <img src="/images/EmptyStar.svg" class="star" id="star1" />
                    <img src="/images/EmptyStar.svg" class="star" id="star2" />
                    <img src="/images/EmptyStar.svg" class="star" id="star3" />
                    <img src="/images/EmptyStar.svg" class="star" id="star4" />
                    <img src="/images/EmptyStar.svg" class="star" id="star5" />
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col">
                <div class="card mt-2 custom-card">
                    <div class="card-body">
                        <p class="card-title display-6 small-title">Description</p>
                        <p class="card-text lead">
                            <c:out value="${recipe.getKey().description}" />
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <%--    Start image and info --%>
        <div class="row mt-3">
            <div class="col-md-6 d-flex justify-content-start">

                <c:choose>
                    <c:when test="${empty recipe.key.pictureUrl}">
                        <img src="/images/GastronomateMateLogo.svg" style="border-radius: 15px; padding-left: 40px; padding-top: 20px;" alt="Image"/>
                    </c:when>
                    <c:otherwise>

                        <c:choose>
                            <c:when test="${fn:startsWith(recipe.key.pictureUrl, 'http')}">
                                <div class="flex-grow-1">
                                    <img src="${recipe.key.pictureUrl}" class="card-img-top img-fluid" style="object-fit: cover; border-radius: 15px" alt="">
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="flex-grow-1">
                                    <img src="/uploads/${recipe.key.pictureUrl}" class="card-img-top img-fluid" style="object-fit: cover; border-radius: 15px; height: 550px; width: 550px " alt="">
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="col-md-6 d-flex justify-content-center">
                <div class="container">
                    <div class="row pt-3">
                        <div class="col-lg-6 col-md-12">

                            <c:choose>
                                <%--if the user is the author of the recipe, the profile picture will be a link to the own user profile--%>
                                <c:when test="${recipe.key.authorUsername.equals(loggedUser.username)}">

                                    <%--if the author profile picture is empty, the image will be the default user logo--%>
                                    <c:choose>
                                        <c:when test="${empty recipe.key.authorProfilePictureUrl}">
                                            <a onclick="window.location.href = '/user/myProfile'"><img src="/images/userLogo.svg" class="me-2 mb-2" height="40" alt="User logo" style="border-radius: 25px"></a>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <%--if the picture starts with http means it's from the original database--%>
                                                <c:when test="${fn:startsWith(recipe.key.authorProfilePictureUrl, 'http')}">
                                                    <a onclick="window.location.href = '/user/myProfile'">
                                                        <img src="${recipe.key.authorProfilePictureUrl}" class="me-2 mb-2" height="40" alt="User logo"  style="border-radius: 25px">
                                                        <p class="d-inline-block custom-text small-title"><c:out value="${recipe.getKey().authorUsername}"/></p>
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a onclick="window.location.href = '/user/myProfile'">
                                                        <img src="/uploads/${recipe.key.authorProfilePictureUrl}" class="me-2 mb-2" height="40" alt="User logo"  style="border-radius: 25px">
                                                        <p class="d-inline-block custom-text small-title"><c:out value="${recipe.getKey().authorUsername}"/></p>
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>

                                </c:when>

                                <%-- if the user is not the author of the recipe, the profile picture will be a link to the author's profile--%>
                                <c:otherwise>

                                    <%--if the author profile picture is empty, the image will be the default user logo--%>
                                    <c:choose>
                                        <c:when test="${empty recipe.key.authorProfilePictureUrl}">
                                            <a onclick="userFind('${recipe.key.authorUsername}')">
                                                <img src="/images/userLogo.svg" class="pe-2 pb-2" height="40" alt="User logo" style="border-radius: 25px">
                                                <p class="d-inline-block custom-text small-title"><c:out value="${recipe.getKey().authorUsername}"/></p>
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <%--if the picture starts with http means it's from the original database--%>
                                                <c:when test="${fn:startsWith(recipe.key.authorProfilePictureUrl, 'http')}">
                                                    <a onclick="userFind('${recipe.key.authorUsername}')">
                                                        <img src="${recipe.key.authorProfilePictureUrl}" class="me-2 mb-2" height="40" alt="User logo" style="border-radius: 25px">
                                                        <p class="d-inline-block custom-text small-title"><c:out value="${recipe.getKey().authorUsername}"/></p>
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a onclick="userFind('${recipe.key.authorUsername}')">
                                                        <img src="/uploads/${recipe.key.authorProfilePictureUrl}" class="me-2 mb-2" height="40" alt="User logo"  style="border-radius: 25px">
                                                        <p class="d-inline-block custom-text small-title"><c:out value="${recipe.getKey().authorUsername}"/></p>
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>

                        </div>
                        <div class="col-12 text-end">
                            <span class="me-3 fw-bold">Likes: ${recipe.getKey().likes}</span>
                            <span class="me-5 fw-bold">Reviews: ${recipe.getKey().reviews}</span>
                            <c:choose>
                                <c:when test="${recipe.getValue()}">
                                    <img id="like-button${recipe.getKey().recipeId}" class="pe-2" src="/images/FilledLike.svg" height="40rm" alt="Like button" onclick='recipeToggleLike("<c:out value="${recipe.getKey().recipeId}"/>")'/>
                                </c:when>
                                <c:otherwise>
                                    <img id="like-button${recipe.getKey().recipeId}" class="pe-2" src="/images/EmptyLike.svg" height="40rm" alt="Like button" onclick='recipeToggleLike("<c:out value="${recipe.getKey().recipeId}"/>")'/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="row pb-3">
                        <div class="card mt-2 custom-card">
                            <h4 class="card-title pt-2 ps-2">Ingredients</h4>
                            <p class="card-text ps-2 pb-3 pe-1">
                                <c:forEach var="ingredient" items="${recipe.getKey().ingredients}">
                                    <c:out value="${ingredient}" />
                                </c:forEach>
                            </p>
                        </div>
                    </div>

                    <c:if test="${!empty recipe.getKey().recipeServings}">
                        <div class="row pb-3">
                            <div class="card mt-2 custom-card">
                                <h4 class="card-title small-title pt-2 ps-2">Servings</h4>
                                <p class="card-text ps-2 pb-3 pe-1">
                                    <c:out value="${recipe.getKey().recipeServings}"/>
                                </p>
                            </div>
                        </div>
                    </c:if>



                    <div class="row py-3">

                        <c:if test="${!recipe.getKey().areEmptyNutritionFields()}">
                            <div class="card mt-2 custom-card">
                                <h4 class="card-title pt-2 ps-2">Nutritional values</h4>
                                <p class="card-text ps-2 pb-3 pe-1">

                                    <c:if test="${!empty recipe.getKey().calories}">
                                        Calories: <c:out value="${recipe.getKey().calories}"/>,
                                    </c:if>

                                    <c:if test="${!empty recipe.getKey().fatContent}">
                                        Fat content: <c:out value="${recipe.getKey().fatContent}"/>,
                                    </c:if>

                                    <c:if test="${!empty recipe.getKey().saturatedFatContent}">
                                        Saturated Fat content: <c:out value="${recipe.getKey().saturatedFatContent}"/>,
                                    </c:if>

                                    <c:if test="${!empty recipe.getKey().sodiumContent}">
                                        Sodium content: <c:out value="${recipe.getKey().sodiumContent}" />,
                                    </c:if>

                                    <c:if test="${!empty recipe.getKey().carbohydrateContent}">
                                        Carbohydrate content: <c:out value="${recipe.getKey().carbohydrateContent}" />,
                                    </c:if>

                                    <c:if test="${!empty recipe.getKey().fiberContent}">
                                        Fiber content: <c:out value="${recipe.getKey().fiberContent}" />,
                                    </c:if>

                                    <c:if test="${!empty recipe.getKey().sugarContent}">
                                        Sugar content: <c:out value="${recipe.getKey().sugarContent}" />,
                                    </c:if>

                                    <c:if test="${!empty recipe.getKey().proteinContent}">
                                        Protein content: <c:out value="${recipe.getKey().proteinContent}" />,
                                    </c:if>

                                    <c:if test="${!empty recipe.getKey().recipeServings}">
                                        Recipe servings: <c:out value="${recipe.getKey().recipeServings}" />
                                    </c:if>

                                </p>
                            </div>
                            </div>
                        </c:if>
                </div>
            </div>
        </div>
        <div class="row mt-5 mb-3">
            <div class="col">
                <div class="row">
                    <div class="time-div">
                        <c:if test="${!empty recipe.getKey().prepTime}">
                            <img class="me-2 mb-3" src="/images/clock.svg" height="18rm" alt=""/>
                            <p class="small-title">Prep time: ${recipe.getKey().prepTime}</p>
                        </c:if>
                    </div>

                </div>
                <div class="row">
                    <div class="time-div">
                        <c:if test="${!empty recipe.getKey().cookTime}">
                            <img class="me-2 mb-3" src="/images/clock.svg" height="18rm" alt=""/>
                            <p class="small-title">Cook time: ${recipe.getKey().cookTime}</p>
                        </c:if>
                    </div>
                </div>
                <div class="row">
                    <div class="time-div">
                        <c:if test="${!empty recipe.getKey().totalTime}">
                            <img class="me-2 mb-3" src="/images/clock.svg" height="18rm" alt=""/>
                            <p class="small-title">Total time: ${recipe.getKey().totalTime}</p>
                        </c:if>
                    </div>
                </div>
            </div>
            <div class="col mb-3">
                <div class="row align-items-center">
                    <h4 class="col-12 mb-3">Keywords</h4>

                    <c:if test="${!empty recipe.getKey().keywords}">
                        <c:forEach var="keyword" items="${recipe.getKey().keywords}">
                            <div class="col col-md-auto py-1 px-1">
                                <a href="/search/${keyword}" class="btn custom-btn-color w-100">${keyword}</a>
                            </div>
                        </c:forEach>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Start reviews -->
        <div class="col column-gap-3 pe-0 text-start g-3">
            <a class="btn custom-btn-color mx-1" href="/recipe/${recipe.getKey().recipeId}/reviews/1">Show reviews</a>
            <a class="btn custom-btn-color2 mx-1" onclick="checkIfReviewed('${recipe.getKey().recipeId}')">Add a review</a>
        </div>

    </div>

</main>



<script>

    function checkIfReviewed(recipeId) {
        fetch("/recipe/" + recipeId + "/reviews", { method: 'GET' })
            .then(response => {
                if (!response.status === 200) {
                    throw new Error("Error occurred during checking if reviewed, please try again later.");
                }
                return response.json();
            })
            .then(data => {
                if (data === true) {
                    displayModal_with_Title("Warning", "You have already reviewed this recipe.");
                } else {
                    displayReviewModal();
                }
            })
            .catch(error => {
                console.log(error);
            });
    }

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

    function displayReviewModal() {
        document.getElementById('overlay').style.display = 'block';
        document.getElementById('reviewModal').style.display = 'block';
    }

    function closeReviewModal(){
        document.getElementById('overlay').style.display = 'none';
        document.getElementById('reviewModal').style.display = 'none';
    }

    function setRating(i) {
        reviewRating = i;
        console.log('Rating is now: ' + reviewRating);
        for (let j = 1; j <= 5; j++) {
            document.getElementById('reviewStar' + j).src = "/images/EmptyStar.svg";
        }
        for (let j = 1; j <= reviewRating; j++) {
            document.getElementById('reviewStar' + j).src = "/images/FullStar.svg";
        }
        document.getElementById('ratingInput').value = reviewRating;
    }


    let isLiked = <c:out value="${recipe.getValue()}"/>;
    let recipeId = "<c:out value="${recipe.getKey().recipeId}"/>";
    let AvgRating = 0;
    <c:if test="${!empty recipe.getKey().averageRating}">
         AvgRating = <c:out value="${recipe.getKey().averageRating}"/>;
    </c:if>

    document.addEventListener('DOMContentLoaded', function() {
        updateStarDisplay(AvgRating);
    });

    function updateStarDisplay(rating) {
        for (let i = 1; i <= 5; i++) {
            let star = document.getElementById('star' + i);
            if (i <= rating) {
                star.src = '/images/FullStar.svg';
            } else if (i <= rating + 0.5) {
                star.src = '/images/HalfStar.svg';
            } else {
                star.src = '/images/EmptyStar.svg';
            }
        }
    }


    function recipeToggleLike(recipeId) {
        if (isLiked === false) {
            fetch("/recipe/" + recipeId + "/like", { method: 'GET' })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Error occurred during liking, please try again later.");
                    }
                    return response;
                })
                .then(() => {
                    document.getElementById('like-button'+recipeId).src = '/images/FilledLike.svg';
                    isLiked = true;

                })
                .catch(error => {
                    console.log(error);
                });

        } else {
            fetch("/recipe/" + recipeId + "/unlike", { method: 'GET' })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Error occurred during unliking, please try again later.");
                    }
                    return response;
                })
                .then(() => {

                    document.getElementById('like-button'+recipeId).src = '/images/EmptyLike.svg';
                    isLiked = false;

                })
                .catch(error => {
                    console.log(error);
                });
        }
    }


    function submitReviewForm() {

        const pattern = /^[A-Za-z0-9\s.,;!?]*$/;
        const descriptionValue = document.getElementById("reviewBody").value.trim();
        if (!pattern.test(descriptionValue)) {
            displayModal_with_Title("Wrong format","Special characters are not allow in the review body.");
            return;
        }

        let rating = document.getElementById('ratingInput').value;

        if (rating === "" && descriptionValue === "") {
            displayModal_with_Title("Uncompleted review","You must add a text or a rating to submit a review");
            return;
        }
        document.getElementById("addReview").submit();
    }

</script>

<!-- Included footer component -->
<%@ include file="components/footer.txt"%>
<!-- External scripts for jQuery, Bootstrap, and custom JavaScript files -->
<script src="https://code.jquery.com/jquery-3.6.4.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
<script src="/js/Main.js"></script>
<script src="/js/StarScript.js"></script>
</body>
</html>

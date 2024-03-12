<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Reviews</title>
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

<%--DELETE MODAL--%>
<div id="deleteModal" class="myAlert">
    <div id="deleteModalContent" class="myAlertBody">
        <h4>Delete</h4>
        <p class="pt-2" id="deleteModalText"></p>
        <div class="d-flex gap-3">
            <button id="deleteReviewButton" onclick="deleteReview()" class="btn custom-btn-color float-end">Delete</button>
            <button class="btn btn-danger float-end" onclick="closeDeleteModal()">Close</button>
        </div>
    </div>
    <form id="deleteReviewForm" action="" method="post">
        <input type="hidden" name="reviewId" id="reviewId" \>
        <input type="hidden" name="recipeId" id="recipeId"\>
    </form>
</div>
<%--END DELETE MODAL--%>

<%--UPDATE MODAL--%>
<div id="reviewUpdateModal" class="reviewModal p-2">
    <h3 class="p-2 mt-2">Update review</h3>
    <form class="p-2" action="" method="post" id="updateReviewForm">
        <label for="reviewBody" class="form-label my-1">Review body</label>
        <textarea class="form-control" name="reviewText" id="reviewBody" style="resize:none;"></textarea>
        <label for="ratingInput" class="form-label my-1">Rating</label>
        <input type="number" name="reviewRating" id="ratingInput" class="form-control" step="1" min="1" max="5">
        <div class="mt-3 container">
            <a class="btn custom-btn-color" onclick="submitUpdateReviewForm()">Update</a>
            <a class="btn btn-danger float-end" onclick="closeUpdateForm()">Close</a>
        </div>
    </form>
</div>
<%--END UPDATE MODAL--%>

<main class="container" style="margin-bottom: 30px; margin-top: 80px">

<c:forEach var="review" items="${reviewPageDTO.entries}" varStatus="loopStatus">

    <div class="card my-4 custom-card">
        <div class="card-body">
            <!-- User info and rating container -->
            <div class="row align-items-center mb-2">
                <!-- User info and Rating stars -->
                <div class="col d-flex justify-content-between">
                    <!-- User info -->
                    <h3 class="card-title display-6 d-flex align-items-center m-0">

                        <c:choose>
                            <c:when test="${!empty review.authorProfilePictureUrl}">
                                <c:choose>
                                    <c:when test="${fn:startsWith(review.authorProfilePictureUrl, 'http')}">
                                        <div class="flex-grow-1">
                                            <img src="${review.authorProfilePictureUrl}" class="card-img-top img-fluid" style="border-radius: 15px; margin-right: 30px" alt="">
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="flex-grow-1">
                                            <img src="/uploads/${review.authorProfilePictureUrl}" class="card-img-top rounded-circle" style="margin-left: 15px; width: 100px; height: 100px; margin-right: 30px" alt="">
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <img src="/images/userLogo.svg" alt="" class="rounded-circle" style="margin-left: 15px; width: 100px; height: 100px;">
                            </c:otherwise>

                        </c:choose>

                        <span class="custom-text small-title">${review.authorUsername}</span>
                        <c:if test="${review.authorUsername.equals(loggedUser.username)}">
                            <div class="ms-2 d-flex align-items-center gap-2 ms-2">
                                <img onclick="displayReviewUpdateModal('${review.recipeId}','${review.reviewId}')" src="/images/pencil.png" width="30px" alt="">

                                <img onclick="displayReviewDeleteModal('${review.recipeId}','${review.reviewId}')" src="/images/trash-bin.png" width="30px" alt="">
                            </div>
                        </c:if>

                    </h3>
                    <!-- Rating stars -->
                    <div id="r_${loopStatus.index}_star-score">
                        <!-- Containers for each star -->
                        <img src="/images/EmptyStar.svg" class="star" id="r_${loopStatus.index}_star1" />
                        <img src="/images/EmptyStar.svg" class="star" id="r_${loopStatus.index}_star2" />
                        <img src="/images/EmptyStar.svg" class="star" id="r_${loopStatus.index}_star3" />
                        <img src="/images/EmptyStar.svg" class="star" id="r_${loopStatus.index}_star4" />
                        <img src="/images/EmptyStar.svg" class="star" id="r_${loopStatus.index}_star5" />

                        <input type="hidden" id="review_rating_${loopStatus.index}" value=${review.rating} />

                    </div>
                </div>


                <div class="row">
                    <p class="card-text mt-3 ms-2" style="font-size: 20px">
                            ${review.reviewBody}
                    </p>
                    <p class="text-end" id="reviewDate_${review.reviewId}">
                        ${review.datePublished}
                    </p>
                </div>


            </div>
        </div>
    </div>

</c:forEach>

    <!-- Pagination buttons -->
    <div class="d-flex justify-content-between position-fixed bottom-0 end-0" style="margin-bottom: 120px; margin-right: 80px">
        <div class="d-flex gap-2">
            <!-- Left arrow to decrease the page -->
            <button class="btn custom-btn-color" onclick="prevPage(${reviewPageDTO.currentPage})">
                &lt; Previous
            </button>
            <!-- Right arrow to increase the page -->
            <button class="btn custom-btn-color" onclick="nextPage(${reviewPageDTO.currentPage}, ${reviewPageDTO.numberOfPages})">
                Next &gt;
            </button>
        </div>
    </div>

    <div class="d-flex justify-content-between position-fixed bottom-0 start-0" style="margin-bottom: 120px; margin-left: 80px">
        <div class="d-flex gap-2">
            <button class="btn custom-btn-color" onclick="goToRecipeDetail()">
                Recipe details
            </button>
        </div>
    </div>

</main>

<script>

    window.addEventListener('load', () => {
        //take all tags whose id starts with reviewDate_
        let reviewDates = document.querySelectorAll('[id^="reviewDate_"]');
        for (let i = 0; i < reviewDates.length; i++) {
            //convert the date to a more readable format
            //format: hh:mm dd/mm/yyyy
            let date = new Date(reviewDates[i].innerText);
            let year = date.getFullYear();
            let mounth = (date.getMonth() + 1).toString().padStart(2, '0');
            let day = date.getDate().toString().padStart(2, '0');

            let hour = date.getHours().toString().padStart(2, '0');
            let minute = date.getMinutes().toString().padStart(2, '0');

            reviewDates[i].innerText = hour + ':' + minute + ' ' + day + '/' + mounth + '/' + year;

        }
    });

    document.addEventListener('DOMContentLoaded', function() {
        let numberOfReviews = parseInt(${reviewPageDTO.entries.size()});
        for (let i = 0; i < numberOfReviews; i++) {
            let review_id = 'review_rating_' + i;
            let ReviewRating = parseFloat(document.getElementById(review_id).value);
            updateStarDisplay_(ReviewRating, i);
        }
    });

    function updateStarDisplay_(rating, j) {
        for (let i = 1; i <= 5; i++) {
            let star = document.getElementById('r_' + j + '_star' + i);
            if (i <= rating) {
                star.src = '/images/FullStar.svg';
            } else if (i <= rating + 0.5) {
                star.src = '/images/HalfStar.svg';
            } else {
                star.src = '/images/EmptyStar.svg';
            }
        }
    }

    function deleteReview () {
        document.getElementById("deleteReviewForm").submit();
    }

    function displayReviewDeleteModal(recipeId, reviewId) {
        // Set the modal message content
        const buttonElement = document.getElementById("deleteReviewButton");

        const overlayElement = document.getElementById("overlay");
        overlayElement.style.display = "block";
        const modalTextElement = document.getElementById("deleteModalText");
        modalTextElement.innerText = 'Are you sure to delete your review?';

        //overflow-y: hidden;
        let body = document.getElementsByTagName("body")[0];
        body.style.overflowY = "hidden";

        //set the form
        document.getElementById("reviewId").value = reviewId;
        document.getElementById("recipeId").value = recipeId;
        document.getElementById("deleteReviewForm").action = "/recipe/review/delete";

        // Show the modal
        const modalElement = document.getElementById("deleteModal");
        modalElement.style.display = "block";
    }

    function displayReviewUpdateModal(recipeId, reviewId) {
        const overlayElement = document.getElementById("overlay");
        overlayElement.style.display = "block";
        const modalElement = document.getElementById("reviewUpdateModal");
        modalElement.style.display = "block";

        const updateReviewForm = document.getElementById("updateReviewForm");
        updateReviewForm.action = "/recipe/" + recipeId + "/review/" + reviewId +'/edit';
    }

    function closeUpdateForm() {
        const overlayElement = document.getElementById("overlay");
        overlayElement.style.display = "none";
        const modalElement = document.getElementById("reviewUpdateModal");
        modalElement.style.display = "none";
    }

    function submitUpdateReviewForm() {
        const pattern = /^[A-Za-z0-9\s.,;!?]*$/;
        const description = document.getElementById("reviewBody");
        const descriptionValue = description.value.trim();
        if (descriptionValue !== "" && !pattern.test(descriptionValue)) {
            displayModal_with_Title("Wrong format","Special characters are not allow in the review body.");
            return;
        } else if (descriptionValue === "" || descriptionValue === null || descriptionValue === undefined) {
            description.remove();
        }
        const ratingValue = document.getElementById("ratingInput");
        if (ratingValue.value === 0 || ratingValue.value == null || ratingValue.value === "" || ratingValue.value === undefined){
            ratingValue.remove();
        }

        else if (ratingValue.value < 1 || ratingValue.value > 5) {
            return;
        }

        document.getElementById("updateReviewForm").submit();
    }

    function goToRecipeDetail() {
        let oldUrl = window.location.href;
        // Find the position of "/recipe/" followed by an alphanumeric sequence (recipeId)
        let regex = /\/recipe\/[a-zA-Z0-9]+/;
        let match = oldUrl.match(regex);
        if (match) {
            // Extract the corresponding part of the URL
            window.location.href = match[0];
        } else {
            // Handle the case where "/recipe/" followed by recipeId is not present in the URL
            console.error("Error: Unable to find '/recipe/' followed by recipeId in the URL");
        }
    }


</script>

<div style="height: 200px"></div>

<!-- Included footer component -->
<%@ include file="components/footer.txt"%>

<!-- External scripts for jQuery, Bootstrap, and custom JavaScript files -->
<script src="https://code.jquery.com/jquery-3.6.4.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
<script src="/js/Main.js"></script>
<script src="/js/StarScript.js"></script>
</body>
</html>

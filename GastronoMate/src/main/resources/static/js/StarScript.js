
/*

<!-- Rating stars -->
<input type="hidden" id="numberOfReviews" value=${recipe.reviews.size()} />

<div id="r_${status.index}_star-score">
    <!-- Containers for each star -->
    <img src="/images/EmptyStar.svg" class="star" id="r_${status.index}_star1" />
    <img src="/images/EmptyStar.svg" class="star" id="r_${status.index}_star2" />
    <img src="/images/EmptyStar.svg" class="star" id="r_${status.index}_star3" />
    <img src="/images/EmptyStar.svg" class="star" id="r_${status.index}_star4" />
    <img src="/images/EmptyStar.svg" class="star" id="r_${status.index}_star5" />

    <input type="hidden" id="review_rating_${status.index}" value=${review.rating.floatValue()} />

</div>

*/
/*
document.addEventListener('DOMContentLoaded', function() {
    // Ottieni il valore di numberOfReviews
    let numberOfReviews = parseInt(document.getElementById('numberOfReviews').value, 10);
    // Esegui il ciclo per fetchare i dati desiderati
    for (let i = 0; i < numberOfReviews; i++) {
        // Ottieni il valore di ogni elemento review_rating_0, review_rating_1, ecc.
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
*/

function homePageToggleLike(recipeId) {

    isLiked = document.getElementById('recipeLiked'+recipeId).textContent.trim();

    if (isLiked === 'false') {
        fetch("/recipe/" + recipeId + "/like", { method: 'GET' })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error occurred during liking, please try again later.");
                }
                return response;
            })
            .then(() => {
                document.getElementById('like-button'+recipeId).src = '/images/FilledLike.svg';

                document.getElementById('recipeLiked'+recipeId).textContent = 'true';

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
                document.getElementById('recipeLiked'+recipeId).textContent = 'false';

            })
            .catch(error => {
                console.log(error);
            });
    }
}

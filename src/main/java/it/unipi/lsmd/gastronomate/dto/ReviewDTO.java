package it.unipi.lsmd.gastronomate.dto;

import it.unipi.lsmd.gastronomate.model.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor

public class ReviewDTO {
    private String reviewId;
    private String authorUsername;
    private String authorProfilePictureUrl;
    private Integer rating;
    private String reviewBody;
    private LocalDateTime datePublished;

    // recipe data
    private String recipeId;
    private String recipeTitle;
    private String recipeAuthorUsername;
    private String recipeAuthorProfilePictureUrl;

    public static ReviewDTO fromReview(Review review){
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewId(review.getId());
        reviewDTO.setAuthorUsername(review.getUser().getUsername());
        reviewDTO.setAuthorProfilePictureUrl(review.getUser().getProfilePictureUrl());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setReviewBody(review.getReviewBody());
        reviewDTO.setDatePublished(review.getDatePublished());
        reviewDTO.setRecipeId(review.getRecipe().getId());
        reviewDTO.setRecipeTitle(review.getRecipe().getTitle());
        reviewDTO.setRecipeAuthorUsername(review.getRecipe().getAuthor().getUsername());
        reviewDTO.setRecipeAuthorProfilePictureUrl(review.getRecipe().getAuthor().getProfilePictureUrl());
        return reviewDTO;
    }
}

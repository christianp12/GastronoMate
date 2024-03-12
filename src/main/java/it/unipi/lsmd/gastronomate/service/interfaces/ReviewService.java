package it.unipi.lsmd.gastronomate.service.interfaces;

import it.unipi.lsmd.gastronomate.dto.PageDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.ReviewDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;

import java.time.LocalDateTime;

public interface ReviewService {
    void addReview(ReviewDTO reviewDTO) throws BusinessException;
    void deleteReview(String reviewId, String authorUsername, String recipeId) throws BusinessException;
    void updateReview(String reviewId, String reviewText, Integer reviewRating) throws BusinessException;

    PageDTO<ReviewDTO> listNReviewsByRecipe(String recipeId, int page) throws BusinessException;

    Boolean isReviewed(String recipeId, String username) throws BusinessException;
}

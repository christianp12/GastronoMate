package it.unipi.lsmd.gastronomate.dao.interfaces;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dto.PageDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.ReviewDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;

import java.time.LocalDateTime;

public interface ReviewDAO {

    void createReview(ReviewDTO reviewDTO) throws DAOException;

    void deleteReviewsWithNoRecipe() throws DAOException;
    void deleteReviewsWithNoAuthor() throws DAOException;

    void deleteReview(String reviewId, String authorUsername, String recipeId) throws DAOException;

    void updateReview(String reviewId, String reviewText, Integer reviewRating) throws DAOException;

    PageDTO<ReviewDTO> getReviewsByRecipe(String recipeId, int page) throws DAOException;

    void updateRecipeRedundantData(RecipeSummaryDTO recipeSummaryDTO) throws DAOException;

    void updateAuthorRedundantData(UserSummaryDTO userSummaryDTO, String oldUsername) throws DAOException;

    Boolean isReviewed(String recipeId, String username) throws DAOException;
    void updateAverageRating() throws DAOException;




}

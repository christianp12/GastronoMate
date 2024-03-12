package it.unipi.lsmd.gastronomate.controller;

import it.unipi.lsmd.gastronomate.dto.LoggedUserDTO;
import it.unipi.lsmd.gastronomate.dto.PageDTO;
import it.unipi.lsmd.gastronomate.dto.ReviewDTO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.interfaces.CookieService;
import it.unipi.lsmd.gastronomate.service.interfaces.ReviewService;
import it.unipi.lsmd.gastronomate.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Controller
public class ReviewController {

    private final ReviewService reviewService = ServiceLocator.getReviewService();
    private final UserService userService = ServiceLocator.getUserService();
    private final CookieService cookieService = ServiceLocator.getCookieService();
    private final Logger applicationLogger = ServiceLocator.getApplicationLogger();


    @GetMapping("/recipe/{id}/reviews/{page}")
    public String showPaginatedReviewsGET(@CookieValue(value = "logged") String logged, @PathVariable String id, @PathVariable int page, Model model, HttpServletRequest request) {

        LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        PageDTO<ReviewDTO> reviews;

        try {
            reviews = reviewService.listNReviewsByRecipe(id, page);

        } catch (BusinessException e) {
            return "errorPage";
        }

        model.addAttribute("loggedUser", loggedUserDTO);
        model.addAttribute("reviewPageDTO", reviews);

        return "reviewsPage";
    }


    @PostMapping("/review/add")
    public String addReviewPOST(@CookieValue(value = "logged") String logged, HttpServletRequest request) {
        LoggedUserDTO loggedUserDTO;
        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }
        //recipe info
        String recipeId = request.getParameter("recipeId");
        String title = request.getParameter("title");
        String recipeAuthorUsername = request.getParameter("recipeAuthorUsername");
        String recipeAuthorProfilePictureUrl = request.getParameter("recipeAuthorProfilePictureUrl");

         //review info
        String reviewText = request.getParameter("reviewText");

        String reviewRatingString = request.getParameter("reviewRating");
        Integer reviewRating = ( notNullAndEmpty(reviewRatingString) ? Integer.parseInt(reviewRatingString) : null);

        if (reviewText == null && reviewRating == null) {
            applicationLogger.severe("ReviewController: Error while adding review: reviewText and reviewRating are null");
            return "errorPage";
        }

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setRecipeId(recipeId);
        reviewDTO.setRecipeTitle(title);
        //review author info
        reviewDTO.setRecipeAuthorUsername(recipeAuthorUsername);
        reviewDTO.setRecipeAuthorProfilePictureUrl( recipeAuthorUsername != null ? recipeAuthorProfilePictureUrl : null);

        reviewDTO.setAuthorUsername(loggedUserDTO.getUsername());
        reviewDTO.setAuthorProfilePictureUrl(loggedUserDTO.getProfilePicture() != null ? loggedUserDTO.getProfilePicture() : null);
        reviewDTO.setReviewBody(reviewText);
        reviewDTO.setRating(reviewRating);

        try {
            reviewService.addReview(reviewDTO);

        } catch (BusinessException e) {
            applicationLogger.severe("ReviewController: Error while adding review: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";
        }

        return "redirect:/recipe/" + recipeId + "/reviews/1";
    }

    @PostMapping("/recipe/review/delete")
    public String deleteReviewPost(@CookieValue(value = "logged") String logged, HttpServletRequest request) {

        LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        String reviewId = request.getParameter("reviewId");
        String recipeId = request.getParameter("recipeId");

        if (recipeId == null) {
            applicationLogger.severe("ReviewController: Error while deleting review: recipeId is null");
            return "errorPage";
        }

        try {
            reviewService.deleteReview(reviewId, loggedUserDTO.getUsername(), recipeId);

        } catch (BusinessException e) {
            applicationLogger.severe("ReviewController: Error while deleting review: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";
        }

        return "redirect:/recipe/" + recipeId + "/reviews/1";
    }

    @PostMapping("/recipe/{recipeId}/review/{reviewId}/edit")
    public String updateReviewPOST(@CookieValue(value = "logged") String logged, HttpServletRequest request, @PathVariable String reviewId, @PathVariable String recipeId) {

        try {
           cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        if (reviewId == null || recipeId == null) {
            applicationLogger.severe("ReviewController: Error while updating review: reviewId or recipeId is null");
            return "errorPage";
        }

        String reviewText = request.getParameter("reviewText");

        String reviewRatingString = request.getParameter("reviewRating");

        Integer reviewRating = reviewRatingString != null ? Integer.parseInt(reviewRatingString) : null;

        if (reviewText == null && reviewRating == null) {
            applicationLogger.severe("ReviewController: Error while updating review: reviewText and reviewRating are null");
            return "errorPage";
        }

        try {
            reviewService.updateReview(reviewId, reviewText, reviewRating);

        } catch (BusinessException e) {
            applicationLogger.severe("ReviewController: Error while updating review: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";
        }

        return "redirect:/recipe/" + recipeId + "/reviews/1";
    }

    @GetMapping("/recipe/{recipeId}/reviews")
    @ResponseBody
    public ResponseEntity<Boolean> isReviewed(@CookieValue(value = "logged") String logged, @PathVariable String recipeId) {
        LoggedUserDTO loggedUserDTO;
        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(null);
        }

        try {
            return ResponseEntity.status(200).body(reviewService.isReviewed(recipeId, loggedUserDTO.getUsername()));

        } catch (BusinessException e) {
            applicationLogger.severe("ReviewController: Error while checking if user " + loggedUserDTO.getUsername() + " has reviewed recipe " + recipeId + ": " + e.getMessage() + " - " + e.getErrorType());

            return ResponseEntity.status(500).body(null);
        }
    }

    private boolean notNullAndEmpty(String s){
        return s != null && !s.isEmpty();
    }

}

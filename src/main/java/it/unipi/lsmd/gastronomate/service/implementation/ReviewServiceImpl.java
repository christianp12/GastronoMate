package it.unipi.lsmd.gastronomate.service.implementation;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.interfaces.ReviewDAO;
import it.unipi.lsmd.gastronomate.dto.PageDTO;
import it.unipi.lsmd.gastronomate.dto.ReviewDTO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.enums.ExecutorTaskServiceTypeEnum;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;

import it.unipi.lsmd.gastronomate.service.implementation.asinc_review_tasks.CreateReviewTask;
import it.unipi.lsmd.gastronomate.service.implementation.asinc_review_tasks.DeleteReviewTask;

import it.unipi.lsmd.gastronomate.service.interfaces.ExecutorTaskService;
import it.unipi.lsmd.gastronomate.service.interfaces.ReviewService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Logger;



public class ReviewServiceImpl implements ReviewService {
    private  ReviewDAO reviewDAOMongoDB;
    private ReviewDAO reviewDAONeo4j;
    private Logger applicationLogger;
    private ExecutorTaskService aperiodicExecutorTaskService;

    public ReviewServiceImpl(){
        this.reviewDAOMongoDB = DAOLocator.getReviewDAO(DAOTypeEnum.MONGODB);
        this.reviewDAONeo4j = DAOLocator.getReviewDAO(DAOTypeEnum.NEO4J);

        this.applicationLogger = ServiceLocator.getApplicationLogger();
        this.aperiodicExecutorTaskService = ServiceLocator.getExecutorTaskService(ExecutorTaskServiceTypeEnum.APERIODIC);

    }
    @Override
    public void addReview(ReviewDTO reviewCreationDTO) throws BusinessException {

        //before calling this method, the controller has already checked that the review contains at least a text or a rating

        try {
              reviewDAOMongoDB.createReview(reviewCreationDTO);

            //at this point the control of the application is returned to the controller
            //the demand of adding a review is performed asynchronously

            //the following code is executed in a separate thread
           //if the task fails, it will be retried by a periodic ExecutorTaskService
            CreateReviewTask task = new CreateReviewTask(reviewCreationDTO);

            aperiodicExecutorTaskService.executeTask(task);

        } catch (DAOException e) {
            applicationLogger.severe("ReviewServiceImpl: Error while creating review in MongoDB: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while creating review in MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public void deleteReview(String reviewId, String authorUsername, String recipeId) throws BusinessException {
        try{
            reviewDAOMongoDB.deleteReview(reviewId, authorUsername, recipeId);
            //at this point the control of the application is returned to the controller
            //the demand of deleting a review is performed asynchronously
            //the following code is executed in a separate thread

            //if the task fails, it will be retried by a periodic ExecutorTaskService
            DeleteReviewTask task = new DeleteReviewTask( Map.of(
                    "reviewId", reviewId,
                    "authorUsername", authorUsername,
                    "recipeId", recipeId
            ));

            aperiodicExecutorTaskService.executeTask(task);

        }catch (DAOException e){
            applicationLogger.severe("ReviewServiceImpl: Error while deleting review in MongoDB: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while deleting review in MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }
    @Override
    public void updateReview(String reviewId, String reviewText, Integer reviewRating) throws BusinessException{

        //before calling this method, the controller has already checked that the review contains at least a text or a rating

        try{
            reviewDAOMongoDB.updateReview(reviewId, reviewText, reviewRating);

        }catch (DAOException e){
            applicationLogger.severe("ReviewServiceImpl: Error while updating review in MongoDB: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while updating review in MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public PageDTO<ReviewDTO> listNReviewsByRecipe(String recipeId, int page) throws BusinessException {
        try{
            return reviewDAOMongoDB.getReviewsByRecipe(recipeId, page);

        }catch (DAOException e){
            applicationLogger.severe("ReviewServiceImpl: Error while retrieving reviews from MongoDB: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while retrieving reviews from MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public Boolean isReviewed(String recipeId, String username) throws BusinessException {
        try{
            return reviewDAONeo4j.isReviewed(recipeId, username);

        }catch (DAOException e){
            applicationLogger.severe("ReviewServiceImpl: Error while checking if user has reviewed recipe in Neo4j: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while checking if user has reviewed recipe in Neo4j", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }
}

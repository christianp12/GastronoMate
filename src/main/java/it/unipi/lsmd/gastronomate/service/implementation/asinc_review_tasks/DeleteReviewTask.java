package it.unipi.lsmd.gastronomate.service.implementation.asinc_review_tasks;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.RecipeDAO;
import it.unipi.lsmd.gastronomate.dao.interfaces.ReviewDAO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Logger;

public class DeleteReviewTask extends Task {
    private ReviewDAO reviewDAONeo4j;
    private RecipeDAO recipeDAONeo4j;
    private RecipeDAO recipeDAOMongo;
    private Logger applicationLogger;
    private Map<String, Object> parameters;

    public DeleteReviewTask(Map<String, Object> parameters){
        super(7);
        this.reviewDAONeo4j = DAOLocator.getReviewDAO(DAOTypeEnum.NEO4J);
        this.recipeDAONeo4j = DAOLocator.getRecipeDAO(DAOTypeEnum.NEO4J);
        this.recipeDAOMongo = DAOLocator.getRecipeDAO(DAOTypeEnum.MONGODB);
        this.applicationLogger = ServiceLocator.getApplicationLogger();
        this.parameters = parameters;
    }

    @Override
    public void executeJob() throws BusinessException {
        try {

            String reviewId = (String) parameters.get("reviewId");
            String recipeId = (String) parameters.get("recipeId");

            String authorUsername = (String) parameters.get("authorUsername");

            reviewDAONeo4j.deleteReview(reviewId, authorUsername, recipeId);

            Integer reviews = recipeDAONeo4j.getNumOfReviews(recipeId);

            recipeDAOMongo.updateNumOfReviews(recipeId, reviews);

        } catch (DAOException e) {
            if (e.getErrorType().equals(ErrorTypeEnum.TRANSIENT_ERROR)) {
                throw new BusinessException("Error while deleting review: " + e.getMessage(), BusinessTypeErrorsEnum.RETRYABLE_ERROR);
            } else {
                applicationLogger.severe("Error while deleting review: " + e.getMessage());
                throw new BusinessException("Error while deleting review: " + e.getMessage(), BusinessTypeErrorsEnum.GENERIC_ERROR);
            }

        }
    }
}

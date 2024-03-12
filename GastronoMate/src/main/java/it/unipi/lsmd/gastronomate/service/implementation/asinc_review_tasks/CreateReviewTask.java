package it.unipi.lsmd.gastronomate.service.implementation.asinc_review_tasks;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.RecipeDAO;
import it.unipi.lsmd.gastronomate.dao.interfaces.ReviewDAO;
import it.unipi.lsmd.gastronomate.dto.ReviewDTO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.util.logging.Logger;

public class CreateReviewTask extends Task {
    private ReviewDAO reviewDAONeo4j;
    private RecipeDAO recipeDAONeo4j;
    private RecipeDAO recipeDAOMongo;
    private Logger applicationLogger;
    private ReviewDTO reviewDTO;

    public CreateReviewTask(ReviewDTO reviewDTO) {

        super(9);
        this.reviewDAONeo4j = DAOLocator.getReviewDAO(DAOTypeEnum.NEO4J);
        this.recipeDAONeo4j = DAOLocator.getRecipeDAO(DAOTypeEnum.NEO4J);
        this.recipeDAOMongo = DAOLocator.getRecipeDAO(DAOTypeEnum.MONGODB);
        this.applicationLogger = ServiceLocator.getApplicationLogger();
        this.reviewDTO = reviewDTO;
    }

    @Override
    public void executeJob() throws BusinessException {
        try {

            reviewDAONeo4j.createReview(reviewDTO);

            Integer reviews = recipeDAONeo4j.getNumOfReviews(reviewDTO.getRecipeId());

            recipeDAOMongo.updateNumOfReviews(reviewDTO.getRecipeId(), reviews);

        } catch (DAOException e) {
            if (e.getErrorType().equals(ErrorTypeEnum.TRANSIENT_ERROR)) {
                throw new BusinessException("Error while creating review: " + e.getMessage(), BusinessTypeErrorsEnum.RETRYABLE_ERROR);
            } else {
                applicationLogger.severe("Error while creating review: " + e.getMessage());
                throw new BusinessException("Error while creating review: " + e.getMessage(), BusinessTypeErrorsEnum.GENERIC_ERROR);
            }
        }
    }
}

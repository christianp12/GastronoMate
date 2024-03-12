package it.unipi.lsmd.gastronomate.service.implementation.asinc_review_tasks;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.ReviewDAO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.util.logging.Logger;

public class UpdateReviewRedundancyTask extends Task {

    private ReviewDAO reviewDAOMongo;
    private Logger applicationLogger;
    private RecipeSummaryDTO recipeSummaryDTO;
    private UserSummaryDTO userSummaryDTO;
    private String oldUsername;

    public UpdateReviewRedundancyTask(RecipeSummaryDTO recipeSummaryDTO, UserSummaryDTO userSummaryDTO, String oldUsername) {
        super(5);
        this.reviewDAOMongo = DAOLocator.getReviewDAO(DAOTypeEnum.MONGODB);
        this.applicationLogger = ServiceLocator.getApplicationLogger();

        this.recipeSummaryDTO = recipeSummaryDTO;
        this.userSummaryDTO = userSummaryDTO;
        this.oldUsername = oldUsername;
    }

    @Override
    public void executeJob() throws BusinessException {
        try{
            if (userSummaryDTO != null && oldUsername != null)
                reviewDAOMongo.updateAuthorRedundantData(userSummaryDTO, oldUsername);

            else if (recipeSummaryDTO != null && oldUsername == null && userSummaryDTO == null){
                reviewDAOMongo.updateRecipeRedundantData(recipeSummaryDTO);

            }

        } catch (DAOException e) {

            applicationLogger.severe("Error while updating review redundancy: " + e.getMessage());

           if(e.getErrorType().equals(ErrorTypeEnum.DATABASE_ERROR)){
                throw new BusinessException("Error while updating review redundancy: " + e.getMessage(), BusinessTypeErrorsEnum.RETRYABLE_ERROR);
              }
              else {
                throw new BusinessException("Error while updating review redundancy: " + e.getMessage(), BusinessTypeErrorsEnum.GENERIC_ERROR);
           }
        }
    }
}

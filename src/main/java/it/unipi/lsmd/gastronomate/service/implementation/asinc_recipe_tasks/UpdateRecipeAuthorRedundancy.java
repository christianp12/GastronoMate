package it.unipi.lsmd.gastronomate.service.implementation.asinc_recipe_tasks;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.RecipeDAO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.util.logging.Logger;

public class UpdateRecipeAuthorRedundancy extends Task {
    private RecipeDAO recipeDAOMongo;
    private Logger applicationLogger;
    private UserSummaryDTO userSummaryDTO;
    private String oldUsername;

    public UpdateRecipeAuthorRedundancy(UserSummaryDTO userSummaryDTO, String oldUsername) {
        super(5);

        this.recipeDAOMongo = DAOLocator.getRecipeDAO(DAOTypeEnum.MONGODB);
        this.applicationLogger = ServiceLocator.getApplicationLogger();

        this.userSummaryDTO = userSummaryDTO;
        this.oldUsername = oldUsername;
    }

    @Override
    public void executeJob() throws BusinessException {
        try{
            if (userSummaryDTO != null && oldUsername != null)
                recipeDAOMongo.updateAuthorRedundantData(userSummaryDTO, oldUsername);

        } catch (DAOException e) {

            applicationLogger.severe("Error while updating recipe redundancy: " + e.getMessage());

           if(e.getErrorType().equals(ErrorTypeEnum.DATABASE_ERROR)){
                throw new BusinessException("Error while updating recipe redundancy: " + e.getMessage(), BusinessTypeErrorsEnum.RETRYABLE_ERROR);
           }
           else {
                throw new BusinessException("Error while updating recipe redundancy: " + e.getMessage(), BusinessTypeErrorsEnum.GENERIC_ERROR);
           }
        }
    }
}

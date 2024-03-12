package it.unipi.lsmd.gastronomate.service.implementation.asinc_user_tasks;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.interfaces.UserDAO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.util.logging.Logger;

public class UpdateRecipesTask extends Task {

    private UserDAO mongoDBUserDAO;
    private Logger applicationLogger;
    private RecipeSummaryDTO recipeSummaryDTO;
    private int operationType;

    public UpdateRecipesTask(RecipeSummaryDTO recipeSummaryDTO, int operationType) {
        super(7);

        this.mongoDBUserDAO = DAOLocator.getUserDAO(DAOTypeEnum.MONGODB);
        this.applicationLogger = ServiceLocator.getApplicationLogger();

        this.recipeSummaryDTO = recipeSummaryDTO;
        this.operationType = operationType;
    }

    @Override
    public void executeJob() throws BusinessException {
        try {
            if (operationType == 0)
                mongoDBUserDAO.addRecipeToUser(recipeSummaryDTO);

            else if (operationType == 1)
                mongoDBUserDAO.removeRecipeFromUser(recipeSummaryDTO.getAuthorUsername(), recipeSummaryDTO.getRecipeId());

            else if (operationType == 2)
                mongoDBUserDAO.updateRecipeInUser(recipeSummaryDTO);

            else
                throw new BusinessException("Error while updating recipes", BusinessTypeErrorsEnum.GENERIC_ERROR);

        } catch (DAOException e) {
            applicationLogger.severe("UpdateRecipesTask: Error while updating recipes: " + e.getMessage());
            throw new BusinessException("Error while updating recipes", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }

    }
}

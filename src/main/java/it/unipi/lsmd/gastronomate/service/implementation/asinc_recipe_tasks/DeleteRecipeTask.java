package it.unipi.lsmd.gastronomate.service.implementation.asinc_recipe_tasks;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.RecipeDAO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.util.logging.Logger;

public class DeleteRecipeTask extends Task {

    private final RecipeDAO recipeDAONeo4j;
    private final Logger applicationLogger;
    private final String recipeId;

    public DeleteRecipeTask(String recipeId){
        super(7);
        this.recipeDAONeo4j = DAOLocator.getRecipeDAO(DAOTypeEnum.NEO4J);
        this.applicationLogger = ServiceLocator.getApplicationLogger();
        this.recipeId = recipeId;
    }
    @Override
    public void executeJob() throws BusinessException {
        try {
            recipeDAONeo4j.deleteRecipe(recipeId);

        } catch (DAOException e) {
            if (e.getErrorType().equals(ErrorTypeEnum.TRANSIENT_ERROR)) {
                throw new BusinessException("Error while deleting recipe: " + e.getMessage(), BusinessTypeErrorsEnum.RETRYABLE_ERROR);
            } else {
                applicationLogger.severe("Error while deleting recipe: " + e.getMessage());
                throw new BusinessException("Error while deleting recipe: " + e.getMessage(), BusinessTypeErrorsEnum.GENERIC_ERROR);
            }
        }
    }
}

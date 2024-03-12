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

import java.util.Map;
import java.util.logging.Logger;

public class UpdateRecipeTask extends Task {

    private final RecipeDAO recipeDAONeo4j;
    private final Logger applicationLogger;
    private final Map<String, Object> updateParams;
    private final String recipeId;

    public UpdateRecipeTask(Map<String, Object> updateParams, String recipeId){
        super(8);
        this.recipeDAONeo4j = DAOLocator.getRecipeDAO(DAOTypeEnum.NEO4J);
        this.applicationLogger = ServiceLocator.getApplicationLogger();
        this.updateParams = updateParams;
        this.recipeId = recipeId;
    }
    @Override
    public void executeJob() throws BusinessException {
        try {
            recipeDAONeo4j.updateRecipe(recipeId, updateParams);
        } catch (DAOException e) {
            if (e.getErrorType().equals(ErrorTypeEnum.TRANSIENT_ERROR)) {
                throw new BusinessException("Error while updating recipe: " + e.getMessage(), BusinessTypeErrorsEnum.RETRYABLE_ERROR);
            } else {
                applicationLogger.severe("Error while updating recipe: " + e.getMessage());
                throw new BusinessException("Error while updating recipe: " + e.getMessage(), BusinessTypeErrorsEnum.GENERIC_ERROR);
            }
        }
    }
}

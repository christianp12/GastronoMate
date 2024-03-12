package it.unipi.lsmd.gastronomate.service.implementation.asinc_recipe_tasks;

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

import java.util.logging.Logger;

public class UpdateNumberOfLikesTask extends Task {
    private RecipeDAO recipeDAOMongo;
    private RecipeDAO recipeDAONeo4j;
    private Logger applicationLogger;
    private String recipeId;

    public UpdateNumberOfLikesTask(String recipeId) {
        super(5);
        this.recipeDAOMongo = DAOLocator.getRecipeDAO(DAOTypeEnum.MONGODB);
        this.recipeDAONeo4j = DAOLocator.getRecipeDAO(DAOTypeEnum.NEO4J);
        this.applicationLogger = ServiceLocator.getApplicationLogger();
        this.recipeId = recipeId;
    }

    @Override
    public void executeJob() throws BusinessException {
        try {
            Integer likes = recipeDAONeo4j.getNumOfLikes(recipeId);

            recipeDAOMongo.updateNumOfLikes(recipeId, likes);

        } catch (DAOException e) {
            applicationLogger.severe("Error while getting number of likes: " + e.getMessage());
            throw new BusinessException("Error while getting number of likes: " + e.getMessage(), BusinessTypeErrorsEnum.DATABASE_ERROR);
        }

    }
}

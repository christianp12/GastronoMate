package it.unipi.lsmd.gastronomate.service.implementation.asinc_recipe_tasks.periodic;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.RecipeDAO;
import it.unipi.lsmd.gastronomate.model.Recipe;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import javax.swing.plaf.basic.BasicButtonUI;
import java.util.logging.Logger;

public class PendingRecipesEliminationTask extends Task {
    private RecipeDAO recipeDAOMongo;
    private RecipeDAO recipeDAONeo4j;
    private Logger applicationLogger;

    public PendingRecipesEliminationTask() {
        super(5);
        this.recipeDAOMongo = DAOLocator.getRecipeDAO(DAOTypeEnum.MONGODB);
        this.recipeDAONeo4j = DAOLocator.getRecipeDAO(DAOTypeEnum.NEO4J);
        this.applicationLogger = ServiceLocator.getApplicationLogger();
    }

    @Override
    public void executeJob() throws BusinessException {
        try {
            recipeDAOMongo.deleteRecipesWithNoAuthor();

            recipeDAONeo4j.deleteRecipesWithNoAuthor();

        } catch (Exception e) {
            applicationLogger.severe("Error while deleting pending recipes: " + e.getMessage());
            throw new BusinessException("Error while deleting pending recipes: " + e.getMessage(), BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }
}

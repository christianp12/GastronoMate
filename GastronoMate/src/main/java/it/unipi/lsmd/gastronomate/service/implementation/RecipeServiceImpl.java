package it.unipi.lsmd.gastronomate.service.implementation;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.interfaces.RecipeDAO;
import it.unipi.lsmd.gastronomate.dto.PageDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.enums.ExecutorTaskServiceTypeEnum;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.implementation.asinc_recipe_tasks.CreateRecipeTask;
import it.unipi.lsmd.gastronomate.service.implementation.asinc_recipe_tasks.DeleteRecipeTask;
import it.unipi.lsmd.gastronomate.service.implementation.asinc_recipe_tasks.UpdateNumberOfLikesTask;
import it.unipi.lsmd.gastronomate.service.implementation.asinc_recipe_tasks.UpdateRecipeTask;
import it.unipi.lsmd.gastronomate.service.implementation.asinc_review_tasks.UpdateReviewRedundancyTask;
import it.unipi.lsmd.gastronomate.service.implementation.asinc_user_tasks.UpdateRecipesTask;
import it.unipi.lsmd.gastronomate.service.interfaces.ExecutorTaskService;
import it.unipi.lsmd.gastronomate.service.interfaces.RecipeService;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RecipeServiceImpl implements RecipeService {

    private RecipeDAO recipeDAOMongoDb;
    private RecipeDAO recipeDAONeo4j;

    private Logger applicationLogger;
    private ExecutorTaskService aperiodicExecutorTaskService;

    public RecipeServiceImpl(){
        this.recipeDAOMongoDb = DAOLocator.getRecipeDAO(DAOTypeEnum.MONGODB);
        this.recipeDAONeo4j = DAOLocator.getRecipeDAO(DAOTypeEnum.NEO4J);


        this.applicationLogger = ServiceLocator.getApplicationLogger();
        this.aperiodicExecutorTaskService = ServiceLocator.getExecutorTaskService(ExecutorTaskServiceTypeEnum.APERIODIC);

    }
    @Override
    public void createRecipe(RecipeDTO recipe) throws BusinessException {

        try{
            //insert the recipe in MongoDB
            recipeDAOMongoDb.createRecipe(recipe);


            //create the task which adds a new node Recipe in Neo4j
            CreateRecipeTask task = new CreateRecipeTask(recipe);
            aperiodicExecutorTaskService.executeTask(task);

            //create the task which adds the recipe to the user's recipes
            RecipeSummaryDTO recipeSummaryDTO = new RecipeSummaryDTO();

            recipeSummaryDTO.setRecipeId(recipe.getRecipeId());
            recipeSummaryDTO.setTitle(recipe.getTitle());
            recipeSummaryDTO.setAuthorUsername(recipe.getAuthorUsername());
            recipeSummaryDTO.setDatePublished(recipe.getDatePublished());
            recipeSummaryDTO.setPictureUrl(recipe.getPictureUrl());



          UpdateRecipesTask task1 = new UpdateRecipesTask(recipeSummaryDTO, 0);
          aperiodicExecutorTaskService.executeTask(task1);

        } catch (DAOException e) {
            applicationLogger.severe("ReviewServiceImpl: Error while creating recipe in MongoDB: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while creating recipe in MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public Pair<RecipeDTO,Boolean> readRecipe(String recipeID, String loggedUser) throws BusinessException {
        try {
            RecipeDTO recipeDTO = recipeDAOMongoDb.readRecipe(recipeID);
            Boolean liked = recipeDAONeo4j.likedRecipe(recipeID, loggedUser);

            return new Pair<>(recipeDTO, liked);

        } catch (DAOException e) {
            applicationLogger.severe("ReviewServiceImpl: Error while reading recipe from MongoDB: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while reading recipe from MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public RecipeDTO retriveRecipe(String recipeId) throws BusinessException {

        try {
            RecipeDTO recipeDTO = recipeDAOMongoDb.readRecipe(recipeId);

            return recipeDTO;

        } catch (DAOException e) {
            applicationLogger.severe("ReviewServiceImpl: Error while reading recipe from MongoDB: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while reading recipe from MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public List<RecipeSummaryDTO> searchRecipesByString(String filter, Integer n, String loggedUser) throws BusinessException {
       try {
            return recipeDAOMongoDb.searchFirstNRecipes(filter, n, loggedUser );

        } catch (DAOException e) {
            applicationLogger.severe("ReviewServiceImpl: Error while reading recipe from MongoDB: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while reading recipe from MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
       }
    }

    @Override
    public void updateRecipe(String recipeId, Map<String, Object> updateParams, String author) throws BusinessException {
        try {
            recipeDAOMongoDb.updateRecipe(recipeId, updateParams);

            UpdateRecipeTask task = new UpdateRecipeTask(updateParams, recipeId);
            aperiodicExecutorTaskService.executeTask(task);

            if(updateParams.containsKey("title") || updateParams.containsKey("ImageUrl")){

                RecipeSummaryDTO recipeSummaryDTO = new RecipeSummaryDTO();

                recipeSummaryDTO.setRecipeId(recipeId);
                recipeSummaryDTO.setAuthorUsername(author);

                if (updateParams.containsKey("title"))
                    recipeSummaryDTO.setTitle((String) updateParams.get("title"));

                if (updateParams.containsKey("ImageUrl"))
                    recipeSummaryDTO.setPictureUrl((String) updateParams.get("ImageUrl"));

                UpdateReviewRedundancyTask task1 = new UpdateReviewRedundancyTask(recipeSummaryDTO, null, null);
                aperiodicExecutorTaskService.executeTask(task1);

                //update the recipe in the user's recipes if the author updates the title or the image
                UpdateRecipesTask task2 = new UpdateRecipesTask(recipeSummaryDTO, 2);
                aperiodicExecutorTaskService.executeTask(task2);
            }

        } catch (DAOException e) {
            applicationLogger.severe("ReviewServiceImpl: Error while deleting recipe from MongoDB: " + e.getMessage() + " - " + e.getErrorType());
           throw new BusinessException("Error while deleting recipe from MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }

    }

    @Override
    public void deleteRecipe(String recipeId, String author) throws BusinessException {
        try {
            recipeDAOMongoDb.deleteRecipe(recipeId);

            DeleteRecipeTask task = new DeleteRecipeTask(recipeId);
            aperiodicExecutorTaskService.executeTask(task);

            RecipeSummaryDTO recipeSummaryDTO = new RecipeSummaryDTO();
            recipeSummaryDTO.setRecipeId(recipeId);
            recipeSummaryDTO.setAuthorUsername(author);

            UpdateRecipesTask task1 = new UpdateRecipesTask(recipeSummaryDTO, 1);
            aperiodicExecutorTaskService.executeTask(task1);


        } catch (DAOException e) {
            applicationLogger.severe("ReviewServiceImpl: Error while deleting recipe from MongoDB: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while deleting recipe from MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }

    }

    @Override
    public List<RecipeSummaryDTO> retriveSuggestedRecipes(String username, int limit) throws BusinessException {
        try{

            return recipeDAONeo4j.suggestedRecipes(username, limit);

        } catch (DAOException e) {
            applicationLogger.severe("ReviewServiceImpl: Error while reading recipe from MongoDB: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while reading recipe from MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public PageDTO<Pair<RecipeSummaryDTO, Boolean>> retriveFollowedUsersRecipes(int page, String username) throws BusinessException {

        try{
            return recipeDAONeo4j.followedUsersRecipe(page, username);

        } catch (DAOException e) {
            applicationLogger.severe("ReviewServiceImpl: Error while reading recipe from MongoDB: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while reading recipe from MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);

        }
    }

    @Override
    public void likeRecipe(String recipeId, String username) throws BusinessException {
        try {
            recipeDAONeo4j.likeRecipe(recipeId, username);

            UpdateNumberOfLikesTask task = new UpdateNumberOfLikesTask(recipeId);
            aperiodicExecutorTaskService.executeTask(task);

        }catch (DAOException e) {
            applicationLogger.severe("ReviewServiceImpl: Error while liking recipe in MongoDB: " + e.getMessage() + " - " + e.getErrorType());
           throw new BusinessException("Error while liking recipe in MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public void unlikeRecipe(String recipeId, String username) throws BusinessException {
        try {
            recipeDAONeo4j.unlikeRecipe(recipeId, username);

            UpdateNumberOfLikesTask task = new UpdateNumberOfLikesTask(recipeId);
            aperiodicExecutorTaskService.executeTask(task);

        }catch (DAOException e) {
            applicationLogger.severe("ReviewServiceImpl: Error while unliking recipe in MongoDB: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while unliking recipe in MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }

    }

}

package it.unipi.lsmd.gastronomate.dao.interfaces;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dto.PageDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public interface RecipeDAO {

    void createRecipe(RecipeDTO recipe) throws DAOException;
    RecipeDTO readRecipe(String recipeID) throws DAOException;
    Boolean likedRecipe(String recipeId, String username) throws DAOException;
    List<RecipeSummaryDTO> searchFirstNRecipes(String query, Integer n, String loggedUser) throws DAOException;
    void updateRecipe(String recipeId, Map<String, Object> updateParams) throws DAOException;
    void deleteRecipe(String recipeId) throws DAOException;
    List<RecipeSummaryDTO> suggestedRecipes(String username, int limit) throws DAOException;

    PageDTO<Pair<RecipeSummaryDTO, Boolean>> followedUsersRecipe(int page, String username) throws DAOException;

    public void likeRecipe(String recipeId, String username) throws DAOException;
    public void unlikeRecipe(String recipeId, String username) throws DAOException;

    public Integer getNumOfLikes(String recipeId) throws DAOException;
    public Integer getNumOfReviews(String recipeId) throws DAOException;

    public void updateNumOfLikes(String recipeId, Integer likes ) throws DAOException;
    public void updateNumOfReviews(String recipeId, Integer reviews) throws DAOException;
    public void updateAuthorRedundantData(UserSummaryDTO userSummaryDTO, String oldUsername) throws DAOException;

    public void deleteRecipesWithNoAuthor() throws DAOException;




}

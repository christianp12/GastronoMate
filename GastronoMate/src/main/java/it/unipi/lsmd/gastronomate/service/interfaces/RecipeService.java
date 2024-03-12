package it.unipi.lsmd.gastronomate.service.interfaces;

import it.unipi.lsmd.gastronomate.dto.PageDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import javafx.util.Pair;
import org.apache.el.parser.BooleanNode;

import java.util.List;
import java.util.Map;

public interface RecipeService {
    void createRecipe(RecipeDTO recipe) throws BusinessException;
    Pair<RecipeDTO, Boolean> readRecipe(String recipeID, String loggedUser) throws BusinessException;
    RecipeDTO retriveRecipe(String recipeId) throws BusinessException;
    List<RecipeSummaryDTO> searchRecipesByString(String filter, Integer n, String loggedUser ) throws BusinessException;
    void updateRecipe(String recipeId, Map<String, Object> updateParams, String author) throws BusinessException;
    void deleteRecipe(String recipeId, String author) throws BusinessException;
    List<RecipeSummaryDTO> retriveSuggestedRecipes(String username, int limit) throws BusinessException;
    PageDTO<Pair<RecipeSummaryDTO, Boolean>> retriveFollowedUsersRecipes(int page, String username) throws BusinessException;
    void likeRecipe(String recipeId, String username) throws BusinessException;
    void unlikeRecipe(String recipeId, String username) throws BusinessException;

}

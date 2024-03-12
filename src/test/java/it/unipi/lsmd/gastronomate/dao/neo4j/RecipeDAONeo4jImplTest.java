package it.unipi.lsmd.gastronomate.dao.neo4j;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.mongoDB.MongoDbBaseDAO;
import it.unipi.lsmd.gastronomate.dto.PageDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RecipeDAONeo4jImplTest {

    @BeforeEach
    void setUp() throws Exception {
       Neo4jBaseDAO.openConnection();
    }

    @Test
    void createRecipe() {
        RecipeDAONeo4jImpl recipeDAONeo4j = new RecipeDAONeo4jImpl();
        RecipeDTO recipeCreationDTO = new RecipeDTO();
        /* fake params */
        recipeCreationDTO.setTitle("Pasta al pomodoro");
        recipeCreationDTO.setAuthorUsername("GMMECH51");
        recipeCreationDTO.setKeywords(List.of("pasta", "pomodoro"));

        recipeCreationDTO.setRecipeId("123456789");
        recipeCreationDTO.setDatePublished(LocalDateTime.now());

        try {
            recipeDAONeo4j.createRecipe(recipeCreationDTO);

        } catch (DAOException e) {
           System.out.println(e.getMessage() + " " + e.getErrorType());
        }
    }
    @Test
    void updateRecipe() {
        RecipeDAONeo4jImpl recipeDAONeo4j = new RecipeDAONeo4jImpl();
        RecipeDTO recipeUpdateDTO = new RecipeDTO();
        /* fake params */
        recipeUpdateDTO.setTitle("Pasta al ragù");
        recipeUpdateDTO.setAuthorUsername("GMMECH51");

        recipeUpdateDTO.setKeywords(List.of("pasta", "pomodoro"));
        recipeUpdateDTO.setRecipeId("123456789");


        Map<String, Object> params = Map.of("title", "Porchetta",
                "keywords", List.of("pasta", "pomodoro", "porchetta"));

        try {
            recipeDAONeo4j.updateRecipe(recipeUpdateDTO.getRecipeId(), params);

        } catch (DAOException e) {
            System.out.println(e.getMessage() + " " + e.getErrorType());
        }
    }
    @Test
    void deleteRecipe() {
        RecipeDAONeo4jImpl recipeDAONeo4j = new RecipeDAONeo4jImpl();
        RecipeDTO recipeDeleteDTO = new RecipeDTO();
        /* fake params */

        recipeDeleteDTO.setRecipeId("123456789");

        try {
            recipeDAONeo4j.deleteRecipe(recipeDeleteDTO.getRecipeId());
        } catch (DAOException e) {
            System.out.println(e.getMessage() + " " + e.getErrorType());
        }
    }

    @Test
    void suggestedRecipes() {
        RecipeDAONeo4jImpl recipeDAONeo4j = new RecipeDAONeo4jImpl();
        RecipeDTO recipeSuggestedDTO = new RecipeDTO();
        /* fake params */

        recipeSuggestedDTO.setAuthorUsername("GMMECH50");

        try {
             List<RecipeSummaryDTO> recipes = recipeDAONeo4j.suggestedRecipes(recipeSuggestedDTO.getAuthorUsername(), 3);
             System.out.println(recipes);

        } catch (DAOException e) {
            System.out.println(e.getMessage() + " " + e.getErrorType());
        }
    }

    @Test
    void followedUsersRecipe() {
        RecipeDAONeo4jImpl recipeDAONeo4j = new RecipeDAONeo4jImpl();
        RecipeDTO recipeFollowedDTO = new RecipeDTO();
        /* fake params */

        recipeFollowedDTO.setAuthorUsername("GMMECH50");

        try {
            PageDTO<Pair<RecipeSummaryDTO, Boolean>> page = recipeDAONeo4j.followedUsersRecipe(2, recipeFollowedDTO.getAuthorUsername());
            System.out.println(page.getEntries());

            System.out.println(page.getTotalCount() + " " + page.getNumberOfPages());

        } catch (DAOException e) {
            System.out.println(e.getMessage() + " " + e.getErrorType());
        }
    }

    @Test
    void getNumOfReviews() {
        RecipeDAONeo4jImpl recipeDAONeo4j = new RecipeDAONeo4jImpl();

        try {
            Integer num = recipeDAONeo4j.getNumOfReviews("6579b98172d58fd14fd70f93");
            System.out.println(num);


        } catch (DAOException e) {
            System.out.println(e.getMessage() + " " + e.getErrorType());
        }
    }

    @Test
    void likedRecipe() {
        RecipeDAONeo4jImpl recipeDAONeo4j = new RecipeDAONeo4jImpl();

        Boolean liked = null;
        try {
            liked = recipeDAONeo4j.likedRecipe("6579b98172d58fd14fd70f93", "GMMECH50");

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(liked);
    }
}
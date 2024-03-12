package it.unipi.lsmd.gastronomate.dao.mongoDB;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dto.RecipeDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RecipeDAOMongoDbImplTest {

    @BeforeEach
    void setUp() throws Exception {
        MongoDbBaseDAO.openConnection();
    }
    @Test
   void WHEN_createRecipe_With_right_data_THEN_NO_Exceptions(){
        RecipeDAOMongoDbImpl recipeDAOMongoDb = new RecipeDAOMongoDbImpl();
        RecipeDTO recipeCreationDTO = new RecipeDTO();

        /* fake params */
        recipeCreationDTO.setTitle("Pasta with tomato sauce");
        recipeCreationDTO.setAuthorUsername("winternox");
        recipeCreationDTO.setAuthorProfilePictureUrl("https://media-assets.lacucinaitaliana.it/photos/6411f3da3d6fc4cae3c3892b/16:9/w_3300,h_1856,c_limit/photo-07201503701.jpg");
        recipeCreationDTO.setDescription("Very nice pasta");
        recipeCreationDTO.setCookTime("10M");
        recipeCreationDTO.setPrepTime("20M");
        recipeCreationDTO.setRecipeServings(4);
        recipeCreationDTO.setPictureUrl("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.giallozafferano.it%2Fricerca-ricette%2Fpa");

        assertDoesNotThrow(() -> recipeDAOMongoDb.createRecipe(recipeCreationDTO));

        System.out.println("Recipe created: " + recipeCreationDTO.getRecipeId());

    }
    //65b1fd0bf24b872bbbfd5001
    @Test
    void WHEN_readRecipe_With_right_data_THEN_NO_Exceptions(){

        RecipeDAOMongoDbImpl recipeDAOMongoDb = new RecipeDAOMongoDbImpl();



        try {
            RecipeDTO  recipeDTO = recipeDAOMongoDb.readRecipe("6579b97d72d58fd14fcf9bc7");
            System.out.println("Recipe read: " + recipeDTO);

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void searchFirstNRecipes() {
        String query = "pppppp";
        Integer n = 10;
        String loggedUser = "Pino";

        RecipeDAOMongoDbImpl recipeDAOMongoDb = new RecipeDAOMongoDbImpl();

        try {
            List<RecipeSummaryDTO> recipes = recipeDAOMongoDb.searchFirstNRecipes(null, 2, loggedUser);
            System.out.println("Recipes found: " + recipes);

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateRecipe(){
        String recipeId = "65b204479e1d8724d88df739";
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("title", "Pasta al ragù");
        updateParams.put("Author.Username", "Pina");

        updateParams.put("description", "nice pasta");
        updateParams.put("cookTime", "10M");
        updateParams.put("prepTime", "20M");
        updateParams.put("recipeServings", 4L);
        updateParams.put("pictureUrl", "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.giallozafferano.it%2Fricerca-ricette%2Fpa");

        RecipeDAOMongoDbImpl recipeDAOMongoDb = new RecipeDAOMongoDbImpl();

        try {
            recipeDAOMongoDb.updateRecipe(recipeId, updateParams);
            System.out.println("Recipe updated: " + recipeId);

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteRecipe(){
        String recipeId = "65b1fd0bf24b872bbbfd5001";

        RecipeDAOMongoDbImpl recipeDAOMongoDb = new RecipeDAOMongoDbImpl();

        try {
            recipeDAOMongoDb.deleteRecipe(recipeId);
            System.out.println("Recipe deleted: " + recipeId);

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateAuthorRedundantData(){
       RecipeDAOMongoDbImpl recipeDAOMongoDb = new RecipeDAOMongoDbImpl();

        UserSummaryDTO userSummaryDTO = new UserSummaryDTO();

        userSummaryDTO.setUsername("Pino");
        userSummaryDTO.setProfilePictureUrl("foto1");

        try {
            recipeDAOMongoDb.updateAuthorRedundantData(userSummaryDTO, "Pina");
            System.out.println("Ok");

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteRecipesWithNoAuthor(){
        RecipeDAOMongoDbImpl recipeDAOMongoDb = new RecipeDAOMongoDbImpl();

        try {
            recipeDAOMongoDb.deleteRecipesWithNoAuthor();
            System.out.println("Ok");

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

}
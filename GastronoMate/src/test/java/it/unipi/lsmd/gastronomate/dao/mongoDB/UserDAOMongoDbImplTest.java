package it.unipi.lsmd.gastronomate.dao.mongoDB;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserDTO;
import it.unipi.lsmd.gastronomate.model.Address;
import it.unipi.lsmd.gastronomate.model.enums.AccountSatusTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

class UserDAOMongoDbImplTest {

    @BeforeEach
    void setUp() throws Exception {
        MongoDbBaseDAO.openConnection();
    }

    @Test
    void createUser(){
        UserDAOMongoDbImpl userDAOMongoDb = new UserDAOMongoDbImpl();
        UserDTO user = new UserDTO();
        user.setFullName("Mario Rossi");
        user.setUsername("p");
        user.setEmail("rossi@gmail.com");
        user.setAddress(new Address("Pisa", "Pisa", "Italy"));
        user.setDateOfBirth(LocalDateTime.of(2001, 12, 12, 9, 30));
        user.setCreationDate(LocalDateTime.now());

        user.setProfilePictureUrl("profilepic");

        try{
            userDAOMongoDb.createUser(user);
            System.out.println("User created: "+ user.getId());

        }catch (DAOException e) {
            System.err.println(e.getMessage() + " " + e.getErrorType());
        }
    }

    @Test
    void readUser(){
        UserDAOMongoDbImpl userDAOMongoDb = new UserDAOMongoDbImpl();
        try{
            System.out.println(userDAOMongoDb.readUser("Glo"));


        } catch (DAOException e) {
            System.err.println(e.getMessage() + " " + e.getErrorType());
        }
    }
    @Test
    void updateUser() {
        UserDAOMongoDbImpl userDAOMongoDb = new UserDAOMongoDbImpl();
        Map<String,Object> map = new HashMap<>();
        map.put("username", "pippo");
        map.put("profilePictureUrl", "newProfilePic");
        try {
            userDAOMongoDb.updateUser("65787f95d3100bd34861bcae", map);
        } catch (DAOException e) {
            System.err.println(e.getMessage() + " " + e.getErrorType());
        }
    }

    @Test
    void deleteUser(){
        UserDAOMongoDbImpl userDAOMongoDb = new UserDAOMongoDbImpl();
        try{
            userDAOMongoDb.deleteUser("p");
        } catch (DAOException e) {
            System.err.println(e.getMessage() + " " + e.getErrorType());
        }
    }

    @Test
    void  addNewRecipe(){
        UserDAOMongoDbImpl userDAOMongoDb = new UserDAOMongoDbImpl();
        try{
            RecipeSummaryDTO recipe = new RecipeSummaryDTO();
            recipe.setRecipeId("65bbac0c83671528f975941e");
            recipe.setTitle("Pasta al pomodoro");
            recipe.setPictureUrl("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.giallozafferano.it%2Fricerca-ricette%2Fpasta%2520al%2520pomodoro%2F&psig=AOvVaw0QZ4Z4Z4Z4Z4Z4Z4Z4Z4Z4&ust=1604427980008000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCJjQ4ZqHt-wCFQAAAAAdAAAAABAD");
            recipe.setDatePublished(LocalDateTime.now());

            recipe.setAuthorUsername("Geme");

            userDAOMongoDb.addRecipeToUser(recipe);

        } catch (DAOException e) {
            System.err.println(e.getMessage() + " " + e.getErrorType());
        }
    }
    @Test
    void deleteRecipe(){
        UserDAOMongoDbImpl userDAOMongoDb = new UserDAOMongoDbImpl();
        try{
            userDAOMongoDb.removeRecipeFromUser("Suzy Q 2", "5f9f9f9f9f9f9f9f9f9f9f9f");
        } catch (DAOException e) {
            System.err.println(e.getMessage() + " " + e.getErrorType());
        }
    }

    @Test
    void updateRecipeInUser() {
    }

    @Test
    void searchFirstNUsers() {
        UserDAOMongoDbImpl userDAOMongoDb = new UserDAOMongoDbImpl();

        try{
            System.out.println(userDAOMongoDb.searchFirstNUsers("glo", 10, "Pippo"));

        } catch (DAOException e) {
            System.err.println(e.getMessage() + " " + e.getErrorType());
        }
    }
}
package it.unipi.lsmd.gastronomate.controller;

import com.google.gson.Gson;
import it.unipi.lsmd.gastronomate.dto.*;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.interfaces.CookieService;
import it.unipi.lsmd.gastronomate.service.interfaces.RecipeService;
import it.unipi.lsmd.gastronomate.service.interfaces.ReviewService;
import it.unipi.lsmd.gastronomate.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import javafx.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Logger;

@Controller
public class RecipeController {
    private final RecipeService recipeService = ServiceLocator.getRecipeService();
    private final CookieService cookieService = ServiceLocator.getCookieService();
    private final Logger applicationLogger = ServiceLocator.getApplicationLogger();
    private static final String PROJECT_PATH = System.getProperty("user.dir");


    private static final SecureRandom random = new SecureRandom();
    private static final char[] digits = "0123456789ABCDEFabcdef".toCharArray();




    @GetMapping("/recipe/{id}")
    public String recipeGET(@CookieValue(value = "logged") String logged, @PathVariable String id, Model model) {

        LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        try {

            Pair<RecipeDTO, Boolean> recipe = recipeService.readRecipe(id, loggedUserDTO.getUsername());

            model.addAttribute("loggedUser", loggedUserDTO);
            model.addAttribute("recipe", recipe);

            System.out.println("Recipe: " + recipe.getKey().getAuthorUsername());
            System.out.println("Logged: " + loggedUserDTO.getUsername());

        } catch (BusinessException e) {
            applicationLogger.severe("RecipeController: Error while reading recipe: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";

        }
        return "recipeDetail";
    }

    @GetMapping("/recipe/{id}/like")
    @ResponseBody
    public ResponseEntity<String> likeRecipeGET(@CookieValue (value = "logged") String logged,  @PathVariable String id) {

        LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            applicationLogger.severe("RecipeController: Error while getting cookie: " + e.getMessage());
            return new ResponseEntity<>("Failed to like the recipe: cookie not available", HttpStatus.BAD_REQUEST);
        }

        try {
            recipeService.likeRecipe(id, loggedUserDTO.getUsername());

        } catch (BusinessException e) {
            applicationLogger.severe("RecipeController: Error while liking recipe: " + e.getMessage() + " - " + e.getErrorType());
            return new ResponseEntity<>("Failed to like the recipe", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Recipe liked successfully", HttpStatus.OK);

    }

    @GetMapping("/recipe/{id}/unlike")
    @ResponseBody
    public ResponseEntity<String> unlikeRecipeGET(@CookieValue (value = "logged") String logged, @PathVariable String id) {

            LoggedUserDTO loggedUserDTO;

            try {
                loggedUserDTO = cookieService.getCookie(logged);

            } catch (Exception e) {
                applicationLogger.severe("RecipeController: Error while getting cookie: " + e.getMessage());
                return new ResponseEntity<>("Failed to unlike the recipe: cookie not available", HttpStatus.BAD_REQUEST);
            }

            try {
                recipeService.unlikeRecipe(id, loggedUserDTO.getUsername());

            } catch (BusinessException e) {
                applicationLogger.severe("RecipeController: Error while unliking recipe: " + e.getMessage() + " - " + e.getErrorType());
                return new ResponseEntity<>("Failed to unlike the recipe", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>("Recipe unliked successfully", HttpStatus.OK);
    }

    @GetMapping("/recipe/publish")
    public String publishRecipeGET(@CookieValue(value = "logged") String logged) {

        try {
            cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        return "publishRecipe";

    }

    @PostMapping("/recipe/publish")
    public String publishRecipePOST(@CookieValue(value = "logged") String logged, HttpServletRequest request){

        LoggedUserDTO loggedUserDTO;

       try {
           loggedUserDTO = cookieService.getCookie(logged);

       }catch (Exception e) {
           return "loginPage";
       }

        RecipeDTO recipeDTO = new RecipeDTO();

        recipeDTO.setAuthorUsername(loggedUserDTO.getUsername());

        if(notNullAndEmpty(loggedUserDTO.getProfilePicture()))
            recipeDTO.setAuthorProfilePictureUrl(loggedUserDTO.getProfilePicture());

        if (notNullAndEmpty(request.getParameter("title")))
            recipeDTO.setTitle(request.getParameter("title"));

        if (notNullAndEmpty(request.getParameter("description")))
            recipeDTO.setDescription(request.getParameter("description"));

        if (notNullAndEmpty(request.getParameter("recipeServings")))
            recipeDTO.setRecipeServings((int) Integer.parseInt(request.getParameter("recipeServings")));

        String cookTime = request.getParameter("cookTime");

        if (notNullAndEmpty(cookTime)) {

            Integer minutes = Integer.parseInt(cookTime);
            String newCookTime;

            if(minutes < 60)
                newCookTime = minutes + "M";

            else if (minutes % 60 == 0)
                newCookTime = (minutes / 60) + "H";

            else
                newCookTime = (minutes / 60) + "H" + (minutes % 60) + "M";

            recipeDTO.setCookTime(newCookTime);

        }

        String prepTime = request.getParameter("prepTime");

        if (notNullAndEmpty(prepTime)) {

            Integer minutes = Integer.parseInt(prepTime);
            String newPrepTime;

            if(minutes < 60)
                newPrepTime = minutes + "M";

            else if (minutes % 60 == 0)
                newPrepTime = (minutes / 60) + "H";

            else
                newPrepTime = (minutes / 60) + "H" + (minutes % 60) + "M";

            recipeDTO.setPrepTime(newPrepTime);
        }

        if (notNullAndEmpty(request.getParameter("listOfIngredients")))
            recipeDTO.setIngredients(List.of(request.getParameter("listOfIngredients").split(",")));

        if (notNullAndEmpty(request.getParameter("listOfKeywords")))
            recipeDTO.setKeywords(List.of(request.getParameter("listOfKeywords").split(",")));

        if (notNullAndEmpty(cookTime) && notNullAndEmpty(prepTime)) {

            Integer totalTime = Integer.parseInt(cookTime) + Integer.parseInt(prepTime);

            if (totalTime < 60)
                recipeDTO.setTotalTime(totalTime + "M");

            else if (totalTime % 60 == 0)
                recipeDTO.setTotalTime((totalTime / 60) + "H" );

            else
                recipeDTO.setTotalTime((totalTime / 60) + "H" + (totalTime % 60) + "M");
        }

        if (notNullAndEmpty(request.getParameter("calories")))
            recipeDTO.setCalories(Double.valueOf(request.getParameter("calories")));

        if (notNullAndEmpty(request.getParameter("fatContent")))
            recipeDTO.setFatContent(Double.valueOf(request.getParameter("fatContent")));

        if (notNullAndEmpty(request.getParameter("saturatedFatContent")))
            recipeDTO.setSaturatedFatContent(Double.valueOf(request.getParameter("saturatedFatContent")));

        if (notNullAndEmpty(request.getParameter("sodiumContent")))
            recipeDTO.setSodiumContent(Double.valueOf(request.getParameter("sodiumContent")));

        if (notNullAndEmpty(request.getParameter("carbohydrateContent")))
            recipeDTO.setCarbohydrateContent(Double.valueOf(request.getParameter("carbohydrateContent")));

        if (notNullAndEmpty(request.getParameter("fiberContent")))
            recipeDTO.setFiberContent(Double.valueOf(request.getParameter("fiberContent")));

        if (notNullAndEmpty(request.getParameter("sugarContent")))
            recipeDTO.setSugarContent(Double.valueOf(request.getParameter("sugarContent")));

        if (notNullAndEmpty(request.getParameter("proteinContent")))
            recipeDTO.setProteinContent(Double.valueOf(request.getParameter("proteinContent")));


        String image = request.getParameter("croppedImageB64");

        if (notNullAndEmpty(image)) {
            try {
                recipeDTO.setPictureUrl(saveImage(image, loggedUserDTO.getUsername(), request));

            } catch (IOException e) {
                applicationLogger.severe("RecipeController: Error while saving image: " + e.getMessage());
            }
        }

        try {
            recipeService.createRecipe(recipeDTO);

        } catch (BusinessException e) {
            try {
                deleteImage(recipeDTO.getPictureUrl());

            } catch (IOException ioException) {
                applicationLogger.severe("RecipeController: Error while deleting image: " + ioException.getMessage());
            }
            applicationLogger.severe("RecipeController: Error while creating recipe: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";
        }

        return "redirect:/recipe/" + recipeDTO.getRecipeId();
    }
    @GetMapping("/recipe/{id}/edit")
    public String editRecipeGET(@CookieValue(value = "logged") String logged, @PathVariable String id, Model model) {

        LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        RecipeDTO recipe;

        try {
            recipe = recipeService.retriveRecipe(id);

        } catch (BusinessException e) {
            applicationLogger.severe("RecipeController: Error while reading recipe: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";
        }

        model.addAttribute("loggedUser", loggedUserDTO);
        model.addAttribute("recipe", recipe);

        return "updateRecipe";
    }

    @PostMapping("/recipe/edit")
    public String updateRecipePost(@CookieValue (value = "logged") String logged, HttpServletRequest request){

        LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        RecipeDTO recipeDTO = new RecipeDTO();

        recipeDTO.setRecipeId(request.getParameter("recipeId"));

        if (notNullAndEmpty(request.getParameter("title")))
            recipeDTO.setTitle(request.getParameter("title"));

        if (notNullAndEmpty(request.getParameter("description")))
            recipeDTO.setDescription(request.getParameter("description"));

        if (notNullAndEmpty(request.getParameter("recipeServings")))
            recipeDTO.setRecipeServings(Integer.parseInt(request.getParameter("recipeServings")));

        String cookTime = request.getParameter("cookTime");

        if (notNullAndEmpty(cookTime)) {

            Integer minutes = Integer.parseInt(cookTime);
            String newCookTime;

            if(minutes < 60)
                newCookTime = minutes + "M";

            else if (minutes % 60 == 0)
                newCookTime = (minutes / 60) + "H";

            else
                newCookTime = (minutes / 60) + "H" + (minutes % 60) + "M";

            recipeDTO.setCookTime(newCookTime);
        }

        String prepTime = request.getParameter("prepTime");

        if (notNullAndEmpty(prepTime)) {

            Integer minutes = Integer.parseInt(prepTime);
            String newPrepTime;

            if(minutes < 60)
                newPrepTime = minutes + "M";

            else if (minutes % 60 == 0)
                newPrepTime = (minutes / 60) + "H";

            else
                newPrepTime = (minutes / 60) + "H" + (minutes % 60) + "M";

            recipeDTO.setPrepTime(newPrepTime);
        }

        if (notNullAndEmpty(request.getParameter("listOfIngredients")))
            recipeDTO.setIngredients(List.of(request.getParameter("listOfIngredients").split(",")));

        if (notNullAndEmpty(request.getParameter("listOfKeywords")))
            recipeDTO.setKeywords(List.of(request.getParameter("listOfKeywords").split(",")));

        if (notNullAndEmpty(cookTime) && notNullAndEmpty(prepTime)) {

            Integer totalTime = Integer.parseInt(cookTime) + Integer.parseInt(prepTime);

            if (totalTime < 60)
                recipeDTO.setTotalTime(totalTime + "M");

            else if(totalTime % 60 == 0)
                recipeDTO.setTotalTime((totalTime / 60) + "H");

            else
                recipeDTO.setTotalTime((totalTime / 60) + "H" + (totalTime % 60) + "M");

        }

        if (notNullAndEmpty(request.getParameter("calories")))
            recipeDTO.setCalories(Double.valueOf(request.getParameter("calories")));

        if (notNullAndEmpty(request.getParameter("fatContent")))
            recipeDTO.setFatContent(Double.valueOf(request.getParameter("fatContent")));

        if (notNullAndEmpty(request.getParameter("saturatedFatContent")))
            recipeDTO.setSaturatedFatContent(Double.valueOf(request.getParameter("saturatedFatContent")));

        if (notNullAndEmpty(request.getParameter("sodiumContent")))
            recipeDTO.setSodiumContent(Double.valueOf(request.getParameter("sodiumContent")));

        if (notNullAndEmpty(request.getParameter("carbohydrateContent")))
            recipeDTO.setCarbohydrateContent(Double.valueOf(request.getParameter("carbohydrateContent")));

        if (notNullAndEmpty(request.getParameter("fiberContent")))
            recipeDTO.setFiberContent(Double.valueOf(request.getParameter("fiberContent")));

        if (notNullAndEmpty(request.getParameter("sugarContent")))
            recipeDTO.setSugarContent(Double.valueOf(request.getParameter("sugarContent")));

        if (notNullAndEmpty(request.getParameter("proteinContent")))
            recipeDTO.setProteinContent(Double.valueOf(request.getParameter("proteinContent")));

        String image = request.getParameter("croppedImageB64");

        String oldImage = request.getParameter("oldImage");

        if (notNullAndEmpty(image)) {
            try {
                recipeDTO.setPictureUrl(saveImage(image, loggedUserDTO.getUsername(), request));

                deleteImage(oldImage);

            } catch (IOException e) {
                applicationLogger.severe("RecipeController: Error while saving image: " + e.getMessage());
            }
        }

        Map<String, Object> updateParams = RecipeDTO.toMap(recipeDTO);

        try {
            recipeService.updateRecipe(recipeDTO.getRecipeId(), updateParams, loggedUserDTO.getUsername());

        } catch (BusinessException e) {
            try {
                deleteImage(recipeDTO.getPictureUrl());

            } catch (IOException ioException) {
                applicationLogger.severe("RecipeController: Error while deleting image: " + ioException.getMessage());
            }
            return "errorPage";
        }

        return "redirect:/recipe/" + recipeDTO.getRecipeId();

    }

    @GetMapping("/search/{keyword}")
    public String searchRecipesGet(@CookieValue(value = "logged") String logged, @PathVariable String keyword, Model model) {

        LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        List<RecipeSummaryDTO> recipeSummaryDTOList;

        try{
            recipeSummaryDTOList = recipeService.searchRecipesByString(keyword,10, loggedUserDTO.getUsername());

        }catch (BusinessException e){
            applicationLogger.severe("RecipeController: Error while searching recipes: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";
        }

        model.addAttribute("loggedUser", loggedUserDTO);
        model.addAttribute("results", recipeSummaryDTOList);

        return "searchRecipe";
    }

    @PostMapping("/recipe/search")
    public String searchRecipesPost(@CookieValue(value = "logged") String logged, Model model, HttpServletRequest request) {

        LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        List<RecipeSummaryDTO> recipeSummaryDTOList;
        String keyword = request.getParameter("keyword");

        try{
              recipeSummaryDTOList = recipeService.searchRecipesByString(keyword,10, loggedUserDTO.getUsername());

        }catch (BusinessException e){
            applicationLogger.severe("RecipeController: Error while searching recipes: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";
        }

        model.addAttribute("loggedUser", loggedUserDTO);
        model.addAttribute("results", recipeSummaryDTOList);

        return "searchRecipe";
    }


    @GetMapping("/recipe/{id}/delete")
    public String deleteRecipeGET(@CookieValue(value = "logged") String logged, @PathVariable String id) {

        LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        try {
            recipeService.deleteRecipe(id, loggedUserDTO.getUsername());

        } catch (BusinessException e) {
            return "errorPage";
        }

        return "redirect:/user/myProfile";
    }

    @GetMapping("/recipe/suggestedRecipes")
    @ResponseBody
    public String suggestedRecipesGET(@CookieValue(value = "logged") String logged) {

        LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        List<RecipeSummaryDTO> recipeSummaryDTOList;

        try {
            recipeSummaryDTOList = recipeService.retriveSuggestedRecipes(loggedUserDTO.getUsername(), 10);

        } catch (BusinessException e) {
            applicationLogger.severe("RecipeController: Error while getting suggested recipes: " + e.getMessage() + " - " + e.getErrorType());
            return null;
        }

        Gson gson = new Gson();
        String json = gson.toJson(recipeSummaryDTOList);


        return json;
    }


    private boolean notNullAndEmpty(String s){
        return s != null && !s.isEmpty();
    }


    private String saveImage(String image, String owner, HttpServletRequest request) throws  IOException{

        // Remove "data:image/png;base64," header if there is one
        image = image.replaceFirst("data:image\\/.*?;base64,", "");

        // Decode Base64 string in byte[]
        byte[] imageBytes = Base64.getDecoder().decode(image);

        // Generate a unique image name based on author username and recipeDTO ID
        String imageName = owner.replace(" ", "") + "_" + generateHexString(16) + ".png";

        // Create the full path for saving the image
        Path imagesPath = Paths.get(PROJECT_PATH,"src", "main", "resources", "static", "uploads", "recipes", imageName);

        // Write the output to the specified path
        Files.write(imagesPath, imageBytes);

        // Set the image URL in the recipeDTO object
        return imageName;

    }

    private void deleteImage(String imageName) throws IOException{

        Files.deleteIfExists(Paths.get(PROJECT_PATH,"src", "main", "resources", "static", "uploads", "recipes", imageName));
    }
    private String generateHexString(int length) {
        char[] buffer = new char[length];
        for (int i = 0; i < length; i++) {
            buffer[i] = digits[random.nextInt(16)];
        }
        return new String(buffer);
    }
}

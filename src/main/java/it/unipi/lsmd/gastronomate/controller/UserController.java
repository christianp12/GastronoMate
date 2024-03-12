
package it.unipi.lsmd.gastronomate.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import it.unipi.lsmd.gastronomate.dto.*;
import it.unipi.lsmd.gastronomate.model.Address;
import it.unipi.lsmd.gastronomate.model.enums.UserTypeEnum;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.CookieService;
import it.unipi.lsmd.gastronomate.service.interfaces.RecipeService;
import it.unipi.lsmd.gastronomate.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javafx.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Logger;


@Controller
public class UserController {

    private final RecipeService recipeService = ServiceLocator.getRecipeService();
    private final UserService userService = ServiceLocator.getUserService();
    private final CookieService cookieService = ServiceLocator.getCookieService();
    private final Logger applicationLogger = ServiceLocator.getApplicationLogger();
    private static final String PROJECT_PATH = System.getProperty("user.dir");


    //Index method
    @GetMapping("/")
    public String index(@CookieValue (value = "logged") String logged) {

        try {

            cookieService.getCookie(logged);

        } catch (Exception e) {
            applicationLogger.severe("UserController: Error while reading cookie: " + e.getMessage());
            return "loginPage";
        }


        return "redirect:/homePage/1";
    }

    @GetMapping("/homePage/{page}")
    public String homePageGET(@CookieValue (value = "logged") String logged, @PathVariable int page,  Model model) {

        PageDTO<Pair<RecipeSummaryDTO, Boolean>> recipes;
        LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            applicationLogger.severe("UserController: Error while reading cookie: " + e.getMessage());
            return "loginPage";
        }

        try {

            recipes = recipeService.retriveFollowedUsersRecipes(page, loggedUserDTO.getUsername());

        }catch (BusinessException e) {
            applicationLogger.severe("UserController: Error while reading recipes: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";

        }
        model.addAttribute("recipes", recipes);

        return "homePage";
    }
    //Sign up and login methods
    @GetMapping("/login")
    public String loginGET() {

        return "loginPage";
    }

    @PostMapping("/login")
    public String loginPOST(HttpServletRequest request, HttpServletResponse response, Model model) {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Map<String, String> params;

        if (notNullAndEmpty(username) && notNullAndEmpty(password)) {
            try {
                params = userService.authenticate(username, password);

            } catch (BusinessException e) {

                if (e.getErrorType().equals(BusinessTypeErrorsEnum.AUTHENTICATION_ERROR)) {

                    model.addAttribute("errorMessage", e.getMessage());
                    return "loginPage";

                } else {
                    applicationLogger.severe("UserController: Error while authenticating user: " + e.getMessage() + " - " + e.getErrorType());
                    return "errorPage";
                }
            }

        } else if (notNullAndEmpty(email) && notNullAndEmpty(password)) {
            try {
                params = userService.authenticate(email, password);

            } catch (BusinessException e) {

                if (e.getErrorType().equals(BusinessTypeErrorsEnum.AUTHENTICATION_ERROR)) {

                    model.addAttribute("errorMessage", e.getMessage());
                    return "loginPage";

                } else {
                    applicationLogger.severe("UserController: Error while authenticating user: " + e.getMessage() + " - " + e.getErrorType());
                    return "errorPage";
                }
            }

        }
        else {

            applicationLogger.severe("UserController: Error while authenticating user: " + "username or email and password are null or empty");
            return "errorPage";
        }

        LoggedUserDTO loggedUserDTO = new LoggedUserDTO();

        loggedUserDTO.setUsername(params.get("username"));

        if(params.containsKey("type"))
            loggedUserDTO.setUserType(params.get("type").equals("ADMIN") ? UserTypeEnum.ADMIN : null);

        if(params.containsKey("profilePictureUrl"))
            loggedUserDTO.setProfilePicture(params.get("profilePictureUrl"));

        List<String> values = new ArrayList<>();

        values.add(loggedUserDTO.getUsername());

        if (loggedUserDTO.getUserType() != null)
            values.add(loggedUserDTO.getUserType().toString());

        if (loggedUserDTO.getProfilePicture() != null)
            values.add(loggedUserDTO.getProfilePicture());

        // Cookie creation
        cookieService.setCookie("logged", values, null, "/", response);

        return "redirect:/homePage/1";
    }

    @GetMapping("/signup")
    public String signUpGET() {return "signUpPage";}

    @PostMapping("/signup")
    public String signUpPOST(HttpServletRequest request, HttpServletResponse response, Model model) {

        UserDTO user = new UserDTO();

        // Full Name validation
        if (notNullAndEmpty(request.getParameter("fullName"))) {
            user.setFullName(request.getParameter("fullName"));
        }

        // Email validation
        if (notNullAndEmpty(request.getParameter("email"))) {
            user.setEmail(request.getParameter("email"));
        }

        // Username validation
        if (notNullAndEmpty(request.getParameter("username"))) {
            user.setUsername(request.getParameter("username"));
        }

        // Password validation
        if (notNullAndEmpty(request.getParameter("password"))) {
            user.setPassword(request.getParameter("password"));
        }

        Address address = new Address();

        // City validation
        if (notNullAndEmpty(request.getParameter("city"))) {
            address.setCity(request.getParameter("city"));
        }

        // Country validation
        if (notNullAndEmpty(request.getParameter("country"))) {
            address.setCountry(request.getParameter("country"));
        }

        // State validation
        if (notNullAndEmpty(request.getParameter("state"))) {
            address.setState(request.getParameter("state"));
        }

        user.setAddress(address);

        // Date of Birth validation
        String dateOfBirthParameter = request.getParameter("dateOfBirth");

        if (notNullAndEmpty(dateOfBirthParameter)) {
            try {
                LocalDate dateOfBirth = LocalDate.parse(dateOfBirthParameter);
                // Calculate the age using Period
                Period age = Period.between(dateOfBirth, LocalDate.now());

                // Check if the person is less than 18 years old
                if (age.getYears() < 18) {

                    model.addAttribute("errorMessage", "You must be at least 18 years old to sign up");
                    return "signUpPage";

                } else {
                    user.setDateOfBirth(dateOfBirth.atStartOfDay());
                }

            } catch (DateTimeParseException e) {

                applicationLogger.severe("UserController: Error while parsing date of birth: " + e.getMessage());
                return "errorPage";
            }
        }

        String image = request.getParameter("croppedImageB64");

        if (notNullAndEmpty(image)) {

            try {
                String imageUrl = saveImage(user.getUsername(), image, request);
                user.setProfilePictureUrl(imageUrl);

            } catch (IOException e) {
                applicationLogger.severe("UserController: Error while saving profile picture: " + e.getMessage());
                return "errorPage";
            }

        }

        try{

            userService.createUser(user);


        }catch (BusinessException e){
            applicationLogger.severe("UserController: Error while creating user: " + e.getMessage() + " - " + e.getErrorType());

            if(notNullAndEmpty(image)){
                try {
                    deleteImage(user.getUsername());

                } catch (IOException ex) {
                    applicationLogger.severe("UserController: Error while deleting profile picture: " + ex.getMessage());
                }
            }

            if (e.getErrorType().equals(BusinessTypeErrorsEnum.DUPLICATED_ELEMENT)){

                model.addAttribute("errorMessage", "username or email already exists");
                return "signUpPage";
            }

            return "errorPage";
        }

        if (user.getProfilePictureUrl() != null)
            cookieService.setCookie("logged", List.of(user.getUsername(), user.getProfilePictureUrl()), null, "/", response);
        else
            cookieService.setCookie("logged", List.of(user.getUsername()), null, "/", response);

        return "redirect:/homePage/1";

    }

    @GetMapping("/logout")
    public String logoutGET(HttpServletResponse response) {

        cookieService.deleteCookie("logged", response);
        return "redirect:/";
    }

    // User methods
    @GetMapping("/user/myProfile")
    public String myProfileGET(@CookieValue(value = "logged") String logged, Model model, @ModelAttribute("errorMessage") String errorMessage) {

        LoggedUserDTO loggedUserDTO;
        UserDTO userDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);


        } catch (Exception e) {
            return "loginPage";
        }

        try {

            userDTO = userService.readUser(loggedUserDTO.getUsername(), true);

        } catch (BusinessException e) {
            applicationLogger.severe("UserController: Error while reading user's profile: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";

        }

        model.addAttribute("loggedUser", loggedUserDTO);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("user", userDTO);

        return "myProfile";
    }

    @GetMapping("/user/myProfile/edit")
    public String updateProfileGET(@CookieValue(value = "logged") String logged, Model model) {

        LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        UserDTO userDTO;

        try {

            userDTO = userService.readUser(loggedUserDTO.getUsername(), true);

        } catch (BusinessException e) {
            applicationLogger.severe("UserController: Error while reading user's profile: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";

        }

        model.addAttribute("loggedUser", loggedUserDTO);
        model.addAttribute("user", userDTO);

        return "signUpPage";
    }

    @PostMapping("/user/myProfile/edit")
    public String updateProfilePOST(@CookieValue(value = "logged") String logged, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

       LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        String email;
        String newUsername = null;
        String password;
        String description;
        String city;
        String country;
        String state;
        String image = null;

        String imageUrl = null;

        try{

            String id = request.getParameter("id");

            if(!notNullAndEmpty(id)){
                applicationLogger.severe("UserController: Error while updating user's profile: " + "id is null or empty");
                return "errorPage";
            }


            email = request.getParameter("email");
            newUsername = request.getParameter("username");
            password = request.getParameter("password");
            description = request.getParameter("description");
            city = request.getParameter("city");
            country = request.getParameter("country");
            state = request.getParameter("state");
            image = request.getParameter("croppedImageB64");

            Map<String, Object> updateParams = new HashMap<>();


            if (notNullAndEmpty(email)){
                updateParams.put("Email", email);
            }
            if (notNullAndEmpty(newUsername)){
                updateParams.put("username", newUsername);
            }
            if (notNullAndEmpty(password)){
                updateParams.put("Password", password);
            }
            if (notNullAndEmpty(description)){
                updateParams.put("Description", description);
            }
            if (notNullAndEmpty(city)){
                updateParams.put("Address.City", city);
            }
            if (notNullAndEmpty(country)){
                updateParams.put("Address.Country", country);
            }
            if (notNullAndEmpty(state)){
                updateParams.put("Address.State", state);
            }


            if (notNullAndEmpty(image)){
                try {
                     imageUrl = saveImage((notNullAndEmpty(newUsername)?newUsername:loggedUserDTO.getUsername()), image, request);

                    updateParams.put("ProfilePictureUrl", imageUrl);

                } catch (IOException e) {
                    applicationLogger.severe("UserController: Error while saving profile picture: " + e.getMessage());
                }
            }
            if (!updateParams.isEmpty())
                userService.updateUser(id, updateParams, loggedUserDTO.getUsername());

        }catch (BusinessException e) {
            applicationLogger.severe("UserController: Error while updating user's profile: " + e.getMessage() + " - " + e.getErrorType());

            if(notNullAndEmpty(image)){
                try {
                    deleteImage((notNullAndEmpty(newUsername)?newUsername:loggedUserDTO.getUsername()));

                } catch (IOException ex) {
                    applicationLogger.severe("UserController: Error while deleting profile picture: " + ex.getMessage());
                }
            }

            if (e.getErrorType().equals(BusinessTypeErrorsEnum.DUPLICATED_ELEMENT)){

                redirectAttributes.addAttribute("errorMessage", "username or email already exists");
                return "redirect:/user/myProfile";
            }

            return "errorPage";
        }

        if (notNullAndEmpty(newUsername)) {
            cookieService.deleteCookie("logged", response);

            if(notNullAndEmpty(imageUrl))
                cookieService.setCookie("logged", List.of(newUsername, imageUrl), null, "/", response);
            else
                cookieService.setCookie("logged", List.of(newUsername), null, "/", response);
        }


        return "redirect:/user/myProfile";
    }

    @GetMapping("/user/myProfile/delete")
    public String deleteUserGET(@CookieValue(value = "logged") String logged) {

        LoggedUserDTO loggedUserDTO;
        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        try {

            userService.deleteUser(loggedUserDTO.getUsername());

            deleteImage(loggedUserDTO.getUsername());


        } catch (BusinessException e) {
            applicationLogger.severe("UserController: Error while deleting user's profile: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";

        }catch (IOException e) {
            applicationLogger.severe("UserController: Error while deleting profile picture: " + e.getMessage());

        }

        return "redirect:/logout";
    }

    @GetMapping("/user/profile")
    public String userProfileGET(@CookieValue(value = "logged") String logged, Model model, HttpServletRequest request) {
        UserDTO userDTO;
        LoggedUserDTO loggedUserDTO;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        String username = request.getParameter("username");
        Boolean isFollowed;

        try {

            userDTO = userService.readUser(username, false);
            isFollowed = userService.isFollowed(loggedUserDTO.getUsername(), username);

        } catch (BusinessException e) {
            applicationLogger.severe("UserController: Error while reading user's profile: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";

        }

        model.addAttribute("user", userDTO);
        model.addAttribute("loggedUser", loggedUserDTO);
        model.addAttribute("isFollowed", isFollowed);

        return "userProfile";
    }


    @GetMapping("/user/{username}/follow")
    @ResponseBody
    public ResponseEntity<String> followUserGET(@CookieValue(value = "logged") String logged, @PathVariable String username) {

        LoggedUserDTO loggedUserDTO;
        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("You must be logged in to follow a user");
        }

        try {
            userService.followUser(loggedUserDTO.getUsername(), username);

        } catch (BusinessException e) {
           return ResponseEntity.internalServerError().body("Error while following user: " + e.getMessage() + " - " + e.getErrorType());

        }

        return new ResponseEntity<>("User followed successfully", HttpStatus.OK);
    }

    @GetMapping("/user/{username}/unfollow")
    @ResponseBody
    public ResponseEntity<String> unfollowUserGET(@CookieValue(value = "logged") String logged, @PathVariable String username) {

        LoggedUserDTO loggedUserDTO;
        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("You must be logged in to unfollow a user");
        }

        try {

            userService.unfollowUser(loggedUserDTO.getUsername(), username);

        } catch (BusinessException e) {
            return ResponseEntity.internalServerError().body("Error while unfollowing user: " + e.getMessage() + " - " + e.getErrorType());

        }

        return ResponseEntity.ok().body("User unfollowed successfully");
    }

    @GetMapping("/user/{username}/followers")
    public String followersGET(@CookieValue(value = "logged") String logged, @PathVariable String username, Model model, HttpServletRequest request) {

        LoggedUserDTO loggedUserDTO;

        List<UserSummaryDTO> followers;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        try {

            followers = userService.getFollowers(loggedUserDTO.getUsername(), username);


        } catch (BusinessException e) {
            applicationLogger.severe("UserController: Error while reading user's followers: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";

        }

        model.addAttribute("loggedUser", loggedUserDTO);

        model.addAttribute("users", followers);

        return "searchUser";
    }

    @GetMapping("/user/{username}/followed")
    public String followedGET(@CookieValue(value = "logged") String logged, @PathVariable String username, Model model) {

        LoggedUserDTO loggedUserDTO;

        List<UserSummaryDTO> followed;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        try {

            followed = userService.getFollowedUsers(loggedUserDTO.getUsername(), username);

        } catch (BusinessException e) {
            applicationLogger.severe("UserController: Error while reading user's followed: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";

        }

        model.addAttribute("loggedUser", loggedUserDTO);

        model.addAttribute("users", followed);

        return "searchUser";

    }

    @PostMapping("/user/search")
    public String searchUsersGET(@CookieValue(value = "logged") String logged, Model model, HttpServletRequest request) {

        LoggedUserDTO loggedUserDTO;

        List<UserSummaryDTO> users;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
            return "loginPage";
        }

        String key = request.getParameter("key");

        try {

            users = userService.findAccounts(key, 10, loggedUserDTO.getUsername());

        } catch (BusinessException e) {
            applicationLogger.severe("UserController: Error while searching users: " + e.getMessage() + " - " + e.getErrorType());
            return "errorPage";

        }

        model.addAttribute("loggedUser", loggedUserDTO);
        model.addAttribute("users", users);

        return "searchUser";
    }

    @GetMapping("/user/suggestedUsers")
    @ResponseBody
    public String suggestedUsersGET(@CookieValue(value = "logged") String logged) {

        LoggedUserDTO loggedUserDTO;
        List<UserSummaryDTO> suggestedUsers;

        try {
            loggedUserDTO = cookieService.getCookie(logged);

        } catch (Exception e) {
               return null;
        }

       try {

           suggestedUsers = userService.getSuggestedUsers(loggedUserDTO.getUsername(), 10);

       } catch (BusinessException e) {
           applicationLogger.severe("UserController: Error while reading suggested users: " + e.getMessage() + " - " + e.getErrorType());
           return null;
       }


        Gson gson = new Gson();
        String json = gson.toJson(suggestedUsers);

        System.out.println(json);


        return json;
    }




    // UTILS
    private boolean notNullAndEmpty(String s){
        return s != null && !s.isEmpty();
    }

    private String saveImage(String owner, String picture, HttpServletRequest request) throws IOException{

        // Remove "data:image/png;base64," header if there is one
        picture = picture.replaceFirst("data:image\\/.*?;base64,", "");
        // Decode Base64 string in byte[]
        byte[] imageBytes = Base64.getDecoder().decode(picture);

        // Generate a unique image name based on author username and recipe ID
        String imageName =  owner.replace(" ","") + "_profilePic.png";

        // Create the full path for saving the image
        Path imagesPath = Paths.get(PROJECT_PATH,"src", "main", "resources", "static", "uploads", "users", imageName);

        // Ensure the directory exists, otherwise create the directory
        Files.createDirectories(imagesPath.getParent());

        // Write the output to the specified path
        Files.write(imagesPath, imageBytes);

        return imageName;

    }

    private void deleteImage(String owner) throws IOException{

        String imageName =  owner.replace(" ","") + "_profilePic.png";


        Path imagesPath = Paths.get(PROJECT_PATH, "src", "main", "resources", "static", "uploads", "users", imageName);

        Files.deleteIfExists(imagesPath);

    }


}

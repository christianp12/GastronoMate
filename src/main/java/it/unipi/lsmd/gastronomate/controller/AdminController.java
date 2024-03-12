package it.unipi.lsmd.gastronomate.controller;

import com.google.gson.Gson;
import it.unipi.lsmd.gastronomate.dto.LoggedUserDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeStatisticDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.model.enums.UserTypeEnum;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.interfaces.StatisticsService;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.CookieService;
import it.unipi.lsmd.gastronomate.service.interfaces.RecipeService;
import it.unipi.lsmd.gastronomate.service.interfaces.ReviewService;
import it.unipi.lsmd.gastronomate.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StatisticsService statisticsService = ServiceLocator.getStatisticsService();
    private final UserService userService = ServiceLocator.getUserService();
    private final CookieService cookieService = ServiceLocator.getCookieService();
    private final Logger applicationLogger = ServiceLocator.getApplicationLogger();

    private boolean notNullAndEmpty(String s){
        return s != null && !s.isEmpty();
    }

    @GetMapping("/login")
    public String loginGET(){
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
                    model.addAttribute("errorMessage", "Wrong username or password");
                    return "loginPage";

                } else {
                    applicationLogger.severe("AdminController: Error while authenticating admin: " + e.getMessage() + " - " + e.getErrorType());
                    return "errorPage";
                }
            }

        } else if (notNullAndEmpty(email) && notNullAndEmpty(password)) {
            try {
                params = userService.authenticate(email, password);

            } catch (BusinessException e) {

                if (e.getErrorType().equals(BusinessTypeErrorsEnum.AUTHENTICATION_ERROR)) {
                    model.addAttribute("errorMessage", "Wrong email or password");
                    return "loginPage";

                } else {
                    applicationLogger.severe("AdminController: Error while authenticating admin: " + e.getMessage() + " - " + e.getErrorType());
                    return "errorPage";
                }
            }
        }
        else {
            return "errorPage";
        }

        LoggedUserDTO loggedUserDTO = new LoggedUserDTO();

        loggedUserDTO.setUsername(params.get("username"));

        if(params.containsKey("type") && params.get("type").equals("ADMIN")){

            loggedUserDTO.setUserType(UserTypeEnum.ADMIN);
        }

        else{
            model.addAttribute("errorMessage", "You are not an admin");
            return "loginPage";
        }


        List<String> values = new ArrayList<>();

        values.add(loggedUserDTO.getUsername());

        if (loggedUserDTO.getUserType() != null)
            values.add(loggedUserDTO.getUserType().toString());

        // Cookie creation
        cookieService.setCookie("logged", values, null, "/", response);

        return "redirect:/admin/";
    }


    @GetMapping("/")
    public String adminDashboard(Model model) {

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        // Future for getYearSubscriptionsData
        Future<Map<String, Integer>> yearSubscriptionsFuture = executorService.submit(() -> {
            try {
                return statisticsService.getYearSubscriptions(2000, 2010);
            } catch (BusinessException e) {

                applicationLogger.severe("AdminController: Error while getting year subscriptions: " + e.getMessage() + " - " + e.getErrorType());
                return Collections.emptyMap();
            }
        });

        // Future for getUsersPerStateData
        Future<Map<String, Integer>> usersPerStateFuture = executorService.submit(() -> {
            try {
                String startDate = "2000-01-01";
                String endDate = "2010-01-01";
                return statisticsService.getUsersPerState(startDate, endDate);

            } catch (BusinessException e) {

                applicationLogger.severe("AdminController: Error while getting users per state: " + e.getMessage() + " - " + e.getErrorType());
                return Collections.emptyMap();
            }
        });

        Future<Double[]> monthlySubscriptionsData = executorService.submit(() -> {
            try {
                return statisticsService.getMontlySubscriptionsPercentage("2000-01-01");

            } catch (BusinessException e) {

                applicationLogger.severe("AdminController: Error while getting monthly subscriptions: " + e.getMessage() + " - " + e.getErrorType());
                return new Double[0];
            }
        });

        // Wait for the completion of both Futures
        try {
            Map<String, Integer> yearSubscriptions = yearSubscriptionsFuture.get();
            Map<String, Integer> usersPerState = usersPerStateFuture.get();
            Double[] monthlySubscriptions = monthlySubscriptionsData.get();
            StringBuilder jsArrayString = new StringBuilder("[");

            for (int i = 0; i < monthlySubscriptions.length; i++) {
                jsArrayString.append((double)monthlySubscriptions[i]);
                if (i < monthlySubscriptions.length - 1) {
                    jsArrayString.append(", ");
                }
            }
            jsArrayString.append("]");



            Gson gson = new Gson();
            String usersPerStateJs = gson.toJson(usersPerState);
            String yearSubscriptionsJs = gson.toJson(yearSubscriptions);

            // Add the data to the model to make it available in the JSP
            model.addAttribute("yearSubscriptions", yearSubscriptionsJs);
            model.addAttribute("usersPerState", usersPerStateJs);
            model.addAttribute("monthlySubscriptions", jsArrayString);

        } catch (InterruptedException | ExecutionException e) {
            applicationLogger.severe("AdminController: Error while getting data for the admin dashboard: " + e.getMessage());
            return "errorPage";
        }

        // Shut down the ExecutorService
        executorService.shutdown();

        return "adminDashboard";
    }


    @GetMapping("/monthlySubscriptions")
    @ResponseBody
    public ResponseEntity<Double[]> getMonthlySubscriptionsData(@RequestParam("start") String start) {
        try {
            Double[] monthlySubscriptionsPercentage = statisticsService.getMontlySubscriptionsPercentage(start);
            return new ResponseEntity<>(monthlySubscriptionsPercentage, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/yearSubscriptions")
    public ResponseEntity<Map<String, Integer>> getYearSubscriptionsData(
            @RequestParam("yearStart") Integer yearStart, @RequestParam("yearEnd") Integer yearEnd) {
        try {
            Map<String, Integer> yearSubscriptions = statisticsService.getYearSubscriptions(yearStart, yearEnd);
            return new ResponseEntity<>(yearSubscriptions, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/usersPerState")
    public ResponseEntity<Map<String, Integer>> getUsersPerStateData(
            @RequestParam("start") String start, @RequestParam("end") String end) {
        try {
            Map<String, Integer> usersPerState = statisticsService.getUsersPerState(start, end);
            return new ResponseEntity<>(usersPerState, HttpStatus.OK);

        } catch (BusinessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/popularKeywords")
    public ResponseEntity<Map<String, Integer>> getPopularKeywordsData(HttpServletRequest request) {
        try {

            String start = request.getParameter("start");
            String end = request.getParameter("end");

            Map<String, Integer> popularKeywords = statisticsService.getMostPupularKeywords(start, end);
            return new ResponseEntity<>(popularKeywords, HttpStatus.OK);

        } catch (BusinessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/bestScoredRecipes")
    public ResponseEntity<String> getBestScoredRecipesData() {
        try {
            List<RecipeStatisticDTO> bestScoredRecipes = statisticsService.getBestScoredRecipes();

            // Return the gson list of best scored recipes
            Gson gson = new Gson();
            return new ResponseEntity<>(gson.toJson(bestScoredRecipes), HttpStatus.OK);

        } catch (BusinessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/influencers")
    public ResponseEntity<String> getInfluencersData() {
        try {
            List<UserSummaryDTO> influencers = statisticsService.getInfluencers();

            // Return the gson list of influencers
            Gson gson = new Gson();
            return new ResponseEntity<>(gson.toJson(influencers), HttpStatus.OK);

        } catch (BusinessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/mostLikedRecipes")
    public ResponseEntity<String> getMostLikedRecipesData() {
        try {
            List<RecipeStatisticDTO> mostLikedRecipes = statisticsService.getMostLikedRecipes();

            // Return the gson map of most liked recipes and their like count
            Gson gson = new Gson();
            return new ResponseEntity<>(gson.toJson(mostLikedRecipes), HttpStatus.OK);

        } catch (BusinessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

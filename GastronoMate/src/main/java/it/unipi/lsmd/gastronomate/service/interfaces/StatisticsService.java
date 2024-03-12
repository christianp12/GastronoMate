package it.unipi.lsmd.gastronomate.service.interfaces;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dto.RecipeStatisticDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StatisticsService {
    /**
     * Retrieves an array containing the monthly subscription percentages starting from the specified date.
     *
     * @param start The start date for calculating monthly subscription percentages.
     * @return An array of Double values representing monthly subscription percentages.
     * @throws BusinessException If an exception occurs during the data access operation.
     */
    Double[] getMontlySubscriptionsPercentage(String start) throws BusinessException;


    /**
     * Retrieves a map containing the number of subscriptions for each year within the specified range.
     *
     * @param yearStart The starting year of the range.
     * @param yearEnd   The ending year of the range.
     * @return A map where keys are years and values are the corresponding number of subscriptions.
     * @throws BusinessException If an exception occurs during the data access operation.
     */
    Map<String, Integer> getYearSubscriptions(Integer yearStart, Integer yearEnd) throws BusinessException;  //GRAFICO A LINEE

    /**
     * Retrieves a map containing the number of users per state within the specified date range.
     *
     * @param start The start date of the range.
     * @param end   The end date of the range.
     * @return A map where keys are state names and values are the corresponding number of users.
     * @throws BusinessException If an exception occurs during the data access operation.
     */
    Map<String, Integer> getUsersPerState(String start, String end) throws BusinessException;

    /**
     * Retrieves a list of the most popular keywords within the specified date range.
     *
     * @param start The start date of the range.
     * @param end   The end date of the range.
     * @return A list of String values representing the most popular keywords.
     * @throws BusinessException If an exception occurs during the data access operation.
     */
    Map<String, Integer> getMostPupularKeywords(String start, String end) throws BusinessException; //SHOW IN A TABLE


    /**
     * Retrieves a list of RecipeSummaryDTO objects representing the best-scored recipes.
     *
     * @return A list of RecipeSummaryDTO objects.
     * @throws BusinessException If an exception occurs during the data access operation.
     */
    List<RecipeStatisticDTO> getBestScoredRecipes() throws BusinessException;

    /**
     * Retrieves a list of UserSummaryDTO objects representing influencers.
     *
     * @return A list of UserSummaryDTO objects.
     * @throws BusinessException If an exception occurs during the data access operation.
     */
    List<UserSummaryDTO> getInfluencers() throws BusinessException; //User summary

    /**
     * Retrieves a list of RecipeStatisticDTO objects representing the most liked recipes.
     *
     * @return A list of RecipeStatisticDTO objects.
     * @throws BusinessException If an exception occurs during the data access operation.
     */
   List<RecipeStatisticDTO> getMostLikedRecipes() throws BusinessException; //Recipe summary

}

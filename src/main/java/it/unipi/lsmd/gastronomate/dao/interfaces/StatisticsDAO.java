package it.unipi.lsmd.gastronomate.dao.interfaces;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dto.RecipeStatisticDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StatisticsDAO {

    /**
     * Retrieves an array containing the monthly subscription percentages starting from the specified date.
     *
     * @param start The start date for calculating monthly subscription percentages.
     * @return An array of Double values representing monthly subscription percentages.
     * @throws DAOException If an exception occurs during the data access operation.
     */
    Double[] getMontlySubscriptionsPercentage(Date start) throws DAOException; //GRAFICO A BARRE


    /**
     * Retrieves a map containing the number of subscriptions for each year within the specified range.
     *
     * @param yearStart The starting year of the range.
     * @param yearEnd   The ending year of the range.
     * @return A map where keys are years and values are the corresponding number of subscriptions.
     * @throws DAOException If an exception occurs during the data access operation.
     */
    Map<String, Integer> getYearSubscriptions(Integer yearStart, Integer yearEnd) throws DAOException;  //GRAFICO A LINEE

    /**
     * Retrieves a map containing the number of users per state within the specified date range.
     *
     * @param start The start date of the range.
     * @param end   The end date of the range.
     * @return A map where keys are state names and values are the corresponding number of users.
     * @throws DAOException If an exception occurs during the data access operation.
     */
    Map<String, Integer> getUsersPerState(Date start, Date end) throws DAOException; //GRAFICO A TORTA

    /**
     * Retrieves a map of the most popular keywords with the scores.
     *
     * @param start The start date of the range.
     * @param end   The end date of the range.
     * @return A map of String and Integer where keys represent the most popular keywords and values represent the total count.
     * @throws DAOException If an exception occurs during the data access operation.
     */
    Map<String, Integer> getMostPupularKeywords(Date start, Date end) throws DAOException; //SHOW IN A TABLE


    /**
     * Retrieves a list of RecipeSummaryDTO objects representing the best-scored recipes.
     *
     * @return A list of RecipeStatisticDTO objects.
     * @throws DAOException If an exception occurs during the data access operation.
     */
    List<RecipeStatisticDTO> getBestScoredRecipes() throws DAOException; //Recipe summary

    /**
     * Retrieves a list of UserSummaryDTO objects representing influencers.
     *
     * @return A list of UserSummaryDTO objects.
     * @throws DAOException If an exception occurs during the data access operation.
     */
    List<UserSummaryDTO> getInfluencers() throws DAOException; //User summary

    /**
     * Retrieves a list of RecipeStatisticDTO objects representing the most liked recipes.
     *
     * @return A list of RecipeStatisticDTO objects.
     * @throws DAOException If an exception occurs during the data access operation.
     */
    List<RecipeStatisticDTO> getMostLikedRecipes() throws DAOException; //Recipe summary

}





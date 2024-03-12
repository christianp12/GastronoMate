package it.unipi.lsmd.gastronomate.service.implementation;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.interfaces.StatisticsDAO;
import it.unipi.lsmd.gastronomate.dto.RecipeStatisticDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.StatisticsService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class StatisticsServiceImpl implements StatisticsService {

    private StatisticsDAO statisticsDAOMongoDB;
    private StatisticsDAO statisticsDAONeo4j;
    private Logger applicationLogger;

    public StatisticsServiceImpl(){
        this.statisticsDAOMongoDB = DAOLocator.getStatisticsDAO(DAOTypeEnum.MONGODB);
        this.statisticsDAONeo4j = DAOLocator.getStatisticsDAO(DAOTypeEnum.NEO4J);

        this.applicationLogger = ServiceLocator.getApplicationLogger();
    }

    @Override
    public Double[] getMontlySubscriptionsPercentage(String dateStart) throws BusinessException {
        try {
            //from the controller, the date is passed as a string in the format "yyyy-MM-dd"

            //we don't need the day, we only need the year and the month

            //try to convert the string to a date

            String[] parts = dateStart.split("-");
            if(parts.length != 3){
                throw new BusinessException("Invalid date format", BusinessTypeErrorsEnum.INVALID_DATA);
            }

            Date date = new Date(Integer.parseInt(parts[0])-1900, Integer.parseInt(parts[1])-1, 1);

            return statisticsDAOMongoDB.getMontlySubscriptionsPercentage(date);

        } catch (DAOException e) {
            applicationLogger.severe("StatisticsServiceImpl: Error while getting monthly subscriptions percentage from MongoDB: " + e.getMessage());
            throw new BusinessException("Error while getting monthly subscriptions percentage from MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public Map<String, Integer> getYearSubscriptions(Integer yearStart, Integer yearEnd) throws BusinessException{
        try {
            return statisticsDAOMongoDB.getYearSubscriptions(yearStart-1900, yearEnd-1900);

        } catch (DAOException e) {
            applicationLogger.severe("StatisticsServiceImpl: Error while getting yearly subscriptions from MongoDB: " + e.getMessage());
            throw new BusinessException("Error while getting yearly subscriptions from MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }

    }

    @Override
    public Map<String, Integer> getUsersPerState(String dateStart, String dateEnd) throws BusinessException{
        try {
            //from the controller, the date is passed as a string in the format "yyyy-MM-dd"

            //we don't need the day, we only need the year and the month

            //try to convert the string to a date

            String[] parts = dateStart.split("-");

            String[] parts2 = dateEnd.split("-");

            if(parts.length != 3 || parts2.length != 3){
                throw new BusinessException("Invalid date format", BusinessTypeErrorsEnum.INVALID_DATA);
            }

            Date date1 = new Date(Integer.parseInt(parts[0])-1900, Integer.parseInt(parts[1])-1, 1);

            Date date2 = new Date(Integer.parseInt(parts2[0])-1900, Integer.parseInt(parts2[1])-1, 1);

            return statisticsDAOMongoDB.getUsersPerState(date1, date2);


        } catch (DAOException e) {
            applicationLogger.severe("StatisticsServiceImpl: Error while getting users per state from MongoDB: " + e.getMessage());
            throw new BusinessException("Error while getting users per state from MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public Map<String, Integer> getMostPupularKeywords(String dateStart, String dateEnd) throws BusinessException {
        try{

            if (dateStart != null && dateEnd != null) {

                String[] parts = dateStart.split("-");

                String[] parts2 = dateEnd.split("-");

                Date date1 = new Date(Integer.parseInt(parts[0])-1900, Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]));

                Date date2 = new Date(Integer.parseInt(parts2[0])-1900, Integer.parseInt(parts2[1])-1, Integer.parseInt(parts[2]));

                return statisticsDAOMongoDB.getMostPupularKeywords(date1, date2);
            }
            else if(dateStart != null){
                String[] parts = dateStart.split("-");
                Date date1 = new Date(Integer.parseInt(parts[0])-1900, Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]));
                return statisticsDAOMongoDB.getMostPupularKeywords(date1, null);
            }
            else if (dateEnd != null){
                String[] parts = dateEnd.split("-");
                Date date2 = new Date(Integer.parseInt(parts[0])-1900, Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]));
                return statisticsDAOMongoDB.getMostPupularKeywords(null, date2);
            }
            else{
                return statisticsDAOMongoDB.getMostPupularKeywords(null, null);
            }

        } catch (DAOException e) {
            applicationLogger.severe("StatisticsServiceImpl: Error while getting most popular keywords from MongoDB: " + e.getMessage());
            throw new BusinessException("Error while getting most popular keywords from MongoDB", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public List<RecipeStatisticDTO> getBestScoredRecipes() throws BusinessException {
       try {
            return statisticsDAOMongoDB.getBestScoredRecipes();

        } catch (DAOException e) {
            applicationLogger.severe("StatisticsServiceImpl: Error while getting best scored recipes from Neo4j: " + e.getMessage());
            throw new BusinessException("Error while getting best scored recipes from Neo4j", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public List<UserSummaryDTO> getInfluencers() throws BusinessException{
        try {
            return statisticsDAONeo4j.getInfluencers();

        } catch (DAOException e) {
            applicationLogger.severe("StatisticsServiceImpl: Error while getting influencers from Neo4j: " + e.getMessage());
            throw new BusinessException("Error while getting influencers from Neo4j", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public List<RecipeStatisticDTO> getMostLikedRecipes() throws BusinessException {
        try {
            return statisticsDAONeo4j.getMostLikedRecipes();

        } catch (DAOException e) {
            applicationLogger.severe("StatisticsServiceImpl: Error while getting most liked recipes from Neo4j: " + e.getMessage());
            throw new BusinessException("Error while getting most liked recipes from Neo4j", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }
}

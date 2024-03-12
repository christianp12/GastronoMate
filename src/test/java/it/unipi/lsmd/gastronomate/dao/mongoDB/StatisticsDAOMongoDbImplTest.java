package it.unipi.lsmd.gastronomate.dao.mongoDB;

import it.unipi.lsmd.gastronomate.dto.RecipeStatisticDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

class StatisticsDAOMongoDbImplTest {

    @BeforeEach
    void setUp() throws Exception {
        MongoDbBaseDAO.openConnection();
    }
    @Test
    void getDashboardStatistics() {
        StatisticsDAOMongoDbImpl statisticsDAOMongoDb = new StatisticsDAOMongoDbImpl();

        try{

            Double[] months = statisticsDAOMongoDb.getMontlySubscriptionsPercentage(new Date(2000-1900,7,1));
            System.out.println(Arrays.toString(months));

            Map<String, Integer> yearSubscriptions = statisticsDAOMongoDb.getYearSubscriptions(2000-1900, 2001-1900);
            System.out.println(yearSubscriptions);

            Map<String, Integer> usersPerState = statisticsDAOMongoDb.getUsersPerState(new Date(2000-1900,0,1), new Date(2001-1900,0,1));
            System.out.println(usersPerState);

           Map<String, Integer> mostPupularKeywords = statisticsDAOMongoDb.getMostPupularKeywords(new Date(2000-1900,0,1), null);
            System.out.println(mostPupularKeywords);

            List<RecipeStatisticDTO> bestScoredRecipes = statisticsDAOMongoDb.getBestScoredRecipes();
            System.out.println(bestScoredRecipes);

       }catch (Exception e){
           throw new RuntimeException(e);
       }
    }

    @Test
    void getMontlySubscriptionsPercentage() {
        StatisticsDAOMongoDbImpl statisticsDAOMongoDb = new StatisticsDAOMongoDbImpl();
        try{
            Date date = new Date(2000-1900,7,1);
            Double[] months = statisticsDAOMongoDb.getMontlySubscriptionsPercentage(date);
            System.out.println(date);
            System.out.println(Arrays.toString(months));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    void usersPerState() {
        StatisticsDAOMongoDbImpl statisticsDAOMongoDb = new StatisticsDAOMongoDbImpl();
        try{
            Map<String, Integer> usersPerState = statisticsDAOMongoDb.getUsersPerState(new Date(2000-1900,1,1), new Date(2001-1900,1,1));
            System.out.println(usersPerState);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
package it.unipi.lsmd.gastronomate.dao.neo4j;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dto.RecipeStatisticDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

class StatisticsDAONeo4jImplTest {

    @BeforeEach
    void setUp() {
        try {
            Neo4jBaseDAO.openConnection();

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testStatisticsDAONeo4jImpl() {
        StatisticsDAONeo4jImpl statisticsDAONeo4jImpl = new StatisticsDAONeo4jImpl();

        try {

           List<UserSummaryDTO> userSummaryDTOS = statisticsDAONeo4jImpl.getInfluencers();
           System.out.println(userSummaryDTOS);

           List<RecipeStatisticDTO> recipeStatisticDTOS = statisticsDAONeo4jImpl.getMostLikedRecipes();
           System.out.println(recipeStatisticDTOS);


        } catch (DAOException e) {
            System.out.println(e.getMessage() + " " + e.getErrorType());

        }
    }


}
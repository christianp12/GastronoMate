package it.unipi.lsmd.gastronomate.dao.neo4j;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.NodeCreationException;
import it.unipi.lsmd.gastronomate.dao.interfaces.StatisticsDAO;
import it.unipi.lsmd.gastronomate.dto.RecipeStatisticDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.types.Node;

import java.util.*;

public class StatisticsDAONeo4jImpl extends Neo4jBaseDAO implements StatisticsDAO {

    @Override
    public Double[] getMontlySubscriptionsPercentage(Date start) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Map<String, Integer> getYearSubscriptions(Integer yearStart, Integer yearEnd) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Map<String, Integer> getUsersPerState(Date start, Date end) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Map<String, Integer> getMostPupularKeywords(Date start, Date end) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public List<RecipeStatisticDTO> getBestScoredRecipes() throws DAOException {
       throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public List<UserSummaryDTO> getInfluencers() throws DAOException {

        List<UserSummaryDTO> influencers = new ArrayList<>();

        try(Session session = getSession()) {

            List<Record> records = session.executeRead(tx -> {
                Result result = tx.run("MATCH (u:User) " +
                        "OPTIONAL MATCH (u)<-[:FOLLOW]-(f:User) " +
                        "WITH u, COUNT(f) * 2 as weightedFollowers " +
                        "OPTIONAL MATCH (u)-[:PUBLISH]->(p:Recipe)<-[:LIKE]-(l:User) " +
                        "WITH u, weightedFollowers, COUNT(l) * 2 as weightedLikes " +
                        "OPTIONAL MATCH (u)-[:PUBLISH]->(p:Recipe)<-[:REVIEW]-(r:User) " +
                        "WITH u, weightedFollowers, weightedLikes, COUNT(r) as reviews " +
                        "RETURN DISTINCT u AS user, (weightedFollowers + weightedLikes + reviews) as influenceScore " +
                        "ORDER BY influenceScore DESC " +
                        "LIMIT 10");

                return result.list();
            });

            records.forEach(record -> {
                try{
                    UserSummaryDTO userSummaryDTO = new UserSummaryDTO();
                    Node userNode = record.get("user").asNode();

                    userSummaryDTO.setUsername(userNode.get("username").asString());
                    userSummaryDTO.setProfilePictureUrl(userNode.get("profilePictureUrl").asString().equals("null") ? null : userNode.get("profilePictureUrl").asString());

                    influencers.add(userSummaryDTO);

                }catch (Exception e){}
            });

        }catch (Neo4jException e){
            getLogger().severe("Error while getting influencers: " + e.getMessage());
            throw new DAOException("Error while getting influencers", ErrorTypeEnum.DATABASE_ERROR);
        }

        return influencers;

    }

    @Override
    public List<RecipeStatisticDTO> getMostLikedRecipes() throws DAOException {

        List<RecipeStatisticDTO> mostLikedRecipes = new ArrayList<>();

        try(Session session = getSession()) {
            List<Record> records = session.executeRead(tx -> {

                Result result = tx.run("MATCH (au:User)-[:PUBLISH]->(r:Recipe)<-[l:LIKE]-(u:User) " +
                        "WITH r, au, COUNT(l) as likes " +
                        "RETURN r, au, likes " +
                        "ORDER BY likes DESC " +
                        "LIMIT 10");

                return result.list();
            });

            records.forEach(record -> {
                try{
                    RecipeStatisticDTO recipeStatisticDTO = new RecipeStatisticDTO();

                    Node recipeNode = record.get("r").asNode();

                    recipeStatisticDTO.setRecipeId(recipeNode.get("documentId").asString().equals("null") ? null : recipeNode.get("documentId").asString());
                    recipeStatisticDTO.setTitle(recipeNode.get("title").asString().equals("null") ? null : recipeNode.get("title").asString());
                    recipeStatisticDTO.setPictureUrl(recipeNode.get("imageUrl").asString().equals("null") ? null : recipeNode.get("imageUrl").asString());

                    recipeStatisticDTO.setAuthorUsername(record.get("au").asNode().get("username").asString().equals("null") ? null : record.get("au").asNode().get("username").asString());
                    recipeStatisticDTO.setAuthorProfilePictureUrl(record.get("au").asNode().get("profilePictureUrl").asString().equals("null") ? null : record.get("au").asNode().get("profilePictureUrl").asString());

                    Integer likes = record.get("likes").asInt();

                    recipeStatisticDTO.setLikes(likes);

                    mostLikedRecipes.add(recipeStatisticDTO);

                }catch (Exception e){}

            });

        }catch (Neo4jException e){
            getLogger().severe("Error while getting most liked recipes: " + e.getMessage());
            throw new DAOException("Error while getting most liked recipes", ErrorTypeEnum.DATABASE_ERROR);
        }

        return mostLikedRecipes;
    }

}

package it.unipi.lsmd.gastronomate.dao.neo4j;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.RelationshipCreationException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.NodeCreationException;
import it.unipi.lsmd.gastronomate.dao.interfaces.RecipeDAO;
import it.unipi.lsmd.gastronomate.dto.PageDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.model.Recipe;
import it.unipi.lsmd.gastronomate.model.user.NormalUser;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.exceptions.TransientException;
import org.neo4j.driver.types.Node;
import javafx.util.Pair;


import javax.management.relation.RelationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.neo4j.driver.Values.parameters;

public class RecipeDAONeo4jImpl extends Neo4jBaseDAO implements RecipeDAO {

    private static final String CREATE_RECIPE_ERR_MSG="RecipeNeo4jDbImpl: createRecipe: Error occurred while inserting a new recipe: ";
    private static final String UPDATE_RECIPE_ERR_MSG="RecipeNeo4jDbImpl: updateRecipe: Error occurred while updating: ";
    private static final String DELETE_RECIPE_ERR_MSG="RecipeNeo4jDbImpl: deleteRecipe: Error occurred while deleting recipe with id: ";
    private static final String SUGGESTED_RECIPES_ERR_MSG="RecipeNeo4jDbImpl: suggestedRecipes: Error occurred while suggesting recipes for user: ";
    private static final String FOLLOWED_USERS_RECIPES_ERR_MSG="RecipeNeo4jDbImpl: FollowedUsersRecipe: Error occurred while suggesting recipes for user: ";

    @Override
    public void createRecipe(RecipeDTO recipe) throws DAOException {
        try(Session session = getSession()){

            StringBuilder query = new StringBuilder("CREATE (r:Recipe {documentId: $recipeId, title: $title");

            if(recipe.getKeywords()!=null)
                query.append(", keywords: $keywords");
            if(recipe.getPictureUrl()!=null)
                query.append(", imageUrl: $imageUrl");

            query.append("}) RETURN r");

            Map<String,Object> params = new HashMap<>();

            params.put("recipeId", recipe.getRecipeId());
            params.put("title", recipe.getTitle());

            if(recipe.getKeywords()!=null)
                params.put("keywords", recipe.getKeywords());
            if(recipe.getPictureUrl()!=null)
                params.put("imageUrl", recipe.getPictureUrl());

            session.executeWrite(tx -> {
               Boolean created = tx.run(query.toString(),params).hasNext();

               if(!created)
                   throw new NodeCreationException("Recipe not created");

                // Edge from user to recipe
                Boolean published = tx.run("MATCH (u:User {username: $username}), (r:Recipe {documentId: $recipeId}) " +
                                "CREATE (u)-[p:PUBLISH {when: $when} ]->(r) RETURN p",
                        parameters("username", recipe.getAuthorUsername(), "recipeId", recipe.getRecipeId(), "when", recipe.getDatePublished())).hasNext();

                if(!published)
                    throw new RelationshipCreationException("Recipe not published");

                return null;
            });

        } catch (TransientException e) {
            getLogger().severe(CREATE_RECIPE_ERR_MSG + recipe.getRecipeId() + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);

        } catch (NodeCreationException | RelationshipCreationException e) {

            getLogger().severe(e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);

        } catch (Neo4jException e) {
            getLogger().severe(CREATE_RECIPE_ERR_MSG + recipe.getRecipeId() + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(CREATE_RECIPE_ERR_MSG + recipe.getRecipeId() + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public RecipeDTO readRecipe(String recipeId) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Boolean likedRecipe(String recipeId, String username) throws DAOException {
        try(Session session = getSession()){

            Boolean liked = session.executeRead(tx -> {
                Result result = tx.run("MATCH (:User {username: $username})-[l:LIKE]->(:Recipe {documentId: $recipeId}) RETURN l",
                        parameters("username", username, "recipeId", recipeId));
                return result.hasNext();
            });

            if (liked == null)
                throw new Neo4jException("Cannot check if user " + username + " liked recipe " + recipeId);

            return liked;

        } catch (Neo4jException e) {

            getLogger().severe("RecipeDAONeo4j: Error while checking if user " + username + " liked recipe " + recipeId + ": " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe("RecipeDAONeo4j: Error while checking if user " + username + " liked recipe " + recipeId + ": " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
            throw ex;
        }
    }

    //recipeId is distinct from the updateParams to make the recipeId necessary in the signature of the function
    @Override
    public void updateRecipe(String recipeId, Map<String, Object> updateParams) throws DAOException {

        Map<String, Object> newParams = new HashMap<>();

        try (Session session = getSession()) {

             Boolean exists = session.executeWrite(tx -> {


                if(updateParams.containsKey("title")){
                    newParams.put("title", updateParams.get("title"));
                }

                if(updateParams.containsKey("Keywords")){
                    newParams.put("keywords", updateParams.get("Keywords"));
                }

                if(updateParams.containsKey("ImageUrl")){
                    newParams.put("imageUrl", updateParams.get("ImageUrl"));
                }

                newParams.put("recipeId", recipeId); // Add recipeId to updateParams

                StringBuilder query = new StringBuilder("MATCH (r:Recipe {documentId: $recipeId}) SET ");
                newParams.forEach((key, value) -> query.append("r.").append(key).append(" = $").append(key).append(", "));
                query.setLength(query.length() - 2); // Remove last comma and space
                query.append(" RETURN r");
                return tx.run(query.toString(), newParams).hasNext();

            });

             if (!exists) {
                 //maybe the recipe has not been added yet
                 throw new NodeCreationException("Recipe not found");
             }

             //before returning, check if the update has run successfully

             if (!checkUpdate(newParams)) {
                throw new NodeCreationException("Recipe not updated");
            }

        }catch (TransientException e) {
            getLogger().severe(UPDATE_RECIPE_ERR_MSG + recipeId + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);

        }catch (NodeCreationException e){
            //mark the exception as Transient to be retried
            //after 3 unsuccessful attempts the task will be marked as failed and removed from the queue
            getLogger().severe(e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);

        }catch (Neo4jException e){
            getLogger().severe(UPDATE_RECIPE_ERR_MSG + recipeId + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);
        }

        catch (Exception e){
            getLogger().severe(UPDATE_RECIPE_ERR_MSG + recipeId + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    private Boolean checkUpdate(Map<String, Object> updateParams) throws DAOException {
        //try to find a recipe with new parameters
        //if it exists, the update has run successfully
        //if it doesn't exist, the update has failed

        StringBuilder query = new StringBuilder("MATCH (r:Recipe {documentId: $recipeId}) WHERE ");
        updateParams.forEach((key, value) -> query.append("r.").append(key).append(" = $").append(key).append(" AND "));
        query.setLength(query.length() - 5); // Remove last AND and space
        query.append(" RETURN r");

        try (Session session = getSession()) {

            return session.executeRead(tx -> tx.run(query.toString(), updateParams).hasNext());

        } catch (Neo4jException e) {

            getLogger().severe("RecipeDAONeo4j: Error while checking if recipe has been updated: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);
        }
    }

    @Override
    public List<RecipeSummaryDTO> searchFirstNRecipes(String query, Integer n, String loggedUser) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void deleteRecipe(String recipeId) throws DAOException {
        try (Session session = getSession()) {

            Boolean exists = session.executeWrite(tx -> tx.run("MATCH (r:Recipe {documentId: $recipeId}) DETACH DELETE r RETURN r ",
                    parameters("recipeId", recipeId)).hasNext());

            if (!exists) {
                throw new NodeCreationException("Recipe not deleted");
            }

        }catch (TransientException e){
            getLogger().severe(DELETE_RECIPE_ERR_MSG + recipeId + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);

        }catch (NodeCreationException e){
            //mark the exception as Transient to be retried
            //after 3 unsuccessful attempts the task will be marked as failed and removed from the queue
            getLogger().severe(e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);

        }catch (Neo4jException e){

            getLogger().severe(DELETE_RECIPE_ERR_MSG + recipeId + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);
        }

        catch (Exception e){
            getLogger().severe(DELETE_RECIPE_ERR_MSG + recipeId + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }

    }

    @Override
    public List<RecipeSummaryDTO> suggestedRecipes(String username, int limit) throws DAOException {
        try (Session session = getSession()) {

            List<Record> records = session.executeRead(tx -> {

                String query =
                        "MATCH (u:User {username: $username})-[:FOLLOW]->()-[l:LIKE]->(recipe:Recipe)<-[p:PUBLISH]-(author:User)" +
                                "WHERE NOT (u)-[:LIKE]->(recipe) " +
                                "WITH recipe, count(l) AS Likes, p.when AS date, author " +
                                "WHERE Likes >= 10 " +
                                "RETURN DISTINCT recipe, date " +
                                "ORDER BY date DESC " +
                                "LIMIT $n";

                Result r = tx.run(query, parameters("username", username, "n", limit));

                return r.list();

            });

            if(records.isEmpty() || records.size() < limit/2){
                //if no recipes are found, return the most liked recipes
                records = session.executeRead(tx -> {

                    String query =
                            "MATCH ()-[l:LIKE]->(recipe:Recipe)<-[p:PUBLISH]-(author:User) " +
                                    "WHERE NOT (:User {username: $username})-[:LIKE]->(recipe) " +
                                    "WITH recipe, count(l) AS Likes, p.when AS date, author " +
                                    "RETURN DISTINCT recipe, date, Likes " +
                                    "ORDER BY Likes DESC, date DESC " +
                                    "LIMIT $n";

                    Result r = tx.run(query, parameters("username", username, "n", limit));

                    return r.list();

                });
            }

            List<Recipe> recipeList = records.stream().map(this::mapRecordToRecipe).toList();

            return recipeList.stream().map(RecipeSummaryDTO::fromRecipe).toList();

        }catch (Neo4jException e){
            getLogger().severe(SUGGESTED_RECIPES_ERR_MSG + username + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);
        }

        catch (Exception e){
            getLogger().severe(SUGGESTED_RECIPES_ERR_MSG + username + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }

    }
    @Override
    public PageDTO< Pair<RecipeSummaryDTO, Boolean> > followedUsersRecipe(int page, String username) throws DAOException {

        PageDTO< Pair<RecipeSummaryDTO, Boolean> > pageDTO = new PageDTO<>();

        int pageOffset = (page - 1) * PageDTO.getPAGE_SIZE();

        try (Session session = getSession()) {

            List<Record> records = session.executeRead(tx -> {

                String query =
                        "MATCH (:User {username: $username})-[:FOLLOW]->(followed:User)-[p:PUBLISH]->(recipe:Recipe) "+
                        "RETURN followed.username AS authorUsername, followed.profilePictureUrl AS authorProfilePictureUrl, recipe, p.when as date, "+
                         "EXISTS ((:User {username: $username})-[:LIKE]->(recipe)) AS liked "+
                                "ORDER BY date DESC SKIP $pageOffset LIMIT $size";

                return  tx.run(query, parameters("username", username, "pageOffset", pageOffset, "size", PageDTO.getPAGE_SIZE())).list();

            });

            List<Pair<RecipeSummaryDTO, Boolean>> items = new ArrayList<>();

           for (Record record : records) {
               Recipe recipe = mapRecordToRecipe(record);
               if (recipe != null) {
                   RecipeSummaryDTO recipeSummaryDTO = RecipeSummaryDTO.fromRecipe(recipe);
                   Boolean liked = record.get("liked").asBoolean();
                     items.add(new Pair<>(recipeSummaryDTO, liked));

               }
           }

           int recipeCount = session.executeRead(tx -> {
               Result result = tx.run("MATCH (:User {username: $username})-[:FOLLOW]->()-[:PUBLISH]->(recipe:Recipe) " +
                               "RETURN count(recipe) as totalCount",
                       parameters("username", username));
               return result.single().get("totalCount").asInt();
           });

           pageDTO.setEntries(items);
           pageDTO.setTotalCount(recipeCount);
           pageDTO.setCurrentPage(page);

           return pageDTO;

        }catch (Neo4jException e){
            getLogger().severe(FOLLOWED_USERS_RECIPES_ERR_MSG + username + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);
        }

        catch (Exception e){
            getLogger().severe(FOLLOWED_USERS_RECIPES_ERR_MSG + username + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public void likeRecipe(String recipeId, String username) throws DAOException {
        try(Session session = getSession()){

          session.executeWrite(tx -> tx.run("MATCH (u:User {username: $username}), (r:Recipe {documentId: $recipeId}) "+
                            "WHERE NOT (u)-[:LIKE]->(r) CREATE (u)-[:LIKE {when: $when} ]->(r)",
                   parameters("username", username, "recipeId", recipeId, "when", LocalDateTime.now())).consume());


        } catch (Neo4jException e) {

            getLogger().severe("RecipeDAONeo4j: Error while adding like relationship between user " + username + " and recipe " + recipeId + ": " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe("RecipeDAONeo4j: Error while adding like relationship between user " + username + " and recipe " + recipeId + ": " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
            throw ex;
        }
    }
    @Override
    public void unlikeRecipe(String recipeId, String username) throws DAOException {
        try(Session session = getSession()){

            session.executeWrite(tx -> tx.run("MATCH (u:User {username: $username})-[l:LIKE]->(r:Recipe {documentId: $recipeId}) " +
                            "DELETE l",
                    parameters("username", username, "recipeId", recipeId)).consume());


        } catch (Neo4jException e) {

            getLogger().severe("RecipeDAONeo4j: Error while deleting like relationship between user " + username + " and recipe " + recipeId + ": " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe("RecipeDAONeo4j: Error while deleting like relationship between user " + username + " and recipe " + recipeId + ": " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
            throw ex;
        }
    }

    @Override
    public Integer getNumOfLikes(String recipeId) throws DAOException {
        try(Session session = getSession()){

            Integer numOfLikes = session.executeRead(tx -> {
                Result result = tx.run("MATCH (:Recipe {documentId: $recipeId})<-[l:LIKE]-() RETURN count(l) as numOfLikes",
                        parameters("recipeId", recipeId));
                return result.single().get("numOfLikes").asInt();
            });

            if (numOfLikes == null) {
                throw new Neo4jException("Recipe not found");
            }

            return numOfLikes;

        } catch (Neo4jException e) {

            getLogger().severe("RecipeDAONeo4j: Error while getting number of likes for recipe " + recipeId + ": " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe("RecipeDAONeo4j: Error while getting number of likes for recipe " + recipeId + ": " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
            throw ex;
        }
    }

    @Override
    public Integer getNumOfReviews(String recipeId) throws DAOException {
        try(Session session = getSession()){


            Integer numOfReviews = session.executeRead(tx -> {
                Result result = tx.run("MATCH (:Recipe {documentId: $recipeId})<-[l:REVIEW]-() RETURN count(l) as numOfReviews",
                        parameters("recipeId", recipeId));
                return result.single().get("numOfReviews").asInt();
            });

            if (numOfReviews == null) {
                throw new Neo4jException("Recipe not found");
            }

            return numOfReviews;

        } catch (Neo4jException e) {

            getLogger().severe("RecipeDAONeo4j: Error while getting number of reviews for recipe " + recipeId + ": " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe("RecipeDAONeo4j: Error while getting number of reviews for recipe " + recipeId + ": " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
            throw ex;
        }
    }

    @Override
    public void updateNumOfLikes(String recipeId, Integer likes) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void updateNumOfReviews(String recipeId, Integer reviews) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void updateAuthorRedundantData(UserSummaryDTO userSummaryDTO, String oldUsername) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void deleteRecipesWithNoAuthor() throws DAOException {
        try(Session session = getSession()){

            session.executeWrite(tx -> {
                tx.run("MATCH (r:Recipe) WHERE NOT (r)<-[:PUBLISH]-() DETACH DELETE r").consume();
                return null;
            });

        } catch (Neo4jException e) {
            getLogger().severe("RecipeDAONeo4j: Error while deleting recipes with no author: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe("RecipeDAONeo4j: Error while deleting recipes with no author: " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
            throw ex;
        }
    }

    private Recipe mapRecordToRecipe(Record record) {

        Recipe recipe = new Recipe();
        NormalUser author = new NormalUser();
        recipe.setAuthor(author);

        Node recipeNode;

        try {
             recipeNode = record.get("recipe").asNode();
        } catch (Exception e) {
            return null;
        }

        try {
            recipe.setId( (recipeNode.get("documentId").asString()).equals("null")? null:recipeNode.get("documentId").asString());
        } catch (Exception e) {
        }

        try {
            recipe.setTitle( (recipeNode.get("title").asString()).equals("null")? null:recipeNode.get("title").asString());
        } catch (Exception e) {

        }

        try {
            recipe.setKeywords(recipeNode.get("keywords").asList().stream().map(Object::toString).collect(Collectors.toList()));
        } catch (Exception e) {

        }

        try {
            recipe.setPictureUrl((recipeNode.get("imageUrl").asString()).equals("null")? null:recipeNode.get("imageUrl").asString());
        } catch (Exception e) {
        }

        try {
            recipe.getAuthor().setUsername((record.get("authorUsername").asString()).equals("null")? null:record.get("authorUsername").asString());
        } catch (Exception e) {
        }

        try {
            recipe.getAuthor().setProfilePictureUrl((record.get("authorProfilePictureUrl").asString()).equals("null")? null:record.get("authorProfilePictureUrl").asString());
        } catch (Exception e) {
        }

        try {
            recipe.setDatePublished(record.get("date").asLocalDateTime());
        } catch (Exception e) {

        }

        return recipe;
    }

}

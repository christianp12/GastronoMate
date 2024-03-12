package it.unipi.lsmd.gastronomate.dao.neo4j;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.RelationshipCreationException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.ReviewDAO;
import it.unipi.lsmd.gastronomate.dto.PageDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.ReviewDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.exceptions.TransientException;
import org.neo4j.driver.types.Node;

import java.time.LocalDateTime;

import static org.neo4j.driver.Values.parameters;

public class ReviewDAONeo4jImpl extends Neo4jBaseDAO implements ReviewDAO {
    @Override
    public void createReview(ReviewDTO reviewDTO) throws DAOException {
        try (Session session = getSession()) {
            // Check if the recipe exists
            Node recipeNode = session.executeRead(tx -> {
                Result result = tx.run(
                        "MATCH (r:Recipe {documentId: $recipeId}) RETURN r",
                        parameters("recipeId", reviewDTO.getRecipeId())
                );
                if (result.hasNext()) {
                    return result.next().get("r").asNode();

                } else {
                    throw new Neo4jException("Recipe " + reviewDTO.getRecipeId() + " does not exist");
                }
            });

            //extract the recipe id from the node
            String recipeId = recipeNode.get("documentId").asString();

            session.writeTransaction(tx -> {

                Boolean created = tx.run(
                        "MATCH (u:User {username: $authorUsername}), (r:Recipe {documentId: $recipeId}) " +
                                "CREATE (u)-[re:REVIEW {when: $when} ]->(r) RETURN re",
                        parameters("authorUsername", reviewDTO.getAuthorUsername(), "recipeId", recipeId, "when", reviewDTO.getDatePublished())
                ).hasNext();

                if (!created) {
                    throw new RelationshipCreationException("Error while creating review relationship between user " + reviewDTO.getAuthorUsername() + " and recipe " + reviewDTO.getRecipeId());
                }

                return null;
            });

        } catch (TransientException e) {
            getLogger().severe("ReviewDAONeo4j: Error while adding review relationship between user " + reviewDTO.getAuthorUsername() + " and recipe " + reviewDTO.getRecipeId() + ": " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);
            throw ex;

        }catch (RelationshipCreationException e){
            getLogger().severe("ReviewDAONeo4j: Error while adding review relationship between user " + reviewDTO.getAuthorUsername() + " and recipe " + reviewDTO.getRecipeId() + ": " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);
            throw ex;

        } catch (Neo4jException e) {
            getLogger().severe("ReviewDAONeo4j: Error while adding review relationship between user " + reviewDTO.getAuthorUsername() + " and recipe " + reviewDTO.getRecipeId() + ": " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);
            throw ex;

        } catch (Exception e) {
            getLogger().severe("ReviewDAONeo4j: Error while adding review relationship between user " + reviewDTO.getAuthorUsername() + " and recipe " + reviewDTO.getRecipeId() + ": " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
            throw ex;
        }
    }

    @Override
    public void deleteReviewsWithNoRecipe() throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void deleteReviewsWithNoAuthor() throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void deleteReview(String reviewId, String authorUsername, String recipeId) throws DAOException {
        try (Session session = getSession()) {

          Boolean deleted = session.executeWrite(tx -> tx.run(
                    "MATCH (u:User {username: $authorUsername})-[r:REVIEW]->(re:Recipe {documentId: $recipeId}) DELETE r RETURN r",
                    parameters("authorUsername", authorUsername, "recipeId", recipeId)

          ).hasNext());

          if(!deleted) {
              throw new RelationshipCreationException("Error while deleting review relationship between user " + authorUsername + " and recipe " + recipeId);
          }


        } catch (TransientException e) {
            getLogger().severe("ReviewDAONeo4j: Error while deleting review relationship between user " + authorUsername + " and recipe " + recipeId + ": " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);
            throw ex;

        } catch (RelationshipCreationException e){
            getLogger().severe("ReviewDAONeo4j: Error while deleting review relationship between user " + authorUsername + " and recipe " + recipeId + ": " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);
            throw ex;

        } catch (Neo4jException e) {

            getLogger().severe("ReviewDAONeo4j: Error while deleting review relationship between user " + authorUsername + " and recipe " + recipeId + ": " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);
            throw ex;

        } catch (Exception e) {
            getLogger().severe("ReviewDAONeo4j: Error while deleting review relationship between user " + authorUsername + " and recipe " + recipeId + ": " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
            throw ex;
        }
    }

    @Override
    public Boolean isReviewed(String recipeId, String username) throws DAOException{
        //return true if the user has already reviewed the recipe
        try (Session session = getSession()) {
            return session.executeRead( tx -> tx.run(
                   "MATCH (u:User {username: $username})-[r:REVIEW]->(re:Recipe {documentId: $recipeId}) RETURN r",
                   parameters("username", username, "recipeId", recipeId)
           ).hasNext());

        } catch (Neo4jException e) {
            getLogger().severe("ReviewDAONeo4j: Error while checking if user " + username + " has reviewed recipe " + recipeId + ": " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe("ReviewDAONeo4j: Error while checking if user " + username + " has reviewed recipe " + recipeId + ": " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public void updateAverageRating() throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void updateReview(String reviewId, String reviewText, Integer reviewRating) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public PageDTO<ReviewDTO> getReviewsByRecipe(String recipeId, int page) {
        throw new UnsupportedOperationException("Not supported");
    }


    @Override
    public void updateRecipeRedundantData(RecipeSummaryDTO recipeSummaryDTO) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void updateAuthorRedundantData(UserSummaryDTO userSummaryDTO, String oldUsername) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

}


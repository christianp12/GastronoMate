package it.unipi.lsmd.gastronomate.dao.neo4j;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.NodeCreationException;
import it.unipi.lsmd.gastronomate.dao.interfaces.UserDAO;
import it.unipi.lsmd.gastronomate.dto.LoggedUserDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.model.Address;
import it.unipi.lsmd.gastronomate.model.user.NormalUser;
import it.unipi.lsmd.gastronomate.model.user.User;
import javafx.util.Pair;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.ClientException;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.exceptions.TransientException;
import org.neo4j.driver.types.Node;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

public class UserDAONeo4jImpl extends Neo4jBaseDAO implements UserDAO {
    private static final String CREATE_USER_ERR_MSG = "UserDAONeo4jImpl: createUser: Error occurred while inserting a new user: ";
    private static final String UPDATE_USER_ERR_MSG = "UserDAONeo4jImpl: updateUser: Error occurred while updating a user with username: ";
    private static final String DELETE_USER_ERR_MSG = "UserDAONeo4jImpl: deleteUser: Error occurred while deleting user with username: ";
    private static final String SUGGESTED_USERS_ERR_MSG="UserDAONeo4jImpl: suggestedUsers: Error occurred while suggesting new users: ";

    @Override
    public void createUser(UserDTO user) throws DAOException {
        try (Session session = getSession()) {

            StringBuilder query = new StringBuilder("CREATE (u:User {documentId: $userId, username: $username, city: $city");

            if (user.getProfilePictureUrl()!=null)
                query.append(", profilePictureUrl: $profilePictureUrl");

            query.append("}) RETURN u");

            Map<String, Object> params = new HashMap<>();

            params.put("userId", user.getId());
            params.put("username", user.getUsername());
            params.put("city", user.getAddress().getCity());

            if (user.getProfilePictureUrl()!=null)
                params.put("profilePictureUrl", user.getProfilePictureUrl());


            session.executeWrite(tx -> {
               Boolean created = tx.run(query.toString(), params).hasNext();

               if(!created)
                   throw new NodeCreationException("Error while creating user node with username " + user.getUsername());

                return null;
            });

        } catch (TransientException e) {
            getLogger().severe(CREATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);

        }catch (NodeCreationException e){
            getLogger().severe("UserDAONeo4jImpl: createUser: Error occurred while creating user node with username " + user.getUsername() + ": " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);

        } catch (Neo4jException e) {
            getLogger().severe(CREATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(CREATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public UserDTO readUser(String username) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public UserDTO readLoggedUserProfile(String username) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void updateUser(String userId, Map<String, Object> updateParams) throws DAOException {

        try (Session session = getSession()) {

            Map<String, Object> newParams = new HashMap<>();

            Boolean exists = session.executeWrite(tx -> {

                if (updateParams.containsKey("username")) {
                    newParams.put("username", updateParams.get("username"));
                }
                if (updateParams.containsKey("ProfilePictureUrl")) {
                    newParams.put("profilePictureUrl", updateParams.get("ProfilePictureUrl"));
                }
                if (updateParams.containsKey("Address.City")) {
                    newParams.put("city", updateParams.get("Address.City"));
                }

                newParams.put("userId", userId); // Add username to newParams
                StringBuilder query = new StringBuilder("MATCH (u:User {documentId: $userId}) SET ");
                newParams.forEach((key, value) -> query.append("u.").append(key).append(" = $").append(key).append(", "));
                query.setLength(query.length() - 2); // Remove last comma and space
                query.append(" RETURN u as user");
                return tx.run(query.toString(), newParams).hasNext();
            });

            if (!exists){
                throw new Neo4jException("User not found");
            }

            //before returning, check if the username has run successfully
            if (!checkUpdate(newParams)) {
                throw new NodeCreationException("Recipe not updated");
            }

        } catch (TransientException e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);

        }catch (NodeCreationException e){
            getLogger().severe("UserDAONeo4jImpl: updateUser: Error occurred while updating a user with username: " + userId + ": " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);

        } catch (Neo4jException e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    private boolean checkUpdate(Map<String, Object> updateParams) throws DAOException {
        //try to find a user with new parameters
        //if it exists, the update has run successfully
        //if it doesn't exist, the update has failed

        StringBuilder query = new StringBuilder("MATCH (u:User {documentId: $userId}) WHERE ");
        updateParams.forEach((key, value) -> query.append("u.").append(key).append(" = $").append(key).append(" AND "));
        query.setLength(query.length() - 5); // Remove last AND and space
        query.append(" RETURN u");

        try (Session session = getSession()) {

            return session.executeRead(tx -> tx.run(query.toString(), updateParams).hasNext());

        } catch (Neo4jException e) {

            getLogger().severe("UserDAONeo4jImpl: checkUpdate: Error occurred while checking if the user has been updated: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);
        }
    }

    @Override
    public void deleteUser(String username) throws DAOException {
        try (Session session = getSession()) {

            Boolean exists = session.executeWrite(tx -> tx.run(
                    "MATCH (u:User {username: $username}) DETACH DELETE u RETURN u",
                    parameters("username", username)).hasNext());

            if (!exists){
                throw new NodeCreationException("Error while deleting user with username " + username);
            }


        } catch (TransientException e) {
            getLogger().severe(DELETE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);

        }catch (NodeCreationException e){
            getLogger().severe("UserDAONeo4jImpl: deleteUser: Error occurred while deleting user with username: " + username + ": " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.TRANSIENT_ERROR);

        } catch (Neo4jException e) {
            getLogger().severe(DELETE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(DELETE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public Map<String, String> authenticate(String username, String password){
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void followUser(String username, String followedUsername) throws DAOException {
        try(Session session = getSession()){

           session.executeWrite(tx -> tx.run(
                    "MATCH (u:User {username: $username}), (f:User {username: $followedUsername}) " +
                            "WHERE NOT (u)-[:FOLLOW]->(f) CREATE (u)-[:FOLLOW]->(f)",
                    parameters("username", username, "followedUsername", followedUsername)).consume());



        }  catch (Neo4jException e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public void unfollowUser(String username, String followedUsername) throws DAOException {
        try (Session session = getSession()) {

            session.executeWrite(tx -> tx.run(
                    "MATCH (:User {username: $username})-[f:FOLLOW]->(:User {username: $followedUsername}) DELETE f",
                    parameters("username", username, "followedUsername", followedUsername)).consume());


        } catch (Neo4jException e) {
            getLogger().severe(DELETE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(DELETE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public Integer getNumOfFollowers(String username) throws DAOException {
        try(Session session = getSession()){

            Integer followers = session.executeRead(tx -> {
                return tx.run(
                        "MATCH (u:User {username: $username})<-[f:FOLLOW]-() RETURN COUNT(f) AS followers",
                        parameters("username", username)).single().get("followers").asInt();
            });

            if (followers == null)
                throw new Neo4jException("Cannot get number of followers");

            return followers;

        } catch (Neo4jException e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public Integer getNumOfFollowed(String username) throws DAOException {
       try(Session session = getSession()){

            Integer followed = session.executeRead(tx -> {
                return tx.run(
                        "MATCH (u:User {username: $username})-[f:FOLLOW]->() RETURN COUNT(f) AS followed",
                        parameters("username", username)).single().get("followed").asInt();
            });

            if (followed == null)
                throw new Neo4jException("Cannot get number of followed");

            return followed;

        } catch (Neo4jException e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public void updateFollowers(String username, Integer followers) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void updateFollowed(String username, Integer followed) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public List<UserSummaryDTO> searchFirstNUsers(String query, Integer n, String loggedUser) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public List<UserSummaryDTO> suggestedUsers(String username, int limit) throws DAOException {
        try (Session session = getSession()) {

            LocalDateTime today = LocalDateTime.now();


            List<Record> records = session.executeRead(tx -> {

                String query =
                        "MATCH (user:User {username: $username})-[l:LIKE]->(:Recipe)<-[:PUBLISH]-(similarUser:User) "+
                                "WHERE l.when >= $likeStart AND l.when <= $likeEnd AND NOT (user)-[:FOLLOW]->(similarUser) AND user <> similarUser RETURN DISTINCT similarUser AS user LIMIT $n ";

                return tx.run(query,
                        parameters("username", username, "likeStart",  today.minusDays(15), "likeEnd", today, "n", limit)).list();
            });

            //if no records are found, try to suggest users based on common-liked recipes
            //if the number of records is less than the 50%, try to suggest users based on common-liked recipes
            if (records.isEmpty() || records.size() < limit/2){

                records = session.executeRead(tx -> {
                    return tx.run(
                            "MATCH (user:User {username: $username})-[:LIKE]->(r:Recipe)<-[:LIKE]->(u:User)"+
                                    "WHERE NOT (user)-[:FOLLOW]->(u) "+
                                    "WITH u, user, COUNT(r) AS commonLikedRecipes "+
                                    "WHERE  commonLikedRecipes > 5 "+
                                    "RETURN DISTINCT u AS user LIMIT $n ",
                            parameters("username", username, "n", limit)).list();
                });
            }
            //if no records are found, try to suggest users based on followers
            //if the number of records is less than the 50%, try to suggest users based on followers
            if (records.isEmpty() || records.size() < limit/2){

                records = session.executeRead(tx -> {
                    return tx.run(
                            "MATCH (user:User)<-[f:FOLLOW]-(user2: User)"+
                                    "WHERE user.username <> $username AND user2.username <> $username "+
                                    "WITH user, COUNT(f) AS Followers "+
                                    "RETURN user ORDER BY Followers DESC LIMIT $n",
                            parameters("username", username, "n", limit)).list();
                });
            }

            List<User> userList = records.stream().map(this::mapRecordToUser).toList();

            return userList.stream().map(UserSummaryDTO::fromUser).toList();

        }catch (Neo4jException e){
                getLogger().severe(SUGGESTED_USERS_ERR_MSG + username + e.getMessage());
                throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(SUGGESTED_USERS_ERR_MSG + username + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }

    }

    @Override
    public List<UserSummaryDTO> showListOfFollowedUsers(String loggedUser, String username) throws DAOException {
        try(Session session = getSession()){

            List<Record> records = session.executeRead(tx -> {
                return tx.run(
                        "MATCH (u:User {username: $username})-[:FOLLOW]->(followed:User) "+
                                "WHERE followed.username <> $loggedUser "+
                                "RETURN followed AS user",
                        parameters("username", username, "loggedUser", loggedUser )).list();
            });


            List<User> userList = records.stream().map(this::mapRecordToUser).toList();

            return userList.stream().map(UserSummaryDTO::fromUser).toList();

        } catch (Neo4jException e) {

            getLogger().severe("UserDAONeo4jImpl: showListOfFollowedUsers: Error occurred while showing list of followed users: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe("UserDAONeo4jImpl: showListOfFollowedUsers: Error occurred while showing list of followed users: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public List<UserSummaryDTO> showListOfFollowers(String loggedUser, String username) throws DAOException {
        try(Session session = getSession()){

            List<Record> records = session.executeRead(tx -> {
                return tx.run(
                        "MATCH (u:User {username: $username})<-[:FOLLOW]-(follower:User) "+
                                "WHERE follower.username <> $loggedUser "+
                                "RETURN follower AS user",
                        parameters("username", username,"loggedUser", loggedUser)).list();
            });

            List<User> userList = records.stream().map(this::mapRecordToUser).toList();

            return userList.stream().map(UserSummaryDTO::fromUser).toList();

        } catch (Neo4jException e) {
            getLogger().severe("Neo4jUserDAOImpl: showListOfFollowers: Error occurred while showing list of followers: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe("Neo4jUserDAOImpl: showListOfFollowers: Error occurred while showing list of followers: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public void addRecipeToUser(RecipeSummaryDTO recipe) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void removeRecipeFromUser(String username, String recipeId) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void updateRecipeInUser(RecipeSummaryDTO recipe) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public Boolean isFollowed(String loggedUser, String username) throws DAOException{
        try(Session session = getSession()){

            Boolean isFollowed = session.executeRead(tx -> tx.run(
                    "MATCH (u:User {username: $username})<-[f:FOLLOW]-(:User {username: $loggedUser}) RETURN f",
                    parameters("username", username, "loggedUser", loggedUser)).hasNext());

            return isFollowed;

        } catch (Neo4jException e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    private User mapRecordToUser(Record record) {
        NormalUser user = new NormalUser();
        Address address = new Address();
        user.setAddress(address);

        Node userNode;

        try {
            userNode = record.get("user").asNode();
        } catch (Exception e) {
            return null;
        }

        try {
             user.setUsername( (userNode.get("username").asString()).equals("null") ? null : userNode.get("username").asString());
        }catch (Exception e){}

        try{
            user.setProfilePictureUrl( (userNode.get("profilePictureUrl").asString()).equals("null") ? null : userNode.get("profilePictureUrl").asString());
        }catch (Exception e){}

        try {
            user.getAddress().setCity( (userNode.get("city").asString()).equals("null") ? null : userNode.get("city").asString());
        }catch (Exception e){}

        return user;
    }


}
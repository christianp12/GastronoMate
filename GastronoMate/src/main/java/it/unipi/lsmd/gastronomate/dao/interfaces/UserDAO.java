package it.unipi.lsmd.gastronomate.dao.interfaces;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dto.LoggedUserDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.model.user.User;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public interface UserDAO {

    void createUser(UserDTO user) throws DAOException;

    UserDTO readUser(String username) throws DAOException;

    UserDTO readLoggedUserProfile(String username) throws DAOException;

    void updateUser(String userId, Map<String, Object> updateParams) throws DAOException;
    void deleteUser(String username) throws DAOException;

    Map<String, String> authenticate(String field, String password) throws DAOException;

    public void followUser(String username, String followedUsername) throws DAOException;
    public void unfollowUser(String username, String followedUsername) throws DAOException;

    public Integer getNumOfFollowers(String username) throws DAOException;
    public Integer getNumOfFollowed(String username) throws DAOException;

    public void updateFollowers(String username, Integer followers) throws DAOException;
    public void updateFollowed(String username, Integer followed) throws DAOException;

    public List<UserSummaryDTO> searchFirstNUsers(String query, Integer n, String loggedUser) throws DAOException;
    public List<UserSummaryDTO> suggestedUsers(String username, int limit) throws DAOException;
    public List<UserSummaryDTO> showListOfFollowedUsers( String loggedUser, String username) throws DAOException;

    public List<UserSummaryDTO> showListOfFollowers( String loggedUser, String username) throws DAOException;

    public void addRecipeToUser(RecipeSummaryDTO recipe) throws DAOException;
    public void removeRecipeFromUser(String username, String recipeId) throws DAOException;
    public void updateRecipeInUser(RecipeSummaryDTO recipe) throws DAOException;


    Boolean isFollowed(String loggedUser, String username) throws DAOException;
}
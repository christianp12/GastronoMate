package it.unipi.lsmd.gastronomate.service.implementation;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.UserDAO;
import it.unipi.lsmd.gastronomate.dto.LoggedUserDTO;
import it.unipi.lsmd.gastronomate.dto.UserDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.model.enums.AccountSatusTypeEnum;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.enums.ExecutorTaskServiceTypeEnum;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.implementation.asinc_recipe_tasks.UpdateRecipeAuthorRedundancy;
import it.unipi.lsmd.gastronomate.service.implementation.asinc_review_tasks.UpdateReviewRedundancyTask;
import it.unipi.lsmd.gastronomate.service.implementation.asinc_user_tasks.*;
import it.unipi.lsmd.gastronomate.service.interfaces.ExecutorTaskService;
import it.unipi.lsmd.gastronomate.service.interfaces.UserService;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class UserServiceImpl implements UserService {
    private final UserDAO userDAOMongoDB;
    private final UserDAO userDAONeo4j;
    private final Logger applicationLogger;
    private final ExecutorTaskService aperiodicExecutorTaskService;


    public UserServiceImpl() {
        this.userDAOMongoDB = DAOLocator.getUserDAO(DAOTypeEnum.MONGODB);
        this.userDAONeo4j = DAOLocator.getUserDAO(DAOTypeEnum.NEO4J);

        this.applicationLogger = ServiceLocator.getApplicationLogger();
        this.aperiodicExecutorTaskService = ServiceLocator.getExecutorTaskService(ExecutorTaskServiceTypeEnum.APERIODIC);

    }

    @Override
    public void createUser(UserDTO user) throws BusinessException {

        try {
            //insert user in MongoDB
            userDAOMongoDB.createUser(user);

            //create a task which adds a new node User in Neo4j
            CreateUserTask task = new CreateUserTask(user);
            aperiodicExecutorTaskService.executeTask(task);

        } catch (DAOException e) {

            if(e.getErrorType() == ErrorTypeEnum.DUPLICATED_ELEMENT)
                throw new BusinessException("Username already exists", BusinessTypeErrorsEnum.DUPLICATED_ELEMENT);

            applicationLogger.severe("UserServiceImpl: Error while adding user: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while adding user", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }


    @Override
    public UserDTO readUser(String username, Boolean loggedUser) throws BusinessException {
        try {
            if(loggedUser)
                return userDAOMongoDB.readLoggedUserProfile(username);
            else
                return userDAOMongoDB.readUser(username);

        } catch (DAOException e) {
            applicationLogger.severe("UserServiceImpl:readUser: Error while reading user: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while reading user", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public Boolean isFollowed(String loggedUser, String username) throws BusinessException{
        try {
            return userDAONeo4j.isFollowed(loggedUser, username);

        } catch (DAOException e) {
            applicationLogger.severe("UserServiceImpl:isFollowed: Error while checking if user is followed: " + e.getMessage() + " - " + e.getErrorType());
           throw new BusinessException("Error while checking if user is followed", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public void updateUser(String id, Map<String, Object> updateParams, String oldUsername) throws BusinessException {
        //try to update a specific user in MongoDB
        try {
            userDAOMongoDB.updateUser(id, updateParams);

            UpdateUserTask task = new UpdateUserTask(updateParams, id);
            aperiodicExecutorTaskService.executeTask(task);

            if (updateParams.containsKey("username") || updateParams.containsKey("ProfilePictureUrl")) {

                UserSummaryDTO userSummaryDTO = new UserSummaryDTO();

                if (updateParams.containsKey("username")) {
                    userSummaryDTO.setUsername((String) updateParams.get("username"));
                }
                if (updateParams.containsKey("ProfilePictureUrl")) {
                    userSummaryDTO.setProfilePictureUrl((String) updateParams.get("ProfilePictureUrl"));
                }

                UpdateReviewRedundancyTask task2 = new UpdateReviewRedundancyTask(null, userSummaryDTO, oldUsername);

                UpdateRecipeAuthorRedundancy task3 = new UpdateRecipeAuthorRedundancy(userSummaryDTO, oldUsername);

                aperiodicExecutorTaskService.executeTask(task2);
                aperiodicExecutorTaskService.executeTask(task3);
            }

        } catch (DAOException e) {

            if(e.getErrorType() == ErrorTypeEnum.DUPLICATED_ELEMENT)
                throw new BusinessException("Username already exists", BusinessTypeErrorsEnum.DUPLICATED_ELEMENT);

            applicationLogger.severe("UserServiceImpl:updateUser: Error while updating user: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while updating user", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public void deleteUser(String username) throws BusinessException {
        try {
            userDAOMongoDB.deleteUser(username);

            DeleteUserTask task = new DeleteUserTask(username);
            aperiodicExecutorTaskService.executeTask(task);

        } catch (DAOException e) {
            applicationLogger.severe("UserServiceImpl:deleteUser: Error while deleting user: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while deleting user", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public Map<String, String> authenticate(String field, String password) throws BusinessException {
        try {
            return userDAOMongoDB.authenticate(field, password);

        } catch (DAOException e) {

            if(e.getErrorType() == ErrorTypeEnum.AUTHENTICATION_ERROR){

                throw new BusinessException("Bad credentials", BusinessTypeErrorsEnum.AUTHENTICATION_ERROR);

            } else if (e.getErrorType() == ErrorTypeEnum.ACCOUNT_STATUS_ERROR) {

                throw new BusinessException("Account is banned or suspended", BusinessTypeErrorsEnum.AUTHENTICATION_ERROR);

            } else {
                applicationLogger.severe("UserServiceImpl:authenticate: Error while authenticating user: " + e.getMessage() + " - " + e.getErrorType());
                throw new BusinessException("Error while authenticating user", BusinessTypeErrorsEnum.DATABASE_ERROR);
            }
        }
    }

    @Override
    public void followUser(String username, String followed) throws BusinessException {
        try {
            userDAONeo4j.followUser(username, followed);

            UpdateNumberOfFollowedTask task = new UpdateNumberOfFollowedTask(username);
            aperiodicExecutorTaskService.executeTask(task);

            UpdateNumberOfFollowersTask task1 = new UpdateNumberOfFollowersTask(followed);
            aperiodicExecutorTaskService.executeTask(task1);

        } catch (DAOException e) {
            applicationLogger.severe("UserServiceImpl:followUser: Error while following: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while following user", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public void unfollowUser(String username, String followed) throws BusinessException {
        try {
            userDAONeo4j.unfollowUser(username, followed);

            UpdateNumberOfFollowedTask task = new UpdateNumberOfFollowedTask(username);
            aperiodicExecutorTaskService.executeTask(task);

            UpdateNumberOfFollowersTask task1 = new UpdateNumberOfFollowersTask(followed);
            aperiodicExecutorTaskService.executeTask(task1);

        } catch (DAOException e) {
            applicationLogger.severe("UserServiceImpl:unfollowUser: Error while unfollowing user: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while unfollowing user", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public List<UserSummaryDTO> findAccounts(String query, Integer n, String loggedUser) throws BusinessException {
        try {
            return userDAOMongoDB.searchFirstNUsers(query, n, loggedUser);

        } catch (DAOException e) {
            applicationLogger.severe("UserServiceImpl:findAccounts: Error while finding accounts: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while finding accounts", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }

    }

    @Override
    public List<UserSummaryDTO> getSuggestedUsers(String username, int limit) throws BusinessException {
        try {
            return userDAONeo4j.suggestedUsers(username, limit);

        } catch (DAOException e) {
            applicationLogger.severe("UserServiceImpl:getSuggestedUsers: Error while getting suggested users: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while getting suggested users", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public List<UserSummaryDTO> getFollowedUsers(String loggedUser, String username) throws BusinessException {
        try {
            return userDAONeo4j.showListOfFollowedUsers(loggedUser, username);

        } catch (DAOException e) {
            applicationLogger.severe("UserServiceImpl:getFollowedUsers: Error while getting followed users: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while getting followed users", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    @Override
    public List<UserSummaryDTO> getFollowers(String loggedUser, String username) throws BusinessException {
        try {
            return userDAONeo4j.showListOfFollowers(loggedUser, username);

        } catch (DAOException e) {
            applicationLogger.severe("UserServiceImpl:getFollowers: Error while getting followers: " + e.getMessage() + " - " + e.getErrorType());
            throw new BusinessException("Error while getting followers", BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }

    public void changeUserAccountStatus (String username, AccountSatusTypeEnum status) throws BusinessException{

    }

}
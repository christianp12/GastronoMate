package it.unipi.lsmd.gastronomate.service.interfaces;

import it.unipi.lsmd.gastronomate.dto.LoggedUserDTO;
import it.unipi.lsmd.gastronomate.dto.UserDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.model.enums.AccountSatusTypeEnum;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public interface UserService {
    void createUser(UserDTO user) throws BusinessException;

    void updateUser(String id, Map<String, Object> updateParams, String oldUsername) throws BusinessException;

    UserDTO readUser(String username, Boolean loggedUser) throws BusinessException;

    void deleteUser(String username) throws BusinessException;
    Map<String, String> authenticate(String username, String password) throws BusinessException;

    void followUser(String username, String followed) throws BusinessException;
    void unfollowUser(String username, String followed) throws BusinessException;

     List<UserSummaryDTO> findAccounts(String query, Integer n, String loggedUser) throws BusinessException;
     List<UserSummaryDTO> getSuggestedUsers(String username, int limit) throws BusinessException;

     List<UserSummaryDTO> getFollowedUsers( String loggedUser, String username) throws BusinessException;
     List<UserSummaryDTO> getFollowers( String loggedUser, String username) throws BusinessException;

     void changeUserAccountStatus (String username, AccountSatusTypeEnum status) throws BusinessException;

    Boolean isFollowed(String username, String loggedUser) throws BusinessException;
}
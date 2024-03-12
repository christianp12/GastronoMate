package it.unipi.lsmd.gastronomate.dao.neo4j;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

class UserDAONeo4jImplTest {

    @BeforeEach
    void setUp() throws Exception {
        Neo4jBaseDAO.openConnection();
    }

    @Test
    void createUser() {
        UserDAONeo4jImpl userDAONeo4j = new UserDAONeo4jImpl();
        UserDTO user = new UserDTO();
        user.setFullName("Mario Rossi");
        user.setProfilePictureUrl("profilePic");
        user.setUsername("mrossi");

        try {
            userDAONeo4j.createUser(user);
        } catch (DAOException e) {
            System.err.println(e.getMessage() + " " + e.getErrorType());
        }
    }

    @Test
    void readUser() {
        UserDAONeo4jImpl userDAONeo4j = new UserDAONeo4jImpl();
        try {
            userDAONeo4j.readUser("1234");
        } catch (DAOException e) {
            System.out.println(e.getMessage() + " " + e.getErrorType());
        }
    }

    @Test
    void updateUser() {
        UserDAONeo4jImpl userDAONeo4j = new UserDAONeo4jImpl();
        Map<String, Object> updatedParams = Map.of("username", "rossim", "profilePictureUrl", "newProfilePic");
        try {
            userDAONeo4j.updateUser("1234", updatedParams);
        } catch (DAOException e) {
            System.out.println(e.getMessage() + " " + e.getErrorType());
        }
    }

    @Test
    void deleteUser() {
        UserDAONeo4jImpl userDAONeo4j = new UserDAONeo4jImpl();
        try {
            userDAONeo4j.deleteUser("p");
        } catch (DAOException e) {
            System.out.println(e.getMessage() + " " + e.getErrorType());
        }
    }
}
package it.unipi.lsmd.gastronomate.service.implementation;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.mongoDB.MongoDbBaseDAO;
import it.unipi.lsmd.gastronomate.dao.neo4j.Neo4jBaseDAO;
import it.unipi.lsmd.gastronomate.dto.UserDTO;
import it.unipi.lsmd.gastronomate.model.Address;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.enums.ExecutorTaskServiceTypeEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.ExecutorTaskService;
import it.unipi.lsmd.gastronomate.service.interfaces.TaskManager;
import org.bson.codecs.jsr310.LocalDateTimeCodec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    private UserServiceImpl userService;
    private static final ExecutorTaskService aperiodicTaskService = ServiceLocator.getExecutorTaskService(ExecutorTaskServiceTypeEnum.APERIODIC);
    private static final TaskManager errorTaskManager = ServiceLocator.getErrorsTaskManager();
    @BeforeEach
    void setUp() {
        try {
            MongoDbBaseDAO.openConnection();
            Neo4jBaseDAO.openConnection();
            aperiodicTaskService.start();
            errorTaskManager.start();
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        this.userService = new UserServiceImpl();

    }

    @Test
    void createUser() {
        UserDTO user = new UserDTO();
        user.setFullName("Mario Rossi");
        user.setUsername("Pippo4");
        user.setEmail("ma4@gmail.com");
        user.setProfilePictureUrl("profilepic");

        user.setAddress(new Address("Pisa", "Pisa", "Italy"));
        user.setDateOfBirth(new Date("12/12/2001").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        try {
            userService.createUser(user);

            Thread.sleep(60*60*1000);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    @Test
    void readUser() {
        try {
            UserDTO user = userService.readUser("Pippo3", null);
            System.out.println(user);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    void deleteUser() {
        try {
            userService.deleteUser("Pippo4");

            Thread.sleep(60*60*1000);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
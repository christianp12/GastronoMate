package it.unipi.lsmd.gastronomate.dao.mongoDB;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MongoDBDaseDAOTest {
    @BeforeEach
     void setUp() throws Exception {
       MongoDbBaseDAO.openConnection();
    }

    // Test for the openConnection() method
    @Test
    void WHEN_openConnection_THEN_noException() {
        assertDoesNotThrow(MongoDbBaseDAO::openConnection);
    }

    // Test for the closeConnection() method
    @Test
    void WHEN_closeConnection_THEN_noException() throws Exception {
        MongoDbBaseDAO.openConnection();
        assertDoesNotThrow(MongoDbBaseDAO::closeConnection);
    }

    @Test
    void WHEN_getMongoClient_THEN_noException() throws Exception {
        assertNotNull(MongoDbBaseDAO.getMongoClient());
    }
}
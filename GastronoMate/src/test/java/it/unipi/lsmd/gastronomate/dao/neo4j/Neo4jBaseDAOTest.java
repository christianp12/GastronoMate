package it.unipi.lsmd.gastronomate.dao.neo4j;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Neo4jBaseDAOTest {

    @Test
    void WHEN_openConnection_THEN_noException() {
        assertDoesNotThrow(Neo4jBaseDAO::openConnection);
    }

    @Test
    void WHEN_closeConnection_THEN_noException() {
        try {
            Neo4jBaseDAO.openConnection();
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(Neo4jBaseDAO::closeConnection);

    }
    @Test
    void WHEN_getSession_THEN_noException() {
        try {
            Neo4jBaseDAO.openConnection();
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(Neo4jBaseDAO::getSession);
    }

}
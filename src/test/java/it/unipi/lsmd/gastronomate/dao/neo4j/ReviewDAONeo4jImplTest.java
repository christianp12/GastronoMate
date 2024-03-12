package it.unipi.lsmd.gastronomate.dao.neo4j;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewDAONeo4jImplTest {

    @BeforeEach
     void setUp() throws Exception {
        Neo4jBaseDAO.openConnection();
    }

}
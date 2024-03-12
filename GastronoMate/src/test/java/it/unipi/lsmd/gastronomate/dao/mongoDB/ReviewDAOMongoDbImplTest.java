package it.unipi.lsmd.gastronomate.dao.mongoDB;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewDAOMongoDbImplTest {

    @BeforeEach
    void setUp() {
        try {
            MongoDbBaseDAO.openConnection();

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateAverageRating() {
        try {
            ReviewDAOMongoDbImpl reviewDAOMongoDb = new ReviewDAOMongoDbImpl();
            reviewDAOMongoDb.updateAverageRating();

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }
}
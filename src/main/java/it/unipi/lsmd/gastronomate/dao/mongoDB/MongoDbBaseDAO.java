package it.unipi.lsmd.gastronomate.dao.mongoDB;

import com.mongodb.*;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;

import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import lombok.Getter;
import lombok.NonNull;

import java.util.logging.Logger;

public class MongoDbBaseDAO {

    private static final String PROTOCOL = "mongodb://";
    private static final String MONGO_HOST1 = "10.1.1.17";
    private static final String MONGO_HOST2 = "10.1.1.18";
    private static final String MONGO_HOST3 = "10.1.1.19";
    private static final String MONGO_PORT = "27017";
    private static final String MONGO_DB = "GastronoMate";

    @Getter
    private static MongoClient mongoClient = null;
    private static MongoClientSettings settings;
    @Getter
    private static Logger logger;

    static {
        ConnectionString connectionString = new ConnectionString(String.format("%s%s:%s,%s:%s,%s:%s/%s", PROTOCOL, MONGO_HOST1, MONGO_PORT, MONGO_HOST2, MONGO_PORT, MONGO_HOST3, MONGO_PORT, MONGO_DB));
        settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .writeConcern(WriteConcern.W1)
                .readPreference(ReadPreference.nearest())
                .retryWrites(true)
                .readConcern(ReadConcern.LOCAL)
                .build();

        logger = ServiceLocator.getApplicationLogger();
    }

    /**
     * Opens a connection to the database
     *
     * @throws DAOException CONNECTION_ERROR if an error occurs while opening the connection
     */
    public static void openConnection() throws DAOException {

        if(mongoClient == null){
            try {
                mongoClient = MongoClients.create(settings);

            } catch (Exception e) {
                logger.severe("MongoDbBaseDAO: Error while connecting to MongoDB (openConnection): " + e.getMessage());
                throw new DAOException(e.getMessage(), ErrorTypeEnum.CONNECTION_ERROR);
            }
        }
    }

    /**
     * Closes the connection to the database
     *
     * @throws DAOException CONNECTION_ERROR if an error occurs while closing the connection or if the connection was not previously opened
     */
    public static void closeConnection() throws DAOException {
        if(mongoClient != null){
            try {
                mongoClient.close();
            } catch (Exception e) {
                logger.severe(" MongoDbBaseDAO: Error while closing MongoDB connection (closeConnection): " + e.getMessage());
                throw new DAOException(e.getMessage(), ErrorTypeEnum.CONNECTION_ERROR);
            }
        }
        else {
            logger.severe("MongoDbBaseDAO: Error while closing MongoDB connection: connection was not previously opened (closeConnection)");
            throw new DAOException("Error while closing MongoDB connection: connection was not previously opened", ErrorTypeEnum.DATABASE_ERROR);
        }
    }

}

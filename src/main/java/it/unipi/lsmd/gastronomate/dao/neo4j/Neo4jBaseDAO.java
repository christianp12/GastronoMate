package it.unipi.lsmd.gastronomate.dao.neo4j;

import com.mongodb.client.MongoClients;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;

import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import lombok.Getter;
import org.neo4j.driver.*;

import java.util.logging.Logger;


public class Neo4jBaseDAO {
    private static final String PROTOCOL = "bolt://";
    private static final String NEO4J_HOST = "10.1.1.17";
    private static final String NEO4J_PORT = "7687";
    private static final String NEO4J_USER = "neo4j";
    private static final String NEO4J_PSW = "password2024";
    private static Driver driver = null;

    @Getter
    private static Logger logger;

    static {
        logger = ServiceLocator.getApplicationLogger();
    }

    /**
     * Opens a connection to the graph database
     *
     * @throws DAOException CONNECTION_ERROR if an error occurs while opening the connection
     */
    public static void openConnection() throws DAOException {
        if(driver == null){
            try {
                String uri = String.format("%s%s:%s", PROTOCOL, NEO4J_HOST, NEO4J_PORT);

                driver = GraphDatabase.driver(uri, AuthTokens.basic(NEO4J_USER, NEO4J_PSW));

            } catch (Exception e) {
                logger.severe("Neo4jBaseDAO: Error while connecting to Neo4j (openConnection): " + e.getMessage());
                DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.CONNECTION_ERROR);
                throw ex;
            }
        }
    }

    /**
     * Closes the connection to the graph database
     *
     * @throws DAOException CONNECTION_ERROR if an error occurs while closing the connection
     */
    public static void closeConnection() throws DAOException {
        if(driver != null){
            try {
                driver.close();
                driver = null;
            } catch (Exception e) {
                logger.severe("Neo4jBaseDAO: Error while closing connection to Neo4j (closeConnection): " + e.getMessage());
                DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.CONNECTION_ERROR);
                throw ex;
            }
        }
        else {
            logger.severe("Neo4jBaseDAO: Connection to Neo4j not opened (closeConnection)");
            DAOException ex = new DAOException("Connection to Neo4j not opened",ErrorTypeEnum.DATABASE_ERROR);
            throw ex;
        }
    }

    /**
     * Returns a session to the graph database
     *
     * @throws DAOException CONNECTION_ERROR if an error occurs if the connection was not previously opened
     */
    public static Session getSession() throws DAOException {
        if(driver == null){
            logger.severe("Neo4jBaseDAO: Connection to Neo4j not opened (getSession)");
            DAOException ex = new DAOException("Connection to Neo4j not opened", ErrorTypeEnum.CONNECTION_ERROR);
            throw ex;
        }
        return driver.session(SessionConfig.builder().withDatabase("neo4j").build());
    }
}

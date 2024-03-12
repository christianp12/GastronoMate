package it.unipi.lsmd.gastronomate.dao;

import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.RecipeDAO;
import it.unipi.lsmd.gastronomate.dao.interfaces.ReviewDAO;
import it.unipi.lsmd.gastronomate.dao.interfaces.StatisticsDAO;
import it.unipi.lsmd.gastronomate.dao.interfaces.UserDAO;
import it.unipi.lsmd.gastronomate.dao.mongoDB.RecipeDAOMongoDbImpl;
import it.unipi.lsmd.gastronomate.dao.mongoDB.ReviewDAOMongoDbImpl;
import it.unipi.lsmd.gastronomate.dao.mongoDB.StatisticsDAOMongoDbImpl;
import it.unipi.lsmd.gastronomate.dao.mongoDB.UserDAOMongoDbImpl;
import it.unipi.lsmd.gastronomate.dao.neo4j.RecipeDAONeo4jImpl;
import it.unipi.lsmd.gastronomate.dao.neo4j.ReviewDAONeo4jImpl;
import it.unipi.lsmd.gastronomate.dao.neo4j.StatisticsDAONeo4jImpl;
import it.unipi.lsmd.gastronomate.dao.neo4j.UserDAONeo4jImpl;

public class DAOLocator {
    public static UserDAO getUserDAO(DAOTypeEnum type) {
        return switch (type) {
            case MONGODB -> new UserDAOMongoDbImpl();
            case NEO4J -> new UserDAONeo4jImpl();
        };
    }

    public static RecipeDAO getRecipeDAO(DAOTypeEnum type) {
        return switch (type) {
            case MONGODB -> new RecipeDAOMongoDbImpl();
            case NEO4J -> new RecipeDAONeo4jImpl();
        };
    }

    public static ReviewDAO getReviewDAO(DAOTypeEnum type) {
        return switch (type) {
            case MONGODB -> new ReviewDAOMongoDbImpl();
            case NEO4J -> new ReviewDAONeo4jImpl();
        };
    }
    public static StatisticsDAO getStatisticsDAO(DAOTypeEnum type) {
        return switch (type) {
            case MONGODB -> new StatisticsDAOMongoDbImpl();
            case NEO4J -> new StatisticsDAONeo4jImpl();
        };
    }
}
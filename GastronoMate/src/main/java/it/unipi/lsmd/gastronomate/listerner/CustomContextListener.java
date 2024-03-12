package it.unipi.lsmd.gastronomate.listerner;

import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.mongoDB.MongoDbBaseDAO;
import it.unipi.lsmd.gastronomate.dao.neo4j.Neo4jBaseDAO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.interfaces.TaskManager;
import it.unipi.lsmd.gastronomate.service.enums.ExecutorTaskServiceTypeEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.ExecutorTaskService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class CustomContextListener implements ServletContextListener {

    private static final Logger logger = ServiceLocator.getApplicationLogger();
    private static final ExecutorTaskService aperiodicTaskService = ServiceLocator.getExecutorTaskService(ExecutorTaskServiceTypeEnum.APERIODIC);
    private static final ExecutorTaskService periodicTaskService = ServiceLocator.getExecutorTaskService(ExecutorTaskServiceTypeEnum.PERIODIC);
    private static final TaskManager errorTaskManager = ServiceLocator.getErrorsTaskManager();
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("initiating startup");
        try{
            MongoDbBaseDAO.openConnection();
        } catch (DAOException e) {
            logger.severe("CustomContextListener:contextInitialized: Error while initializing MongoDB connection: " + e.getMessage() + " - " + e.getErrorType());
            throw new RuntimeException(e);
        }
        System.out.println("MongoDB connection initialized");
        try {
            Neo4jBaseDAO.openConnection();
        }catch (DAOException e){
            logger.severe("CustomContextListener:contextInitialized: Error while initializing Neo4j connection: " + e.getMessage() + " - " + e.getErrorType());
           throw new RuntimeException(e);
        }
        try {
            aperiodicTaskService.start();
        }catch (Exception e){
            logger.severe("CustomContextListener:contextInitialized: Error while starting AperiodicExecutorTaskService: " + e.getMessage());
            throw new RuntimeException(e);
        }
        try {
           errorTaskManager.start();
        }catch (Exception e){
            logger.severe("CustomContextListener:contextInitialized: Error while starting GraphErrorsTaskManager: " + e.getMessage());
            throw new RuntimeException(e);
        }
        try {
            periodicTaskService.start();
        }catch (Exception e){
            logger.severe("CustomContextListener:contextInitialized: Error while starting PeriodicExecutorTaskService: " + e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println("Neo4j connection initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("shutting down");
        try {
            MongoDbBaseDAO.closeConnection();
        } catch (DAOException e) {
            logger.severe("CustomContextListener:contextDestroyed: Error while closing MongoDB connection: " + e.getMessage() + " - " + e.getErrorType());
            throw new RuntimeException(e);
        }
       System.out.println("MongoDB connection closed");
        try {
            Neo4jBaseDAO.closeConnection();
        } catch (DAOException e) {
            logger.severe("CustomContextListener:contextDestroyed: Error while closing Neo4j connection: " + e.getMessage() + " - " + e.getErrorType());
            System.exit(1);
        }
        System.out.println("Neo4j connection closed");
        try{
            aperiodicTaskService.stop();
        }catch (Exception e){
            logger.severe("CustomContextListener:contextDestroyed: Error while stopping AperiodicExecutorTaskService: " + e.getMessage());
            throw new RuntimeException(e);
        }
        try{
            errorTaskManager.stop();
        }catch (Exception e){
            logger.severe("CustomContextListener:contextDestroyed: Error while stopping GraphErrorsTaskManager: " + e.getMessage());
            throw new RuntimeException(e);
        }
        try{
            periodicTaskService.stop();
        }catch (Exception e){
            logger.severe("CustomContextListener:contextDestroyed: Error while stopping PeriodicExecutorTaskService: " + e.getMessage());
            throw new RuntimeException(e);
        }

        System.out.println("shutdown complete");
    }
}

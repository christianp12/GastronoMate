package it.unipi.lsmd.gastronomate.service.implementation.asinc_user_tasks;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.interfaces.UserDAO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.util.logging.Logger;

public class UpdateNumberOfFollowedTask extends Task {
    private UserDAO mongoDbUserDAO;
    private UserDAO neo4jUserDAO;
    private Logger applicationLogger;
    private String username;

    public UpdateNumberOfFollowedTask(String username) {
        super(5);
        this.username = username;
        this.mongoDbUserDAO = DAOLocator.getUserDAO(DAOTypeEnum.MONGODB);
        this.neo4jUserDAO = DAOLocator.getUserDAO(DAOTypeEnum.NEO4J);
        this.applicationLogger = ServiceLocator.getApplicationLogger();
    }
    @Override
    public void executeJob() throws BusinessException {
        try{
            Integer followed = neo4jUserDAO.getNumOfFollowed(username);

            mongoDbUserDAO.updateFollowed(username, followed);

        }catch (DAOException e){
            applicationLogger.severe("Error while updating number of followed: " + e.getMessage());
            throw new BusinessException("Error while updating number of followed: " + e.getMessage(), BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }
}

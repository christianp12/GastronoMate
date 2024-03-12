package it.unipi.lsmd.gastronomate.service.implementation.asinc_user_tasks;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.UserDAO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.util.logging.Logger;

public class DeleteUserTask extends Task {
    private final UserDAO neo4jUserDAO;
    private final Logger applicationLogger;
    private final String username;

    public DeleteUserTask(String username) {
        super(7);
        this.neo4jUserDAO = DAOLocator.getUserDAO(DAOTypeEnum.NEO4J);
        this.applicationLogger = ServiceLocator.getApplicationLogger();
        this.username = username;
    }

    @Override
    public void executeJob() throws BusinessException {
        try {
            neo4jUserDAO.deleteUser(username);

        } catch (DAOException e) {
           if (e.getErrorType().equals(ErrorTypeEnum.TRANSIENT_ERROR)) {
                throw new BusinessException("Error while deleting user: " + e.getMessage(), BusinessTypeErrorsEnum.RETRYABLE_ERROR);
           } else {
                applicationLogger.severe("Error while deleting user: " + e.getMessage());
                throw new BusinessException("Error while deleting user: " + e.getMessage(), BusinessTypeErrorsEnum.GENERIC_ERROR);
           }
        }
    }
}
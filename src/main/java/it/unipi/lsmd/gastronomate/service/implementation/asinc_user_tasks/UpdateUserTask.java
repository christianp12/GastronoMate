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

import java.util.Map;
import java.util.logging.Logger;

public class UpdateUserTask extends Task {
    private final UserDAO userDAONeo4j;
    private final Logger applicationLogger;
    private final Map<String, Object> updateParams;
    private final String userId;

    public UpdateUserTask(Map<String, Object> updateParams, String userId) {
        super(8);
        this.userDAONeo4j = DAOLocator.getUserDAO(DAOTypeEnum.NEO4J);
        this.applicationLogger = ServiceLocator.getApplicationLogger();
        this.updateParams = updateParams;
        this.userId = userId;
    }

    @Override
    public void executeJob()  throws BusinessException {
        try {
            userDAONeo4j.updateUser(userId, updateParams);

        } catch (DAOException e) {
            if (e.getErrorType().equals(ErrorTypeEnum.TRANSIENT_ERROR)) {
                throw new BusinessException("Error while updating user: " + e.getMessage(), BusinessTypeErrorsEnum.RETRYABLE_ERROR);
            } else {
                applicationLogger.severe("Error while updating user: " + e.getMessage());
                throw new BusinessException("Error while updating user: " + e.getMessage(), BusinessTypeErrorsEnum.DATABASE_ERROR);
            }
        }
    }
}
package it.unipi.lsmd.gastronomate.service.implementation.asinc_user_tasks;


import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.UserDAO;
import it.unipi.lsmd.gastronomate.dto.UserDTO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.util.logging.Logger;

public class CreateUserTask extends Task {
    private final UserDAO userDAONeo4j;
    private final Logger applicationLogger;
    private final UserDTO user;

    public CreateUserTask(UserDTO user){
        super(9);
        this.userDAONeo4j = DAOLocator.getUserDAO(DAOTypeEnum.NEO4J);
        this.applicationLogger = ServiceLocator.getApplicationLogger();
        this.user = user;
    }

    @Override
    public void executeJob() throws BusinessException {
        try {
            userDAONeo4j.createUser(user);

        } catch (DAOException e) {
            if (e.getErrorType().equals(ErrorTypeEnum.TRANSIENT_ERROR)) {
                throw new BusinessException("Error while creating user: " + e.getMessage(), BusinessTypeErrorsEnum.RETRYABLE_ERROR);
            } else {
                applicationLogger.severe("Error while creating user: " + e.getMessage());
                throw new BusinessException("Error while creating user: " + e.getMessage(), BusinessTypeErrorsEnum.DATABASE_ERROR);
            }

        }
    }
}

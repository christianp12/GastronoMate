package it.unipi.lsmd.gastronomate.service.implementation.asinc_recipe_tasks.periodic;

import it.unipi.lsmd.gastronomate.dao.DAOLocator;
import it.unipi.lsmd.gastronomate.dao.enums.DAOTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.RecipeDAO;
import it.unipi.lsmd.gastronomate.dao.interfaces.ReviewDAO;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.util.logging.Logger;

public class UpdateAverageRatingTask extends Task {
    private ReviewDAO reviewDAOMongo;
    private Logger applicationLogger;

    public UpdateAverageRatingTask() {
        super(5);
        this.reviewDAOMongo = DAOLocator.getReviewDAO(DAOTypeEnum.MONGODB);

        this.applicationLogger = ServiceLocator.getApplicationLogger();
    }

    @Override
    public void executeJob() throws BusinessException {
        try {
            reviewDAOMongo.updateAverageRating();

        } catch (Exception e) {
            applicationLogger.severe("Error while updating average rating: " + e.getMessage());
            throw new BusinessException("Error while  updating average rating: " + e.getMessage(), BusinessTypeErrorsEnum.DATABASE_ERROR);
        }
    }
}

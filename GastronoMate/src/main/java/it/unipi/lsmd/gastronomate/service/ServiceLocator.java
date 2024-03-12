package it.unipi.lsmd.gastronomate.service;

import it.unipi.lsmd.gastronomate.service.enums.ExecutorTaskServiceTypeEnum;

import it.unipi.lsmd.gastronomate.service.implementation.*;
import it.unipi.lsmd.gastronomate.service.implementation.loggers.ApplicationLogService;
import it.unipi.lsmd.gastronomate.service.implementation.loggers.TaskLogService;
import it.unipi.lsmd.gastronomate.service.interfaces.*;

import java.util.logging.Logger;

public class ServiceLocator {

   public static RecipeService getRecipeService() {
        return new RecipeServiceImpl();
    }
    public static ReviewService getReviewService() {
        return new ReviewServiceImpl();
    }
    public static UserService getUserService() {
        return new UserServiceImpl();
    }
    public static CookieService getCookieService() {
        return new CookieServiceImpl();
    }

    public static StatisticsService getStatisticsService() {
        return new StatisticsServiceImpl();
    }
    public static Logger getApplicationLogger() {
        return ApplicationLogService.getApplicationLogger();
    }

    public static TaskLogService getTaskLogger() {
        return TaskLogService.getInstance();
    }

    public static TaskManager getErrorsTaskManager() {
        return ErrorTaskManager.getInstance();
    }


    public static ExecutorTaskService getExecutorTaskService(ExecutorTaskServiceTypeEnum type) {
        switch (type) {
            case APERIODIC:
                return AperiodicExecutorTaskServiceImpl.getInstance(getErrorsTaskManager());
            case PERIODIC:
                return PeriodicExecutorTaskServiceImpl.getInstance();
            default:
                return null;
        }

    }

}

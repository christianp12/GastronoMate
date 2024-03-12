package it.unipi.lsmd.gastronomate.service.implementation;

import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.implementation.asinc_recipe_tasks.periodic.PendingRecipesEliminationTask;
import it.unipi.lsmd.gastronomate.service.implementation.asinc_recipe_tasks.periodic.UpdateAverageRatingTask;
import it.unipi.lsmd.gastronomate.service.interfaces.ExecutorTaskService;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.*;

public class PeriodicExecutorTaskServiceImpl implements ExecutorTaskService {
    private static volatile ExecutorTaskService instance = null;
    private ScheduledExecutorService scheduledExecutorService;

    private PeriodicExecutorTaskServiceImpl() {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
    }

    public static ExecutorTaskService getInstance() {
        if (instance == null) {
            synchronized (PeriodicExecutorTaskServiceImpl.class) {
                if (instance == null) {
                    instance = new PeriodicExecutorTaskServiceImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public void executeTask(Task task) {}

    @Override
    public void start() {

        scheduledExecutorService.scheduleAtFixedRate(() -> {

            try {
                new PendingRecipesEliminationTask().executeJob();

            } catch (BusinessException e) {
             throw new RuntimeException(e);
            }

        }, 0, 1, DAYS);

        scheduledExecutorService.scheduleAtFixedRate(() -> {

            try {
                new UpdateAverageRatingTask().executeJob();

            } catch (BusinessException e) {
             throw new RuntimeException(e);
            }

        }, 0, 12, HOURS);

    }

    @Override
    public void stop() {
        scheduledExecutorService.shutdown();
        try{
            if (!scheduledExecutorService.awaitTermination(2, MINUTES)) {
                scheduledExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}

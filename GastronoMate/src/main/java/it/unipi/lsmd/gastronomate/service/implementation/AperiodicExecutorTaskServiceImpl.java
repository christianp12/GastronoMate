package it.unipi.lsmd.gastronomate.service.implementation;

import it.unipi.lsmd.gastronomate.service.interfaces.TaskManager;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.interfaces.ExecutorTaskService;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MINUTES;

public class AperiodicExecutorTaskServiceImpl implements ExecutorTaskService {

    private static volatile ExecutorTaskService instance = null;
    private ExecutorService executorService;
    private TaskManager taskManager;

    private AperiodicExecutorTaskServiceImpl(TaskManager taskManager) {
        executorService = Executors.newFixedThreadPool(10);
        this.taskManager = taskManager;
    }
    @Override
    public synchronized void start() {}

    @Override
    public synchronized void stop() {
        executorService.shutdown();
        try{
            if (!executorService.awaitTermination(2, MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public synchronized void executeTask(Task task) {
       executorService.execute(() -> {
           try {
               task.executeJob();
           } catch (BusinessException e) {
               if (e.getErrorType().equals(BusinessTypeErrorsEnum.RETRYABLE_ERROR)) {
                   taskManager.addTask(task);
               }
               else {
                  System.err.println("Cannot execute task: " + e.getMessage() + " " + task.getClass());
               }
           } catch (Exception e) {
               throw new RuntimeException(e);
           }
       });
    }

    public static ExecutorTaskService getInstance(TaskManager taskManager) {
        if (instance == null) {
            synchronized (AperiodicExecutorTaskServiceImpl.class) {
                if (instance == null) {
                    instance = new AperiodicExecutorTaskServiceImpl(taskManager);
                }
            }
        }
        return instance;
    }
}

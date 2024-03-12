package it.unipi.lsmd.gastronomate.service.implementation;

import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.interfaces.TaskManager;
import it.unipi.lsmd.gastronomate.service.exceptions.BusinessException;
import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import it.unipi.lsmd.gastronomate.service.implementation.loggers.TaskLogService;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.util.concurrent.*;

public class ErrorTaskManager extends TaskManager {
    private static volatile ErrorTaskManager instance = null;
    private static final TaskLogService taskLogService = ServiceLocator.getTaskLogger();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Boolean isRunning;
    private ErrorTaskManager() {
        super();
        isRunning = true;
    }
    public static ErrorTaskManager getInstance() {
        if (instance == null) {
            synchronized (ErrorTaskManager.class) {
                if (instance == null) {
                    instance = new ErrorTaskManager();
                }
            }
        }
        return instance;
    }

    /*

    Start the task manager: start the executor service and schedule a priodic task to execute the tasks in the queue.
    The periodic task is scheduled to run every 8 seconds, and it will start if and only if the queue is not empty.
    It will execute all the tasks in the queue, and then it will stop.

     */


    @Override
    public void start() {
        executorService.execute(() -> {
            while (isRunning) {
                Task task = null;
                try {
                    task = getTaskQueue().take();
                    task.incrementRetries();
                    if(task.getRetries() > Task.getMAX_RETRIES()) {
                        taskLogService.write(task);
                        continue;
                    }
                    task.executeJob();

                } catch (BusinessException e) {
                    if (e.getErrorType().equals(BusinessTypeErrorsEnum.RETRYABLE_ERROR)) {
                        addTask(task);
                    }
                    else {
                        System.err.println("Cannot execute task: " + e.getMessage() + " " + task.getClass());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void stop() {
        isRunning = false;
        executorService.shutdown();
        try{
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

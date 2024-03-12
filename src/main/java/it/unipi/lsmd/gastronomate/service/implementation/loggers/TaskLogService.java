package it.unipi.lsmd.gastronomate.service.implementation.loggers;

import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.interfaces.Task;

import java.io.*;
import java.util.logging.*;


public class TaskLogService {
    private static volatile TaskLogService graphLogger = null;
    private static final String LOG_FILE = "taskLog.bin";
    private static final String DIR = "logs";
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final Logger applicationLogger = ServiceLocator.getApplicationLogger();
    private File file;

    private TaskLogService(File file) {
        this.file = file;
    }

    public static TaskLogService getInstance() {
        if (graphLogger == null) {
            synchronized (TaskLogService.class) {
                if (graphLogger == null) {
                    File file = new File(PROJECT_PATH + File.separator + DIR + File.separator + LOG_FILE);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            applicationLogger.severe("TaskLogService: Error while creating task log file: " + e.getMessage());
                        }
                    }
                    graphLogger = new TaskLogService(file);
                }
            }
        }
        return graphLogger;
    }


    public synchronized void write(Task task) {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file, true))) {
            out.writeObject(task);
            out.flush();

        } catch (Exception e) {
            applicationLogger.severe("GraphLogService: Error while writing to graph log file: " + e.getMessage());
        }

    }
}

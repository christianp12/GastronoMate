package it.unipi.lsmd.gastronomate.service.implementation.loggers;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.*;

public class ApplicationLogService {
    private static volatile Logger applicationLogger;
    private static final String LOG_FILE = "applicationLog.txt";
    private static final String DIR = "logs";
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final int LOG_SIZE_LIMIT = 1024 * 1024 ; // 1MB per file
    private static final int LOG_FILE_COUNT = 5; // 5 files max
    private static final Object lock = new Object();

    private ApplicationLogService(){}

    /**
     * Returns the application logger
     *
     *
     * @Note the logger is thread safe
     * @Note the logger is configured to write on a file
     * @Note the logger is configured to write on a file with a max size of 1MB
     * @Note the logger is configured to write on a file with a max number of 5 files
     *
     * @Throws RuntimeException if an error occurs while creating the logger
     *
     * @return application logger
     */
    public static Logger getApplicationLogger() {
        if (applicationLogger == null) {
            synchronized (lock) {
                if (applicationLogger == null) {
                    try {
                        applicationLogger = Logger.getLogger("ApplicationLogger");
                        Path logFilePath = Paths.get(PROJECT_PATH, DIR, LOG_FILE);
                        Handler fileHandler = new FileHandler(logFilePath.toString(), LOG_SIZE_LIMIT, LOG_FILE_COUNT, true);
                        SimpleFormatter formatterTxt = new SimpleFormatter();
                        fileHandler.setFormatter(formatterTxt);
                        applicationLogger.addHandler(fileHandler);
                        applicationLogger.setLevel(Level.ALL);
                        applicationLogger.setUseParentHandlers(false);
                        applicationLogger.log(Level.CONFIG, "Logger: {0} created.", applicationLogger.getName());
                    } catch (IOException e) {
                        applicationLogger.log(Level.SEVERE, "Error in Logger creation", e);
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return applicationLogger;
    }
}


package it.unipi.lsmd.gastronomate.log;

import it.unipi.lsmd.gastronomate.service.implementation.loggers.ApplicationLogService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogServiceTest {

    @Test
    void WHEN_getApplicationLogger_THEN_noException() {
        assertDoesNotThrow(ApplicationLogService::getApplicationLogger);
    }
    @Test
    void WHEN_getApplicationLogger_THEN_notNull() {
        assertNotNull(ApplicationLogService.getApplicationLogger());
    }
    @Test
    void WHEN_getApplicationLogger_THEN_sameLogger() {
        assertEquals(ApplicationLogService.getApplicationLogger(), ApplicationLogService.getApplicationLogger());
    }
    @Test
    void WHEN_Two_THREADS_getApplicationLogger_THEN_sameLogger() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            assertEquals(ApplicationLogService.getApplicationLogger(), ApplicationLogService.getApplicationLogger());
        });
        Thread t2 = new Thread(() -> {
            assertEquals(ApplicationLogService.getApplicationLogger(), ApplicationLogService.getApplicationLogger());
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
package it.unipi.lsmd.gastronomate.listerner;

import it.unipi.lsmd.gastronomate.dao.mongoDB.MongoDbBaseDAO;
import lombok.SneakyThrows;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomConnectionPoolListenerTest {
    @SneakyThrows
    @BeforeEach
    void setUp() {
        MongoDbBaseDAO.openConnection();
    }

    // Test for the connectionPoolCreated() method
    @Test
    void WHEN_two_Threads_execute_same_method_THEN_noException() throws Exception {
        Thread t1 = new Thread(() -> {
            try {
                MongoDbBaseDAO.getMongoClient().getDatabase("GastronoMate").getCollection("test").insertOne(new Document("test", "test"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                MongoDbBaseDAO.getMongoClient().getDatabase("GastronoMate").getCollection("test").insertOne(new Document("test1", "test1"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

    }

}
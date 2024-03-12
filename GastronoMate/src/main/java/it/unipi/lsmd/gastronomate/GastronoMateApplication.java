package it.unipi.lsmd.gastronomate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class GastronoMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(GastronoMateApplication.class, args);
    }

}
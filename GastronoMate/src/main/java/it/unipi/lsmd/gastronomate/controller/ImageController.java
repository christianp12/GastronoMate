package it.unipi.lsmd.gastronomate.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {
    private static final String PROJECT_PATH = System.getProperty("user.dir");

    @GetMapping(value = "/uploads/{imageName}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String imageName) throws Exception {

        InputStreamResource resource;
        Path path = null;
        try {

            path = Paths.get(PROJECT_PATH, "src", "main", "resources", "static", "uploads", "recipes", imageName);

            resource = new InputStreamResource(Files.newInputStream(path));

        } catch (Exception e) {

            path = Paths.get(PROJECT_PATH, "src", "main", "resources", "static", "uploads", "users", imageName);

            resource = new InputStreamResource(Files.newInputStream(path));

        }

        if (imageName.endsWith(".png"))
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .body(resource);
        else if (imageName.endsWith(".jpg"))
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpg")
                    .body(resource);
        else if (imageName.endsWith(".jpeg"))
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .body(resource);
        else if (imageName.endsWith(".gif"))
            return ResponseEntity.ok()
                    .header("Content-Type", "image/gif")
                    .body(resource);
        else if (imageName.endsWith(".bmp"))
            return ResponseEntity.ok()
                    .header("Content-Type", "image/bmp")
                    .body(resource);
        else if (imageName.endsWith(".webp"))
            return ResponseEntity.ok()
                    .header("Content-Type", "image/webp")
                    .body(resource);
        else
            return ResponseEntity.notFound().build();
    }
}

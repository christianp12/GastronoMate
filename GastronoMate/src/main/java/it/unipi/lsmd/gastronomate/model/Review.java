package it.unipi.lsmd.gastronomate.model;

import com.google.gson.Gson;
import it.unipi.lsmd.gastronomate.model.user.NormalUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class Review {
    private String id;
    private Recipe recipe;
    private NormalUser user;
    private Integer rating;
    private String reviewBody;
    private LocalDateTime datePublished;
    private LocalDateTime dateModified;



    public String toJson() {
        return new Gson().toJson(this);
    }

}

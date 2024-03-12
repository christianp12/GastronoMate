package it.unipi.lsmd.gastronomate.model;

import it.unipi.lsmd.gastronomate.model.user.NormalUser;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class Recipe {

    private String id;
    private String title;
    private NormalUser author;
    private String cookTime;
    private String prepTime;
    private String totalTime;
    private LocalDateTime datePublished;
    private LocalDateTime dateModified;
    private String description;
    private List<String> keywords;
    private List<String> ingredients;
    private Double calories;
    private Double fatContent;
    private Double saturatedFatContent;
    private Double sodiumContent;
    private Double carbohydrateContent;
    private Double fiberContent;
    private Double sugarContent;
    private Double proteinContent;
    private Integer recipeServings;
    private String pictureUrl;
    private List<Review> reviews;
    private Double AverageRating;
    private Integer likes;
    private Integer reviewsCount;
}

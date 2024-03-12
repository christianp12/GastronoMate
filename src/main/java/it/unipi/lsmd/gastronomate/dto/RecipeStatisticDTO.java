package it.unipi.lsmd.gastronomate.dto;

import lombok.Data;

@Data
public class RecipeStatisticDTO {
    private String recipeId;
    private String title;
    private String pictureUrl;
    private String authorUsername;
    private String authorProfilePictureUrl;
    private Double averageRating;
    private Integer likes;
}

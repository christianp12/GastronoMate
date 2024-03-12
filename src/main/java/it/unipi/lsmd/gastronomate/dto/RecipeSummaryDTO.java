package it.unipi.lsmd.gastronomate.dto;


import it.unipi.lsmd.gastronomate.model.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeSummaryDTO {

    private String recipeId;
    private String title;
    private String pictureUrl;
    private List<String> keywords;
    private String authorUsername;
    private String authorProfilePictureUrl;
    private transient LocalDateTime datePublished;
    private Double averageRating;

    public static RecipeSummaryDTO fromRecipe(Recipe recipe){
        RecipeSummaryDTO recipeSummaryDTO = new RecipeSummaryDTO();

        recipeSummaryDTO.setRecipeId(recipe.getId());
        recipeSummaryDTO.setTitle(recipe.getTitle());
        recipeSummaryDTO.setPictureUrl(recipe.getPictureUrl());
        recipeSummaryDTO.setKeywords(recipe.getKeywords());
        recipeSummaryDTO.setAuthorUsername(recipe.getAuthor().getUsername());
        recipeSummaryDTO.setAuthorProfilePictureUrl(recipe.getAuthor().getProfilePictureUrl());
        recipeSummaryDTO.setDatePublished(recipe.getDatePublished());
        recipeSummaryDTO.setAverageRating(recipe.getAverageRating());


        return recipeSummaryDTO;

    }


}

package it.unipi.lsmd.gastronomate.dto;

import it.unipi.lsmd.gastronomate.model.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO extends RecipeSummaryDTO {

    /*
    *
    private String recipeId;
    private String title;
    private String pictureUrl;
    private List<String> keywords;
    private String authorUsername;
    private String authorProfilePictureUrl;
    private LocalDateTime datePublished;
    *
    */

    //mandatory fields
    private String description;

    //optional fields

    private String cookTime;
    private String prepTime;
    private String totalTime;
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


    private Integer likes;
    private Integer reviews;

    public boolean areEmptyNutritionFields(){
        return calories == null && fatContent == null && saturatedFatContent == null && sodiumContent == null && carbohydrateContent == null && fiberContent == null && sugarContent == null && proteinContent == null;
    }

    public String toJsArray(List<String> list){

        if (list == null)
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(String s : list){
            sb.append("\"");
            sb.append(s);
            sb.append("\"");
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }

    public static RecipeDTO fromRecipe(Recipe recipe){

        RecipeDTO recipeDTO = new RecipeDTO();

        recipeDTO.setRecipeId(recipe.getId());
        recipeDTO.setTitle(recipe.getTitle());
        recipeDTO.setPictureUrl(recipe.getPictureUrl());
        recipeDTO.setKeywords(recipe.getKeywords());
        recipeDTO.setAuthorUsername(recipe.getAuthor().getUsername());
        recipeDTO.setAuthorProfilePictureUrl(recipe.getAuthor().getProfilePictureUrl());
        recipeDTO.setDatePublished(recipe.getDatePublished());

        recipeDTO.setDescription(recipe.getDescription());
        recipeDTO.setCookTime(recipe.getCookTime());
        recipeDTO.setPrepTime(recipe.getPrepTime());
        recipeDTO.setTotalTime(recipe.getTotalTime());
        recipeDTO.setIngredients(recipe.getIngredients());
        recipeDTO.setCalories(recipe.getCalories());
        recipeDTO.setFatContent(recipe.getFatContent());
        recipeDTO.setSaturatedFatContent(recipe.getSaturatedFatContent());
        recipeDTO.setSodiumContent(recipe.getSodiumContent());
        recipeDTO.setCarbohydrateContent(recipe.getCarbohydrateContent());
        recipeDTO.setFiberContent(recipe.getFiberContent());
        recipeDTO.setSugarContent(recipe.getSugarContent());
        recipeDTO.setProteinContent(recipe.getProteinContent());
        recipeDTO.setRecipeServings(recipe.getRecipeServings());
        recipeDTO.setAverageRating(recipe.getAverageRating());

        recipeDTO.setLikes(recipe.getLikes() == null ? 0 : recipe.getLikes());
        recipeDTO.setReviews(recipe.getReviewsCount() == null ? 0 : recipe.getReviewsCount());

        return recipeDTO;

    }

    public static Map<String, Object> toMap(RecipeDTO recipeDTO){

        //put in map only the fields that are not null
        Map<String, Object> map = new HashMap<>();

        if(recipeDTO.getTitle() != null)
            map.put("title", recipeDTO.getTitle());
        if(recipeDTO.getPictureUrl() != null)
            map.put("ImageUrl", recipeDTO.getPictureUrl());
        if(recipeDTO.getKeywords() != null)
            map.put("Keywords", recipeDTO.getKeywords());
        if(recipeDTO.getDescription() != null)
            map.put("Description", recipeDTO.getDescription());
        if(recipeDTO.getCookTime() != null)
            map.put("CookTime", recipeDTO.getCookTime());
        if(recipeDTO.getPrepTime() != null)
            map.put("PrepTime", recipeDTO.getPrepTime());
        if(recipeDTO.getTotalTime() != null)
            map.put("TotalTime", recipeDTO.getTotalTime());
        if(recipeDTO.getIngredients() != null)
            map.put("ingredients", recipeDTO.getIngredients());
        if(recipeDTO.getCalories() != null)
            map.put("Calories", recipeDTO.getCalories());
        if(recipeDTO.getFatContent() != null)
            map.put("FatContent", recipeDTO.getFatContent());
        if(recipeDTO.getSaturatedFatContent() != null)
            map.put("SaturatedFatContent", recipeDTO.getSaturatedFatContent());
        if(recipeDTO.getSodiumContent() != null)
            map.put("SodiumContent", recipeDTO.getSodiumContent());
        if(recipeDTO.getCarbohydrateContent() != null)
            map.put("CarbohydrateContent", recipeDTO.getCarbohydrateContent());
        if(recipeDTO.getFiberContent() != null)
            map.put("FiberContent", recipeDTO.getFiberContent());
        if(recipeDTO.getSugarContent() != null)
            map.put("SugarContent", recipeDTO.getSugarContent());
        if(recipeDTO.getProteinContent() != null)
            map.put("ProteinContent", recipeDTO.getProteinContent());
        if(recipeDTO.getRecipeServings() != null)
            map.put("RecipeServings", recipeDTO.getRecipeServings());

        return map;

    }

    @Override
    public String toString() {
        return "RecipeDTO{" +
                "recipeId='" + getRecipeId() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", pictureUrl='" + getPictureUrl() + '\'' +
                ", keywords=" + getKeywords() +
                ", authorUsername='" + getAuthorUsername() + '\'' +
                ", authorProfilePictureUrl='" + getAuthorProfilePictureUrl() + '\'' +
                ", datePublished=" + getDatePublished() +
                "description='" + description + '\'' +
                ", cookTime='" + cookTime + '\'' +
                ", prepTime='" + prepTime + '\'' +
                ", totalTime='" + totalTime + '\'' +
                ", ingredients=" + ingredients +
                ", calories=" + calories +
                ", fatContent=" + fatContent +
                ", saturatedFatContent=" + saturatedFatContent +
                ", sodiumContent=" + sodiumContent +
                ", carbohydrateContent=" + carbohydrateContent +
                ", fiberContent=" + fiberContent +
                ", sugarContent=" + sugarContent +
                ", proteinContent=" + proteinContent +
                ", recipeServings=" + recipeServings +
                ", likes=" + likes +
                ", reviews=" + reviews +
                '}';
    }
}

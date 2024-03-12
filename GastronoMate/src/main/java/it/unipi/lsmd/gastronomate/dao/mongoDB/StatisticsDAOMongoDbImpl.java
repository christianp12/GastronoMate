package it.unipi.lsmd.gastronomate.dao.mongoDB;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.StatisticsDAO;
import it.unipi.lsmd.gastronomate.dto.RecipeStatisticDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.ascending;

public class StatisticsDAOMongoDbImpl extends MongoDbBaseDAO implements StatisticsDAO {
    @Override
    public Double[] getMontlySubscriptionsPercentage(Date start) throws DAOException {
        /*
         * This method should retrieve the monthly subscriptions percentage from the database.
         * The method should return an array of 12 elements, where each element represents the percentage of subscriptions for a specific month.
         */

        Double[] percentage = new Double[12];

        try {
            MongoCollection<Document> userCollection = getMongoClient().getDatabase("GastronoMate").getCollection("user");

            Bson filter;

           //if start is not null, then we want to filter the users that have been created after the start date and before the beginning of the next year
            if(start != null){
                filter = and(gte("CreationDate", start), lt("CreationDate", new Date(start.getYear() + 1, 0, 1)));
            }
            //if start is null, then we want to retrieve the users that have been created since the beginning of the current year
            else{
                filter = and(gte("CreationDate", new Date(new Date().getYear(), 0, 1)), lt("CreationDate", new Date(new Date().getYear() + 1, 0, 1)));
            }

            List<Bson> pipeline = Arrays.asList(

                    match(filter),
                    //exclude admin users, which have the "Type" field
                    match(exists("Type", false)),

                    project(fields(include("month"), computed("month", new Document("$month", "$CreationDate")))),

                    group("$month", sum("count", 1)),

                    group(null, sum("total", "$count"), push("counts", new Document("month", "$_id").append("count", "$count"))),

                    unwind("$counts"),

                    project(fields(include("month", "percentage"), computed("month", "$counts.month"), computed("percentage", new Document("$multiply", Arrays.asList(new Document("$divide", Arrays.asList("$counts.count", "$total")), 100))))),

                    sort(ascending("month"))
            );

            userCollection.aggregate(pipeline).forEach(document -> {

                try {
                    percentage[document.getInteger("month") - 1] = document.getDouble("percentage");

                } catch (Exception e) {}

            });


        } catch (MongoException e) {
            getLogger().severe("Error while retrieving the monthly subscriptions percentage" + e.getMessage());
            throw new DAOException("Error while retrieving the monthly subscriptions percentage", ErrorTypeEnum.DATABASE_ERROR);

        }catch (Exception e){

            getLogger().severe("Error while retrieving the monthly subscriptions percentage" + e.getMessage());
            throw new DAOException("Error while retrieving the monthly subscriptions percentage", ErrorTypeEnum.GENERIC_ERROR);
        }

        return percentage;
    }
    @Override
    public Map<String, Integer> getYearSubscriptions(Integer yearStart, Integer yearEnd) throws DAOException {
        /*
         * This method should retrieve the yearly subscriptions from the database.
         * The method should return a map of elemnts, here each element has a key which is the year and the value is the number of subscriptions for a specific year.
         */

        Map<String, Integer> yearlySubscriptions = new LinkedHashMap<>();

        Bson filter = empty();

        if (yearStart != null && yearEnd != null) {
            filter = and(gte("CreationDate", new Date(yearStart, 0, 1)), lt("CreationDate", new Date(yearEnd, 0, 1)));

        } else if (yearStart != null) {
            filter = gte("CreationDate", new Date(yearStart, 0, 1));

        } else if (yearEnd != null) {
            filter = lt("CreationDate", new Date(yearEnd, 0, 1));
        }

        try {
            MongoCollection<Document> userCollection = getMongoClient().getDatabase("GastronoMate").getCollection("user");

            // perform the query and return the result

            List<Bson> pipeline = Arrays.asList(

                    match(filter),
                    //exclude admin users, which have the "Type" field
                    match(exists("Type", false)),

                    project(fields(include("year"), computed("year", new Document("$year", "$CreationDate")))),
                    group("$year", sum("count", 1)),
                    project(fields(include("_id", "count"), computed("yearAsInt", new Document("$toInt", "$_id")))),
                    sort(ascending("yearAsInt"))
            );


            userCollection.aggregate(pipeline).forEach(document -> {

                try {
                    int year = document.getInteger("yearAsInt");
                    int count = document.getInteger("count");

                    yearlySubscriptions.put(String.valueOf(year), count);

                } catch (Exception e) {}

            });

        } catch (MongoException e) {
            getLogger().severe("Error while retrieving the yearly subscriptions" + e.getMessage());
            throw new DAOException("Error while retrieving the yearly subscriptions", ErrorTypeEnum.DATABASE_ERROR);

        }
        catch (Exception e){

            getLogger().severe("Error while retrieving the yearly subscriptions" + e.getMessage());
            throw new DAOException("Error while retrieving the yearly subscriptions", ErrorTypeEnum.GENERIC_ERROR);
        }

        return yearlySubscriptions;
    }

    @Override
    public Map<String, Integer> getUsersPerState(Date start, Date end) throws DAOException {

        Map<String, Integer> totalUsersByState = new LinkedHashMap<>();

        Bson filter = empty();

        if (start != null && end != null) {
            filter = and(gte("CreationDate", start), lt("CreationDate", end));

        } else if (start != null) {
            filter = gte("CreationDate", start);

        } else if (end != null) {
            filter = lt("CreationDate", end);
        }

        try{
            MongoCollection<Document> userCollection = getMongoClient().getDatabase("GastronoMate").getCollection("user");

            Document match = new Document("$match", filter);

            //exclude admin users, which have the "Type" field
            Document match2 = new Document("$match", exists("Type", false));

            Document group = new Document("$group", new Document("_id", new Document("state", "$Address.State")).append("count", new Document("$sum", 1)));

            Document sort = new Document("$sort", new Document("count", -1));

            Document project = new Document("$project", new Document("state", "$_id.state").append("count", "$count").append("_id", 0));


            userCollection.aggregate(Arrays.asList(match, match2, group, sort, project)).forEach(document -> {
                try {

                    totalUsersByState.put(document.getString("state"), document.getInteger("count"));

                } catch (Exception e) {}
            });

        }catch (MongoException e){
            getLogger().severe("Error while retrieving the city with the highest number of users" + e.getMessage());
            throw new DAOException("Error while retrieving the city with the highest number of users", ErrorTypeEnum.DATABASE_ERROR);

        }catch (Exception e){
            getLogger().severe("Error while retrieving the city with the highest number of users" + e.getMessage());
            throw new DAOException("Error while retrieving the city with the highest number of users", ErrorTypeEnum.GENERIC_ERROR);
        }

        return totalUsersByState;
    }


    @Override
    public Map<String, Integer> getMostPupularKeywords(Date start, Date end) throws DAOException {
        /*
         * This method should retrieve the top 5 most used keywords in recipes from the database.
         * The method should return a map where each item represents a keyword and the total count.
         */

        Map<String, Integer> mostPopularKeywords = new LinkedHashMap<>();

        Bson filter = empty();

        if (start != null && end != null) {
            filter = and(gte("DatePublished", start), lt("DatePublished", end));

        } else if (start != null) {
            filter = gte("DatePublished", start);

        } else if (end != null) {
            filter = lt("DatePublished", end);
        }


        try {
            MongoCollection<Document> recipeCollection = getMongoClient().getDatabase("GastronoMate").getCollection("recipe");


            List<Bson> pipeline = Arrays.asList(
                    match(exists("Keywords")),
                    match(filter),
                    unwind("$Keywords"),
                    group("$Keywords", sum("count", 1)),
                    sort(Sorts.descending("count")),
                    limit(5)
            );


            recipeCollection.aggregate(pipeline).forEach(document -> {

                try {
                    mostPopularKeywords.put(document.getString("_id"), document.getInteger("count"));

                } catch (Exception e) {}

            });

        } catch (MongoException e) {
            getLogger().severe("Error while retrieving the most popular keywords" + e.getMessage());
            throw new DAOException("Error while retrieving the most popular keywords", ErrorTypeEnum.DATABASE_ERROR);

        }catch (Exception e){

            getLogger().severe("Error while retrieving the most popular keywords" + e.getMessage());
            throw new DAOException("Error while retrieving the most popular keywords", ErrorTypeEnum.GENERIC_ERROR);
        }

        return mostPopularKeywords;
    }

    @Override
    public List<RecipeStatisticDTO> getBestScoredRecipes() throws DAOException {

        List<RecipeStatisticDTO> bestScoredRecipes = new ArrayList<>();

        try {

            MongoCollection<Document> recipeCollection = getMongoClient().getDatabase("GastronoMate").getCollection("review");

            // perform the query and return the result

            List<Bson> pipeline = Arrays.asList(
                    match(exists("Rating")),
                    group("$Recipe", avg("AverageRating", "$Rating")),
                    sort(Sorts.descending("AverageRating")),
                    limit(10),
                    //project the recipe's fileds by taking each filed from the _id
                    project(fields(include("Recipe"), computed("Recipe", "$_id"), excludeId(), include("AverageRating")))
            );

            recipeCollection.aggregate(pipeline).forEach(document -> {

                try {
                    RecipeStatisticDTO recipe = new RecipeStatisticDTO();

                    recipe.setRecipeId(document.get("Recipe", Document.class).getObjectId("RecipeId").toString());
                    recipe.setTitle(document.get("Recipe", Document.class).getString("Title"));
                    recipe.setAuthorUsername(document.get("Recipe", Document.class).getString("AuthorUsername"));
                    try{
                        recipe.setAuthorProfilePictureUrl(document.get("Recipe", Document.class).getString("AuthorProfilePictureUrl"));
                    }catch (Exception e){}

                    recipe.setAverageRating(document.getDouble("AverageRating"));

                    bestScoredRecipes.add(recipe);

                } catch (Exception e) {}
            });

        } catch (MongoException e) {
            getLogger().severe("Error while retrieving the best scored recipes" + e.getMessage());
            throw new DAOException("Error while retrieving the best scored recipes", ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe("Error while retrieving the best scored recipes" + e.getMessage());
            throw new DAOException("Error while retrieving the best scored recipes", ErrorTypeEnum.GENERIC_ERROR);

        }
        return bestScoredRecipes;
    }

    @Override
    public List<UserSummaryDTO> getInfluencers() throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public List<RecipeStatisticDTO> getMostLikedRecipes() throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

}

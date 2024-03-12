package it.unipi.lsmd.gastronomate.dao.mongoDB;

import com.mongodb.*;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.TransactionBody;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.RecipeDAO;
import it.unipi.lsmd.gastronomate.dto.PageDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.model.Recipe;
import it.unipi.lsmd.gastronomate.model.user.NormalUser;
import javafx.util.Pair;
import org.bson.Document;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.nin;

public class RecipeDAOMongoDbImpl extends MongoDbBaseDAO implements RecipeDAO {
    private static final String DATABASE_NAME= "GastronoMate";
    private static final String COLLECTION_NAME= "recipe";
    private static final String CREATE_RECIPE_ERR_MSG="RecipeDAOMongoDbImpl: createRecipe: Error occurred while inserting a new recipe: ";
    private static final String READ_RECIPE_ERR_MSG="RecipeDAOMongoDbImpl: readRecipe: Error occurred while reading recipe by id: ";
    private static final String UPDATE_RECIPE_ERR_MSG="RecipeDAOMongoDbImpl: updateRecipe: Error occurred while updating a recipe with id: ";
    private static final String DELETE_RECIPE_ERR_MSG="RecipeDAOMongoDbImpl: deleteRecipe: Error occurred while deleting recipe with id: ";

    @Override
    public void createRecipe(RecipeDTO recipe) throws DAOException {

        try{
            //create recipe Document
            Document recipeDocument = new Document();
            //mandatory field
            recipeDocument.append("title", recipe.getTitle());

            if(recipe.getCookTime() != null)
                recipeDocument.append("CookTime", recipe.getCookTime());
            if(recipe.getPrepTime() != null)
                recipeDocument.append("PrepTime", recipe.getPrepTime());
            if(recipe.getTotalTime() != null)
                recipeDocument.append("TotalTime", recipe.getTotalTime());

            recipe.setDatePublished(LocalDateTime.now());
            recipeDocument.append("DatePublished", recipe.getDatePublished());
            //mandatory field
            recipeDocument.append("Description", recipe.getDescription());

            if (recipe.getKeywords() != null)
                recipeDocument.append("Keywords", recipe.getKeywords());
            if (recipe.getIngredients() != null)
                recipeDocument.append("ingredients", recipe.getIngredients());
            if(recipe.getCalories() != null)
                recipeDocument.append("Calories", recipe.getCalories());
            if (recipe.getFatContent() != null)
                recipeDocument.append("FatContent", recipe.getFatContent());
            if (recipe.getSaturatedFatContent() != null)
                recipeDocument.append("SaturatedFatContent", recipe.getSaturatedFatContent());
            if (recipe.getSodiumContent() != null)
                recipeDocument.append("SodiumContent", recipe.getSodiumContent());
            if (recipe.getCarbohydrateContent() != null)
                recipeDocument.append("CarbohydrateContent", recipe.getCarbohydrateContent());
            if (recipe.getFiberContent() != null)
                recipeDocument.append("FiberContent", recipe.getFiberContent());
            if (recipe.getSugarContent() != null)
                recipeDocument.append("SugarContent", recipe.getSugarContent());
            if (recipe.getProteinContent() != null)
                recipeDocument.append("ProteinContent", recipe.getProteinContent());
            if (recipe.getRecipeServings() != null)
                recipeDocument.append("RecipeServings", recipe.getRecipeServings());
            if (recipe.getPictureUrl() != null)
                recipeDocument.append("ImageUrl", recipe.getPictureUrl());

            //author embedded document
            Document authorDocument = new Document();
            authorDocument.append("Username", recipe.getAuthorUsername());

            if (recipe.getAuthorProfilePictureUrl() != null)
                authorDocument.append("ProfilePictureUrl", recipe.getAuthorProfilePictureUrl());

            recipeDocument.append("Author", authorDocument);

            //insert recipe
            MongoCollection<Document> recipeCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
            InsertOneResult result = recipeCollection.insertOne(recipeDocument);


            if(result.getInsertedId() != null) {

                BsonObjectId bsonObjectId = (BsonObjectId) result.getInsertedId();
                ObjectId objectId = bsonObjectId.getValue();
                String objectIdString = objectId.toHexString();
                recipe.setRecipeId(objectIdString);

            }

            else
                throw new MongoException("RecipeDAOMongoDbImpl: createRecipe: Error occurred while inserting a new recipe");

        } catch (MongoException e) {
            getLogger().severe(CREATE_RECIPE_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        }catch (Exception e) {
            getLogger().severe(CREATE_RECIPE_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public RecipeDTO readRecipe(String recipeID) throws DAOException {
        try {
            MongoCollection<Document> recipeCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            Document projection = new Document();

            projection.append("_id", 1);
            projection.append("title", 1);
            projection.append("CookTime", 1);
            projection.append("PrepTime", 1);
            projection.append("TotalTime", 1);
            projection.append("DatePublished", 1);
            projection.append("Description", 1);
            projection.append("Keywords", 1);
            projection.append("ingredients", 1);
            projection.append("Calories", 1);
            projection.append("FatContent", 1);
            projection.append("SaturatedFatContent", 1);
            projection.append("SodiumContent", 1);
            projection.append("CarbohydrateContent", 1);
            projection.append("FiberContent", 1);
            projection.append("SugarContent", 1);
            projection.append("ProteinContent", 1);
            projection.append("RecipeServings", 1);
            projection.append("Author", 1);
            projection.append("ImageUrl", 1);
            projection.append("AverageRating", 1);
            projection.append("Likes", 1);
            projection.append("Reviews", 1);

            //Try to find recipe's details
            Document recipeDocument = recipeCollection.find(eq("_id", new ObjectId(recipeID))).projection(projection).first();

            // If the recipe is found, map the document to a RecipeDTO object
            if (recipeDocument != null) {

                Recipe recipe = mapDocumentToRecipe(recipeDocument);

                return RecipeDTO.fromRecipe(recipe);
            } else {
                // Otherwise, throw an exception
                throw new MongoException("RecipeDAOMongoDbImpl: readRecipe: Recipe with id: " + recipeID + " was not found");
            }

        } catch (MongoException e) {
            getLogger().severe(READ_RECIPE_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(READ_RECIPE_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public Boolean likedRecipe(String recipeId, String username) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    public List<RecipeSummaryDTO> searchFirstNRecipes(String query, Integer n, String loggedUser) throws DAOException {
        /*
            This method returns the first 'n' recipes that match a specific filter
            if n is null, it returns the first 10 recipes that match the filter
            if the query is null, it returns the first 'n' recipes
         */
        try {
            MongoCollection<Document> recipeCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
            List<Document> documents;

            //RecipeSummaryDTO is returned instead of RecipeDTO because it contains less data
            Document projection = new Document();
            projection.append("_id", 1);
            projection.append("title", 1);
            projection.append("ImageUrl", 1);
            projection.append("Author", 1);
            projection.append("Keywords", 1);
            projection.append("DatePublished", 1);

            if (n == null) {
                n = 10;
            }

            Document notOwnPosts = new Document();
            notOwnPosts.append("Author.Username", new Document("$ne", loggedUser));

            if (query != null && !query.isEmpty()){

                Document titleFilter = new Document();
                titleFilter.append("title", Pattern.compile(query, Pattern.CASE_INSENSITIVE));
                Document descriptionFilter = new Document();
                descriptionFilter.append("Description", Pattern.compile(query, Pattern.CASE_INSENSITIVE));
                Document kewordsFilter = new Document();
                kewordsFilter.append("Keywords", Pattern.compile(query, Pattern.CASE_INSENSITIVE));

                Document orFilter = new Document();
                orFilter.append("$or", List.of(titleFilter, descriptionFilter, kewordsFilter));

                Document andFilter = new Document();
                andFilter.append("$and", List.of(orFilter, notOwnPosts));

                documents = recipeCollection.find(andFilter).projection(projection).limit(n).sort(new Document("DatePublished", -1)).into(new ArrayList<>());
            }
            else {

                documents = recipeCollection.find(notOwnPosts).projection(projection).limit(n).sort(new Document("DatePublished", -1)).into(new ArrayList<>());
            }

            List<Recipe> recipeList = documents.stream().map(this::mapDocumentToRecipe).toList();

            return recipeList.stream().map(RecipeSummaryDTO::fromRecipe).toList();

        } catch (MongoException e) {
            getLogger().severe(READ_RECIPE_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(READ_RECIPE_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }
    @Override
    public void updateRecipe(String recipeId, Map<String, Object> updateParams) throws DAOException {
        try {
            MongoCollection<Document> reviewCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            Document update = new Document();

            // Add the modified fields from the update parameters map
            updateParams.forEach((key, value) -> update.append(key, value));

            // Update the modification date
            update.append("DateModified", LocalDateTime.now());

            // Build the document for the update operation
            Document set = new Document("$set", update);

            // Perform the update operation in the MongoDB database
           UpdateResult result = reviewCollection.updateOne(eq("_id", new ObjectId(recipeId)), set);

           if(result.getModifiedCount() == 0)
               throw new MongoException("RecipeDAOMongoDbImpl: updateRecipe: Recipe with id: " + recipeId + " was not found");

        } catch (MongoException e) {
            getLogger().severe(UPDATE_RECIPE_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe(UPDATE_RECIPE_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public void deleteRecipe(String recipeId) throws DAOException {
        //delete recipe with a specific id
        try {
            MongoCollection<Document> reviewCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            DeleteResult result = reviewCollection.deleteOne(eq("_id", new ObjectId(recipeId)));

            if (result.getDeletedCount() == 0)
                throw new MongoException("RecipeDAOMongoDbImpl: deleteRecipe: Recipe with id: " + recipeId + " was not found");

        } catch (MongoException e) {
            getLogger().severe(DELETE_RECIPE_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(DELETE_RECIPE_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public List<RecipeSummaryDTO> suggestedRecipes(String username, int limit) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public PageDTO<Pair<RecipeSummaryDTO, Boolean>> followedUsersRecipe(int page, String username) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void likeRecipe(String recipeId, String username) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }
    @Override
    public void unlikeRecipe(String recipeId, String username) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Integer getNumOfLikes(String recipeId) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Integer getNumOfReviews(String recipeId) throws DAOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void updateNumOfLikes(String recipeId, Integer likes) throws DAOException {
        try{
            MongoCollection<Document> recipeCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            //update the number of likes for a specific recipe
            recipeCollection.updateOne(eq("_id", new ObjectId(recipeId)), new Document("$set", new Document("Likes", likes)));


        } catch (MongoException e) {
            getLogger().severe("RecipeDAOMongoDbImpl: updateNumOfLikes: Error occured while updating a recipe: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe("RecipeDAOMongoDbImpl: updateNumOfLikes: Error occured while updating a recipe: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public void updateNumOfReviews(String recipeId, Integer reviews) throws DAOException {
        try{
            MongoCollection<Document> recipeCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            //update the number of reviews for a specific recipe
             recipeCollection.updateOne(eq("_id", new ObjectId(recipeId)), new Document("$set", new Document("Reviews", reviews)));


        } catch (MongoException e) {
            getLogger().severe("RecipeDAOMongoDbImpl: updateNumOfReviews: Error occured while updating a recipe: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe("RecipeDAOMongoDbImpl: updateNumOfReviews: Error occured while updating a recipe: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public void updateAuthorRedundantData(UserSummaryDTO userSummaryDTO, String oldUsername) throws DAOException {

        try(ClientSession session = getMongoClient().startSession()) {

            TransactionOptions txnOptions = TransactionOptions.builder()
                    .readPreference(ReadPreference.primary()) //reading from primary data should be the most up to date
                    .readConcern(ReadConcern.LOCAL) //read from local data
                    .writeConcern(WriteConcern.MAJORITY) //write to the majority of replicas
                    .build();

            //create author embedded Document
            Document author = new Document();

            author.append("Username", userSummaryDTO.getUsername());
            if(userSummaryDTO.getProfilePictureUrl() != null)
                author.append("ProfilePictureUrl", userSummaryDTO.getProfilePictureUrl());


            TransactionBody txnBody = new TransactionBody<String>() {
                public String execute(){

                    MongoCollection<Document> recipeCollection = getMongoClient().getDatabase("GastronoMate").getCollection("recipe");

                    //update the author data in all the recipes
                    recipeCollection.updateMany(session, eq("Author.Username", oldUsername), new Document("$set", new Document("Author", author)));

                    return null;
                }
            };

            //start the transaction
            session.withTransaction(txnBody, txnOptions);

        } catch (MongoException e) {
            getLogger().severe("RecipeDAOMongoDbImpl: updateAuthorRedundantData: Error occurred while updating author data: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe("RecipeDAOMongoDbImpl: updateAuthorRedundantData: Error occurred while updating author data: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }

    }

    @Override
    public void deleteRecipesWithNoAuthor() throws DAOException {
        try {
            MongoCollection<Document> userCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection("user");

            List<String> usernames = userCollection.find().projection(new Document("username", 1)).map(document -> document.getString("username")).into(new ArrayList<>());

            try (ClientSession session = getMongoClient().startSession()) {

                TransactionOptions txnOptions = TransactionOptions.builder()
                        .readPreference(ReadPreference.primary()) //reading from primary data should be the most up to date
                        .readConcern(ReadConcern.LOCAL) //read from local data
                        .writeConcern(WriteConcern.MAJORITY) //write to the majority of replicas
                        .build();


                TransactionBody txnBody = new TransactionBody<String>() {
                    public String execute() {

                        MongoCollection<Document> recipeCollection = getMongoClient().getDatabase("GastronoMate").getCollection("recipe");

                        //update the author data in all the recipes
                        recipeCollection.deleteMany(session, nin("Author.Username", usernames));

                        return null;
                    }
                };

                //start the transaction
                session.withTransaction(txnBody, txnOptions);

            }
        }catch (MongoException e) {
            getLogger().severe("RecipeDAOMongoDbImpl: deleteRecipesWithNoAuthor: Error occurred while deleting recipes with no author: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe("RecipeDAOMongoDbImpl: deleteRecipesWithNoAuthor: Error occurred while deleting recipes with no author: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }


   private Recipe mapDocumentToRecipe(Document recipeDocument) {

        Recipe recipe = new Recipe();
        NormalUser author = new NormalUser();
        recipe.setAuthor(author);

        try { recipe.getAuthor().setUsername(recipeDocument.get("Author",Document.class).getString("Username")); } catch (Exception e) {}

        try { recipe.getAuthor().setProfilePictureUrl(recipeDocument.get("Author",Document.class).getString("ProfilePictureUrl")); } catch (Exception e) {}

        try { recipe.setId(recipeDocument.getObjectId("_id").toString()); } catch (Exception e) {}
        try { recipe.setTitle(recipeDocument.getString("title")); } catch (Exception e) {}
        try { recipe.setCookTime(recipeDocument.getString("CookTime")); } catch (Exception e) {}
        try { recipe.setPrepTime(recipeDocument.getString("PrepTime")); } catch (Exception e) {}
        try { recipe.setTotalTime(recipeDocument.getString("TotalTime")); } catch (Exception e) {}
        try { recipe.setDatePublished(recipeDocument.getDate("DatePublished").toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime()); } catch (Exception e) {}
        try { recipe.setDescription(recipeDocument.getString("Description")); } catch (Exception e) {}
        try { recipe.setKeywords(recipeDocument.getList("Keywords", String.class)); } catch (Exception e) {}
        try { recipe.setIngredients(recipeDocument.getList("ingredients", String.class)); } catch (Exception e) {}
        try { recipe.setCalories(recipeDocument.getDouble("Calories")); } catch (Exception e) {}
        try { recipe.setFatContent(recipeDocument.getDouble("FatContent")); } catch (Exception e) {}
        try { recipe.setSaturatedFatContent(recipeDocument.getDouble("SaturatedFatContent")); } catch (Exception e) {}
        try { recipe.setSodiumContent(recipeDocument.getDouble("SodiumContent")); } catch (Exception e) {}
        try { recipe.setCarbohydrateContent(recipeDocument.getDouble("CarbohydrateContent")); } catch (Exception e) {}
        try { recipe.setFiberContent(recipeDocument.getDouble("FiberContent")); } catch (Exception e) {}
        try { recipe.setSugarContent(recipeDocument.getDouble("SugarContent")); } catch (Exception e) {}
        try { recipe.setProteinContent(recipeDocument.getDouble("ProteinContent")); } catch (Exception e) {}
        try { recipe.setRecipeServings(recipeDocument.getInteger("RecipeServings")); } catch (Exception e) {}
        try { recipe.setPictureUrl(recipeDocument.getString("ImageUrl")); } catch (Exception e) {}
        try { recipe.setAverageRating(recipeDocument.getDouble("AverageRating")); } catch (Exception e) {}
        try { recipe.setLikes(recipeDocument.getInteger("Likes")); } catch (Exception e) {}
        try { recipe.setReviewsCount(recipeDocument.getInteger("Reviews")); } catch (Exception e) {}

        return recipe;
    }

}

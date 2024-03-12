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
import it.unipi.lsmd.gastronomate.dao.interfaces.ReviewDAO;
import it.unipi.lsmd.gastronomate.dto.PageDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.ReviewDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.model.Recipe;
import it.unipi.lsmd.gastronomate.model.Review;
import it.unipi.lsmd.gastronomate.model.user.NormalUser;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;

public class ReviewDAOMongoDbImpl extends MongoDbBaseDAO implements ReviewDAO {

    @Override
    public void createReview(ReviewDTO reviewDTO) throws DAOException {

        try {
            //create recipe embedded Document
            Document recipe = new Document();
            recipe.append("RecipeId", new ObjectId(reviewDTO.getRecipeId()));
            recipe.append("Title", reviewDTO.getRecipeTitle());
            recipe.append("AuthorUsername", reviewDTO.getRecipeAuthorUsername());

            if (reviewDTO.getRecipeAuthorProfilePictureUrl() != null)
                recipe.append("AuthorProfilePictureUrl", reviewDTO.getRecipeAuthorProfilePictureUrl());

            //create author embedded Document
            Document author = new Document();
            author.append("Username", reviewDTO.getAuthorUsername());
            if (reviewDTO.getAuthorProfilePictureUrl() != null)
                author.append("ProfilePictureUrl", reviewDTO.getAuthorProfilePictureUrl());
            //create review Document
            Document reviewDoc = new Document();

            reviewDoc.append("Author", author);
            reviewDoc.append("Recipe", recipe);

            if (reviewDTO.getReviewBody() != null)
                reviewDoc.append("Text", reviewDTO.getReviewBody());
            if (reviewDTO.getRating() != null)
                reviewDoc.append("Rating", reviewDTO.getRating());

            reviewDTO.setDatePublished(LocalDateTime.now());

            reviewDoc.append("DateSubmitted", reviewDTO.getDatePublished());

            /*
            Once the Document is prepared, we try to find the recipe.
            If the recipe is found, we insert the review. However, even if the recipe is found, it may have been deleted and the update
            may not have been performed yet. For a period of time, the recipe will be present in the review collection but not in the recipe collection.
            Eventually, reviews referring to a deleted recipe will be deleted as well.

             */
            MongoCollection<Document> recipeCollection = getMongoClient().getDatabase("GastronoMate").getCollection("recipe");
            MongoCollection<Document> reviewCollection = getMongoClient().getDatabase("GastronoMate").getCollection("review");

            //check if the recipe exists: counting the number of recipes with the same id
            long recipeCount = recipeCollection.countDocuments(eq("_id", new ObjectId(reviewDTO.getRecipeId())));

            if (recipeCount == 0) {
                getLogger().info("ReviewDAOMongoDbImpl:createReview: Recipe with id: " + reviewDTO.getRecipeAuthorUsername() + " does not exist");
                throw new MongoException("Recipe with id: " + reviewDTO.getRecipeAuthorUsername() + " does not exist");
            }

            //insert the review
            InsertOneResult result = reviewCollection.insertOne(reviewDoc);

            if (result.getInsertedId() != null) {

                BsonObjectId bsonObjectId = (BsonObjectId) result.getInsertedId();
                ObjectId objectId = bsonObjectId.getValue();
                String objectIdString = objectId.toHexString();
                reviewDTO.setReviewId(objectIdString);
            } else
                throw new MongoException("No review inserted");

        } catch (MongoException e) {

            getLogger().severe("ReviewDAOMongoDbImpl:createReview: Error occured while inserting a new review: " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);
            throw ex;


        } catch (Exception e) {
            getLogger().severe("ReviewDAOMongoDbImpl:createReview: Error occured while inserting a new review: " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
            throw ex;
        }
    }

    @Override
    public void deleteReviewsWithNoRecipe() throws DAOException {
        try {

            MongoCollection<Document> recipeCollection = getMongoClient().getDatabase("GastronoMate").getCollection("recipe");

            List<Document> recipesIds = recipeCollection.find().projection(new Document("_id", 1)).into(new ArrayList<>());

            List<ObjectId> ids = recipesIds.stream().map(document -> document.getObjectId("_id")).toList();

            try (ClientSession session = getMongoClient().startSession()) {

                TransactionOptions txnOptions = TransactionOptions.builder()
                        .readPreference(ReadPreference.primary()) //reading from primary data should be the most up to date
                        .readConcern(ReadConcern.LOCAL) //read from local data
                        .writeConcern(WriteConcern.MAJORITY) //write to the majority of replicas
                        .build();

                TransactionBody txnBody = new TransactionBody<String>() {
                    public String execute() {

                        MongoCollection<Document> reviewCollection = getMongoClient().getDatabase("GastronoMate").getCollection("review");
                        //update the recipe data in all the reviews
                        reviewCollection.deleteMany(session, nin("Recipe.RecipeId", ids));

                        return null;
                    }
                };

                session.withTransaction(txnBody, txnOptions);
            }

        } catch (MongoException e) {
            getLogger().severe("ReviewDAOMongoDbImpl:deletePendingReviews: Error occured while deleting reviews: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe("ReviewDAOMongoDbImpl:deletePendingReviews: Error occured while deleting reviews: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public void deleteReviewsWithNoAuthor() throws DAOException {
        try {

            MongoCollection<Document> userCollection = getMongoClient().getDatabase("GastronoMate").getCollection("user");

            List<Document> usernameDocList = userCollection.find().projection(new Document("username", 1)).into(new ArrayList<>());

            List<String> usernames = usernameDocList.stream().map(document -> document.getString("username")).toList();

            try (ClientSession session = getMongoClient().startSession()) {

                TransactionOptions txnOptions = TransactionOptions.builder()
                        .readPreference(ReadPreference.primary()) //reading from primary data should be the most up to date
                        .readConcern(ReadConcern.LOCAL) //read from local data
                        .writeConcern(WriteConcern.MAJORITY) //write to the majority of replicas
                        .build();

                TransactionBody txnBody = new TransactionBody<String>() {
                    public String execute() {

                        MongoCollection<Document> reviewCollection = getMongoClient().getDatabase("GastronoMate").getCollection("review");
                        //update the recipe data in all the reviews
                        reviewCollection.deleteMany(session, nin("Author.Username", usernames));

                        return null;
                    }
                };

                session.withTransaction(txnBody, txnOptions);
            }

        } catch (MongoException e) {
            getLogger().severe("ReviewDAOMongoDbImpl:deletePendingReviews: Error occured while deleting reviews: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe("ReviewDAOMongoDbImpl:deletePendingReviews: Error occured while deleting reviews: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }

    }

    @Override
    public void deleteReview(String reviewId, String authorUsername, String recipeId) throws DAOException {
        //delete review with a specific id
        try {
            MongoCollection<Document> reviewCollection = getMongoClient().getDatabase("GastronoMate").getCollection("review");

            DeleteResult result = reviewCollection.deleteOne(eq("_id", new ObjectId(reviewId)));

            if (result.getDeletedCount() == 0) {
                throw new MongoException("Review with id: " + reviewId + " does not exist");
            }


        } catch (MongoException e) {
            getLogger().severe("ReviewDAOMongoDbImpl:deleteReview Error occured while deleting a review: " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);
            throw ex;

        } catch (Exception e) {
            getLogger().severe("ReviewDAOMongoDbImpl:deleteReview: Error occured while deleting a review: " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
            throw ex;
        }
    }

    @Override
    public PageDTO<ReviewDTO> getReviewsByRecipe(String recipeId, int page) throws DAOException {
        try {
            MongoCollection<Document> reviewCollection = getMongoClient().getDatabase("GastronoMate").getCollection("review");

            Document projection = new Document();

            projection.append("_id", 1);
            projection.append("Author", 1);
            projection.append("Recipe.RecipeId", 1);
            projection.append("Rating", 1);
            projection.append("Text", 1);
            projection.append("DateSubmitted", 1);

            int offset = (page - 1) * PageDTO.getPAGE_SIZE();

            List<Document> reviews = reviewCollection.find(eq("Recipe.RecipeId", new ObjectId(recipeId))).projection(projection).sort(new Document("DateSubmitted", -1)).skip(offset).limit(PageDTO.getPAGE_SIZE()).into(new ArrayList<>());

            List<ReviewDTO> reviewDTOList = reviews.stream().map(this::readReview).toList().stream().map(ReviewDTO::fromReview).toList();

            PageDTO<ReviewDTO> pageDTO = new PageDTO<>();
            pageDTO.setEntries(reviewDTOList);

            int totalCount = (int) reviewCollection.countDocuments(eq("Recipe.RecipeId", new ObjectId(recipeId)));

            pageDTO.setTotalCount(totalCount);
            pageDTO.setCurrentPage(page);

            return pageDTO;

        } catch (MongoException e) {
            getLogger().severe("ReviewDAOMongoDbImpl: Error occured while getting reviews by recipe (getReviewsByRecipe): " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);
            throw ex;


        } catch (Exception e) {
            getLogger().severe("ReviewDAOMongoDbImpl: Error occured while getting reviews by recipe (getReviewsByRecipe): " + e.getMessage());
            DAOException ex = new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
            throw ex;
        }
    }

    @Override
    public void updateReview(String reviewId, String reviewText, Integer reviewRating) throws DAOException {
        try {
            MongoCollection<Document> reviewCollection = getMongoClient().getDatabase("GastronoMate").getCollection("review");

            Document update = new Document();

            if (reviewText != null)
                update.append("Text", reviewText);

            if (reviewRating != null)
                update.append("Rating", reviewRating);

            update.append("DateModified", LocalDateTime.now());

            Document set = new Document("$set", update);

            UpdateResult result = reviewCollection.updateOne(eq("_id", new ObjectId(reviewId)), set);

            if (result.getModifiedCount() == 0) {
                throw new MongoException("Review with id: " + reviewId + " does not exist");
            }


        } catch (MongoException e) {
            getLogger().severe("ReviewDAOMongoDbImpl:updateReview: Error occured while updating a review: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe("ReviewDAOMongoDbImpl:updateReview: Error occured while updating a review: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }

    }

    @Override
    public void updateRecipeRedundantData(RecipeSummaryDTO recipeSummaryDTO) throws DAOException {

        /*
        update recipe data in the review collection in a transaction

        the update is performed in a transaction to ensure that all the reviews are updated to avoid inconsistencies.
        we don't want to have some reviews with the old recipe data and some with the new one

        this update is performed when a recipe is updated but not immediately, and it's performed by a background thread
         */

        try (ClientSession session = getMongoClient().startSession()) {

            TransactionOptions txnOptions = TransactionOptions.builder()
                    .readPreference(ReadPreference.primary()) //reading from primary data should be the most up to date
                    .readConcern(ReadConcern.LOCAL) //read from local data
                    .writeConcern(WriteConcern.MAJORITY) //write to the majority of replicas
                    .build();

            //create recipe emebedded embedded Document
            Document recipe = new Document();

            recipe.append("RecipeId", new ObjectId(recipeSummaryDTO.getRecipeId()));

            if (recipeSummaryDTO.getTitle() != null)
                recipe.append("Title", recipeSummaryDTO.getTitle());

            TransactionBody txnBody = new TransactionBody<String>() {
                public String execute() {

                    MongoCollection<Document> reviewCollection = getMongoClient().getDatabase("GastronoMate").getCollection("review");

                    //update the recipe data in all the reviews
                    reviewCollection.updateMany(session, eq("Recipe.RecipeId", new ObjectId(recipeSummaryDTO.getRecipeId())), new Document("$set", new Document("Recipe", recipe)));

                    return null;
                }
            };

            //start the transaction
            session.withTransaction(txnBody, txnOptions);

        } catch (MongoException e) {
            getLogger().severe("ReviewDAOMongoDbImpl:UpdateRecipeRedundantData: Error occured while updating a review: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe("ReviewDAOMongoDbImpl:UpdateRecipeRedundantData: Error occured while updating a review: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public void updateAuthorRedundantData(UserSummaryDTO userSummaryDTO, String oldUsername) throws DAOException {
           /*
        update author data in the review collection in a transaction

        the update is performed in a transaction to ensure that all the reviews are updated to avoid inconsistencies.
        we don't want to have some reviews with the old recipe data and some with the new one

        this update is performed when author'info is updated but not immediately, and it's performed by a background thread
         */

        try (ClientSession session = getMongoClient().startSession()) {

            TransactionOptions txnOptions = TransactionOptions.builder()
                    .readPreference(ReadPreference.primary()) //reading from primary data should be the most up to date
                    .readConcern(ReadConcern.LOCAL) //read from local data
                    .writeConcern(WriteConcern.MAJORITY) //write to the majority of replicas
                    .build();

            //create author embedded Document
            Document reviewAuthor = new Document();

            if (userSummaryDTO.getUsername() != null)
                reviewAuthor.append("Username", userSummaryDTO.getUsername());

            if (userSummaryDTO.getProfilePictureUrl() != null)
                reviewAuthor.append("ProfilePictureUrl", userSummaryDTO.getProfilePictureUrl());

            Document recipeAuthor = new Document();

            if (userSummaryDTO.getUsername() != null)
                recipeAuthor.append("AuthorUsername", userSummaryDTO.getUsername());

            if (userSummaryDTO.getProfilePictureUrl() != null)
                recipeAuthor.append("AuthorProfilePictureUrl", userSummaryDTO.getProfilePictureUrl());

            TransactionBody txnBody = new TransactionBody<String>() {
                public String execute() {

                    MongoCollection<Document> reviewCollection = getMongoClient().getDatabase("GastronoMate").getCollection("review");

                    //update the review author's info
                    reviewCollection.updateMany(session, eq("Author.Username", oldUsername), new Document("$set", new Document("Author", reviewAuthor)));

                    //update the recipe author's info
                    reviewCollection.updateMany(session, eq("Recipe.AuthorUsername", oldUsername), new Document("$set", new Document("Recipe", recipeAuthor)));

                    return null;
                }
            };

            //start the transaction
            session.withTransaction(txnBody, txnOptions);

        } catch (MongoException e) {
            getLogger().severe("ReviewDAOMongoDbImpl:UpdateRecipeRedundantData: Error occured while updating a review: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe("ReviewDAOMongoDbImpl:UpdateRecipeRedundantData: Error occured while updating a review: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public Boolean isReviewed(String recipeId, String username) throws DAOException {
        throw new UnsupportedOperationException("Not Supported");
    }

    @Override
    public void updateAverageRating() throws DAOException {
        //this task will be performed by a periodic background thread

        //step 1) delete reviews with no recipe
        deleteReviewsWithNoRecipe();

        //step 2) delete reviews with no author
        deleteReviewsWithNoAuthor();

        try {

            //step 3) evaluate the average rating of the recipes
            MongoCollection<Document> reviewCollection = getMongoClient().getDatabase("GastronoMate").getCollection("review");

            //group the reviews by recipe id and calculate the average rating
            List<Document> reviews = reviewCollection.aggregate(List.of(
                    new Document("$match", new Document("Rating", new Document("$exists", true))),
                    new Document("$group", new Document("_id", "$Recipe.RecipeId").append("AverageRating", new Document("$avg", "$Rating")))
            )).into(new ArrayList<>());

            //step 4) update the recipe collection with the average rating
            MongoCollection<Document> recipeCollection = getMongoClient().getDatabase("GastronoMate").getCollection("recipe");

            //update the recipe collection with the average rating
            for (Document review : reviews) {
                recipeCollection.updateOne(eq("_id", review.getObjectId("_id")), new Document("$set", new Document("AverageRating", review.getDouble("AverageRating"))));
            }


        }catch(MongoException e) {
            getLogger().severe("ReviewDAOMongoDbImpl:updateAverageRating: Error occured while updating the average rating: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch(Exception e) {
            getLogger().severe("ReviewDAOMongoDbImpl:updateAverageRating: Error occured while updating the average rating: " + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    private Review readReview(Document document) {
        Review review = new Review();
        NormalUser author = new NormalUser();
        review.setUser(author);
        try {
            review.setId(document.getObjectId("_id").toString());

        } catch (Exception e) {}

        try{
            review.setRecipe(readRedundantRecipeData(document));
        } catch (Exception e) {}

        try{
            review.getUser().setUsername(document.get("Author", Document.class).getString("Username"));
        } catch (Exception e) {}

        try{
            review.getUser().setProfilePictureUrl(document.get("Author", Document.class).getString("ProfilePictureUrl"));
        } catch (Exception e) {}

        try{
            review.setRating(document.getInteger("Rating"));
        } catch (Exception e) {}

        try{
            review.setReviewBody(document.getString("Text"));
        } catch (Exception e) {}

        try{
            review.setDatePublished(document.getDate("DateSubmitted").toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
        } catch (Exception e) {}

        try{
            review.setDateModified(document.getDate("DateModified").toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
        } catch (Exception e) {}

        return review;
    }

    private Recipe readRedundantRecipeData(Document document) {
        Recipe recipe = new Recipe();
        NormalUser author = new NormalUser();
        recipe.setAuthor(author);
        try {
            recipe.setId(document.get("Recipe", Document.class).getObjectId("RecipeId").toString());
        } catch (Exception e) {
        }

        try {
            recipe.setTitle(document.get("Recipe", Document.class).getString("Title"));
        } catch (Exception e) {
        }

        try {
            recipe.getAuthor().setUsername(document.get("Recipe", Document.class).getString("AuthorUsername"));
        } catch (Exception e) {
        }
        try {
            recipe.getAuthor().setProfilePictureUrl(document.get("Recipe", Document.class).getString("AuthorProfilePictureUrl"));
        } catch (Exception e) {
        }
        return recipe;
    }

}

package it.unipi.lsmd.gastronomate.dao.mongoDB;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.lsmd.gastronomate.dao.exceptions.AuthenticationException;
import it.unipi.lsmd.gastronomate.dao.exceptions.DAOException;
import it.unipi.lsmd.gastronomate.dao.exceptions.DuplicatedException;
import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import it.unipi.lsmd.gastronomate.dao.interfaces.UserDAO;
import it.unipi.lsmd.gastronomate.dto.LoggedUserDTO;
import it.unipi.lsmd.gastronomate.dto.RecipeSummaryDTO;
import it.unipi.lsmd.gastronomate.dto.UserDTO;
import it.unipi.lsmd.gastronomate.dto.UserSummaryDTO;
import it.unipi.lsmd.gastronomate.model.Address;
import it.unipi.lsmd.gastronomate.model.Recipe;
import it.unipi.lsmd.gastronomate.model.enums.AccountSatusTypeEnum;
import it.unipi.lsmd.gastronomate.model.enums.RoleTypeEnum;
import it.unipi.lsmd.gastronomate.model.enums.UserTypeEnum;
import it.unipi.lsmd.gastronomate.model.user.Admin;
import it.unipi.lsmd.gastronomate.model.user.NormalUser;
import it.unipi.lsmd.gastronomate.model.user.User;
import javafx.util.Pair;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class UserDAOMongoDbImpl extends MongoDbBaseDAO implements UserDAO {
    private static final String DATABASE_NAME = "GastronoMate";
    private static final String COLLECTION_NAME = "user";
    private static final String CREATE_USER_ERR_MSG = "UserDAOMongoDbImpl: createUser: Error occurred while inserting a new user: ";
    private static final String READ_USER_ERR_MSG = "UserDAOMongoDbImpl: readUser: Error occurred while reading user by username: ";
    private static final String UPDATE_USER_ERR_MSG = "UserDAOMongoDbImpl: updateUser: Error occurred while updating a user with id: ";
    private static final String DELETE_USER_ERR_MSG = "UserDAOMongoDbImpl: deleteUser: Error occurred while deleting user with username: ";
    private static final String AUTHENTICATE_USER_ERR_MSG = "UserDAOMongoDbImpl: authenticateUser: Error occurred while authenticating user: ";

    public void createUser(UserDTO user) throws DAOException {
        try {
            //create user Document
            Document userDocument = new Document();

            userDocument.append("FullName", user.getFullName());
            userDocument.append("username", user.getUsername());
            userDocument.append("Email", user.getEmail());
            userDocument.append("Password", user.getPassword());

            Document addressDocument = new Document();
               addressDocument.append("City", user.getAddress().getCity());
               addressDocument.append("State", user.getAddress().getState());
               addressDocument.append("Country", user.getAddress().getCountry());

            userDocument.append("Address", addressDocument);

            userDocument.append("DateOfBirth", user.getDateOfBirth());

            if (user.getProfilePictureUrl() != null) 
                userDocument.append("ProfilePictureUrl", user.getProfilePictureUrl());


            user.setCreationDate(LocalDateTime.now());
            userDocument.append("CreationDate", LocalDateTime.now());

            MongoCollection<Document> userCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            if(userCollection.countDocuments(eq("username",user.getUsername())) != 0)
               throw new DuplicatedException("Username already exists");

            if(userCollection.countDocuments(eq("Email",user.getEmail())) != 0)
                throw new DuplicatedException("Email already exists");

            //insert user
            InsertOneResult result = userCollection.insertOne(userDocument);
            
            if (result.getInsertedId() != null){
                BsonObjectId bsonObjectId = (BsonObjectId) result.getInsertedId();
                ObjectId objectId = bsonObjectId.getValue();
                String objectIdString = objectId.toHexString();
                user.setId(objectIdString);
            }

            else throw new MongoException(CREATE_USER_ERR_MSG);


        } catch (MongoException e) {
            getLogger().severe(CREATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (DuplicatedException e) {
            getLogger().severe(CREATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DUPLICATED_ELEMENT);

        } catch (Exception e) {
            getLogger().severe(CREATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public UserDTO readUser(String username) throws DAOException {
        try {
            MongoCollection<Document> userCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            Document projection = new Document();
            projection.append("_id", 1);
            projection.append("FullName", 1);
            projection.append("username", 1);

            projection.append("ProfilePictureUrl", 1);
            projection.append("Description", 1);
            projection.append("Recipes", 1);
            projection.append("Followers", 1);
            projection.append("Followed", 1);


            Document userDocument = userCollection.find(eq("username", username)).projection(projection).first();

            if (userDocument != null) {
                User user = mapDocumentToUser(userDocument);
                return UserDTO.fromUser(user);

            } else {
                throw new MongoException(READ_USER_ERR_MSG + username);
            }

        } catch (MongoException e) {
            getLogger().severe(READ_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(READ_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }


    @Override
    public UserDTO readLoggedUserProfile(String username) throws DAOException {
        try {
            MongoCollection<Document> userCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            Document projection = new Document();
            projection.append("_id", 1);
            projection.append("FullName", 1);
            projection.append("username", 1);
            projection.append("Email", 1);
            projection.append("ProfilePictureUrl", 1);
            projection.append("Description", 1);
            projection.append("Recipes", 1);
            projection.append("Followers", 1);
            projection.append("Followed", 1);
            projection.append("DateOfBirth", 1);
            projection.append("Address", 1);

            Document userDocument = userCollection.find(eq("username", username)).projection(projection).first();

            if (userDocument != null) {
                User user = mapDocumentToUser(userDocument);
                return UserDTO.fromUser(user);
            } else {
                throw new MongoException(READ_USER_ERR_MSG + username);
            }

        } catch (MongoException e) {
            getLogger().severe(READ_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(READ_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    public void updateUser(String userId, Map<String, Object> updateParams) throws DAOException {
        try {
            MongoCollection<Document> userCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            Document update = new Document();

            if (updateParams.containsKey("username")) {
                if(userCollection.countDocuments(eq("username",updateParams.get("username"))) != 0)
                    throw new DuplicatedException("Username already exists");
            }

            if(updateParams.containsKey("Email")) {
                if(userCollection.countDocuments(eq("Email",updateParams.get("Email"))) != 0)
                    throw new DuplicatedException("Email already exists");
            }

            // Add the modified fields from the update parameters map
            updateParams.forEach((key, value) -> update.append(key, value));

            update.append("DateModified", LocalDateTime.now());

            Document set = new Document("$set", update);

            UpdateResult result = userCollection.updateOne(eq("_id", new ObjectId(userId)), set);

            if (result.getModifiedCount() == 0) {
               throw new MongoException(UPDATE_USER_ERR_MSG + userId);
            }

        } catch (MongoException e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (DuplicatedException e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DUPLICATED_ELEMENT);

        } catch (Exception e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }


    @Override
    public void deleteUser(String username) throws DAOException {
        //delete user with a specific id
        try {
            MongoCollection<Document> userCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

             DeleteResult result = userCollection.deleteOne(eq("username", username));

             if (result.getDeletedCount() == 0) {
                 throw new MongoException(DELETE_USER_ERR_MSG + username);
             }

        } catch (MongoException e) {
            getLogger().severe(DELETE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(DELETE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public Map<String, String> authenticate(String field, String password) throws DAOException {
        try {
            MongoCollection<Document> userCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            Document projection = new Document();
            projection.append("username", 1);
            projection.append("ProfilePictureUrl", 1);
            projection.append("AccountStatus", 1);
            projection.append("Type", 1);

            Document userDocument;

            if (field.contains("@"))
                userDocument = userCollection.find(and(eq("Email", field), eq("Password", password))).projection(projection).first();

            else {
                // Find the user document with the provided username
                userDocument = userCollection.find(and(eq("username", field), eq("Password", password))).projection(projection).first();
            }

            if (userDocument != null) {

                User user = mapDocumentToUser(userDocument);



                if (user instanceof NormalUser normalUser && ( normalUser.getAccountStatus() == AccountSatusTypeEnum.BANNED || normalUser.getAccountStatus() == AccountSatusTypeEnum.SUSPENDED))
                    throw new AuthenticationException("Account is banned or suspended");


                Map<String, String> parameters = new HashMap<>();

                parameters.put("username", user.getUsername());

                if (user.getUserType() != null)
                    parameters.put("type", user.getUserType().toString());

               if(user instanceof NormalUser normalUser && normalUser.getProfilePictureUrl() != null)
                   parameters.put("profilePictureUrl", ((NormalUser) user).getProfilePictureUrl());


                return parameters;

            } else {
                throw new AuthenticationException("Bad credentials");
            }

        }catch (AuthenticationException e) {

            getLogger().severe(AUTHENTICATE_USER_ERR_MSG + e.getMessage());

            if (e.getMessage().equals("Bad credentials"))
                throw new DAOException(e.getMessage(), ErrorTypeEnum.AUTHENTICATION_ERROR);

            else
                throw new DAOException(e.getMessage(), ErrorTypeEnum.ACCOUNT_STATUS_ERROR);
        }

        catch (MongoException e) {
            getLogger().severe(e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        } catch (Exception e) {
            getLogger().severe(AUTHENTICATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public void followUser(String username, String followedUsername) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void unfollowUser(String username, String followedUsername) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public Integer getNumOfFollowers(String username) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public Integer getNumOfFollowed(String username) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void updateFollowers(String username, Integer followers) throws DAOException {
        try{
            MongoCollection<Document> userCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            Document update = new Document();
            update.append("Followers", followers);

            Document set = new Document("$set", update);

           userCollection.updateOne(eq("username", username), set);

        } catch (MongoException e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);

        }

    }

    @Override
    public void updateFollowed(String username, Integer followed) throws DAOException {
        try{
            MongoCollection<Document> userCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            Document update = new Document();
            update.append("Followed", followed);

            Document set = new Document("$set", update);

            userCollection.updateOne(eq("username", username), set);

        } catch (MongoException e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);


        } catch (Exception e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);

        }

    }

    @Override
    public List<UserSummaryDTO> searchFirstNUsers(String query, Integer n, String loggedUser) throws DAOException {
       try{
              MongoCollection<Document> userCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

              Document projection = new Document();

              projection.append("username", 1);

              projection.append("ProfilePictureUrl", 1);

              if(n == null)
                  n = 10;

              List<Document> documents;

           if (query != null && !query.isEmpty()){

               Document usernameFilter = new Document();
               usernameFilter.append("username", Pattern.compile(query, Pattern.CASE_INSENSITIVE));

               Document notOwnAccountFilter = new Document();
                notOwnAccountFilter.append("username", new Document("$ne", loggedUser));

               Document andFilter = new Document();
               andFilter.append("$and", List.of(usernameFilter, notOwnAccountFilter));

               documents = userCollection.find(andFilter).projection(projection).limit(n).sort(new Document("username", 1)).into(new ArrayList<>());
           }
           else {
               documents = userCollection.find().projection(projection).limit(n).sort(new Document("username", 1)).into(new ArrayList<>());
           }

           System.out.println(documents);


           return documents.stream().map(userDocument -> {
                User user = mapDocumentToUser(userDocument);
                return UserSummaryDTO.fromUser(user);

              }).toList();

       }catch (MongoException e) {
              getLogger().severe(READ_USER_ERR_MSG + e.getMessage());
              throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

       }catch (Exception e) {
              getLogger().severe(READ_USER_ERR_MSG + e.getMessage());
              throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
       }
    }

    @Override
    public List<UserSummaryDTO> suggestedUsers(String username, int limit) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public List<UserSummaryDTO> showListOfFollowedUsers(String loggedUser, String username) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public List<UserSummaryDTO> showListOfFollowers(String loggedUser, String username) throws DAOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void addRecipeToUser(RecipeSummaryDTO recipe) throws DAOException {
        try{
            MongoCollection<Document> userCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            Document recipeDoc = new Document();



            recipeDoc.append("RecipeId", new ObjectId(recipe.getRecipeId()));
            recipeDoc.append("Title", recipe.getTitle());
            recipeDoc.append("DatePublished", recipe.getDatePublished());
            if (recipe.getPictureUrl() != null)
                recipeDoc.append("ImageUrl", recipe.getPictureUrl());

            Document update = new Document();
            update.append("Recipes", recipeDoc);

            Document set = new Document("$push", update);

            userCollection.updateOne(eq("username", recipe.getAuthorUsername()), set);

        } catch (MongoException e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        }catch (Exception e) {
            getLogger().severe(READ_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public void removeRecipeFromUser(String username, String recipeId) throws DAOException {
        try{
            MongoCollection<Document> userCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            Document query = new Document("RecipeId", new ObjectId(recipeId));
            Document update = new Document("$pull", new Document("Recipes", query));

            userCollection.updateOne(eq("username", username), update);

        } catch (MongoException e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        }catch (Exception e) {
            getLogger().severe(READ_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public void updateRecipeInUser(RecipeSummaryDTO recipe) throws DAOException {
        try{
            MongoCollection<Document> userCollection = getMongoClient().getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);


            Document update = new Document();

            if(recipe.getTitle() != null)
                update.append("Recipes.$.Title", recipe.getTitle());
            if (recipe.getPictureUrl() != null)
                update.append("Recipes.$.ImageUrl", recipe.getPictureUrl());

            userCollection.updateOne(and( eq("username", recipe.getAuthorUsername()), eq("Recipes.RecipeId", new ObjectId(recipe.getRecipeId())) ) , new Document("$set", update));

        } catch (MongoException e) {
            getLogger().severe(UPDATE_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.DATABASE_ERROR);

        }catch (Exception e) {
            getLogger().severe(READ_USER_ERR_MSG + e.getMessage());
            throw new DAOException(e.getMessage(), ErrorTypeEnum.GENERIC_ERROR);
        }
    }

    @Override
    public Boolean isFollowed(String loggedUser, String username) {
        throw new UnsupportedOperationException("not supported");
    }


    private User mapDocumentToUser(Document document) {
        String id = null;
        String fullName = null;
        String email =  null;
        String username = null;
        String password = null;
        Address address = new Address();
        LocalDateTime creationDate = null;
        String type = null;

        try {
            id = document.getObjectId("_id").toString();
        }catch (Exception e) {}

        try {
            fullName = document.getString("FullName");
        }catch (Exception e) {}

        try {
            email = document.getString("Email");
        }catch (Exception e) {}

        try {
            username = document.getString("username");
        }catch (Exception e) {}

        try {
            password = document.getString("Password");
        }catch (Exception e) {}

        try {
            Document addressDocument = document.get("Address", Document.class);
            String city = (String) addressDocument.get("City");
            String state = (String) addressDocument.get("State");
            String country = (String) addressDocument.get("Country");
            address = new Address(city, state, country);
        }catch (Exception e) {}

        try {
            creationDate = document.getDate("CreationDate").toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();

        } catch (Exception e) {
        }

        try {
            type = document.getString("Type");
        } catch (Exception e) {
        }

        if (type == null) {
            NormalUser user = new NormalUser();
            user.setId(id);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setUsername(username);
            user.setPassword(password);
            user.setAddress(address);
            user.setCreationDate(creationDate);

            try {
                user.setProfilePictureUrl(document.getString("ProfilePictureUrl"));
            } catch (Exception e) {
            }
            try {
                user.setDescription(document.getString("Description"));
            } catch (Exception e) {
            }
            try {
                user.setFollowers(document.getInteger("Followers"));
            } catch (Exception e) {
            }
            try {
                user.setFollowed(document.getInteger("Followed"));
            } catch (Exception e) {
            }
            try {
                user.setDateOfBirth(document.getDate("DateOfBirth").toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
            } catch (Exception e) {
            }
            try {
                String accountStatus = document.getString("AccountStatus");
                user.setAccountStatus(AccountSatusTypeEnum.valueOf(accountStatus));
            } catch (Exception e) {
            }


            try {

                List<Document> recipeDocuments = (List<Document>) document.get("Recipes");

                List<Recipe> recipes = recipeDocuments.stream().map(recipeDocument -> {
                    Recipe recipe = new Recipe();
                    try {
                        recipe.setId(recipeDocument.getObjectId("RecipeId").toString());
                    } catch (Exception e) {
                    }
                    try {
                        recipe.setTitle(recipeDocument.getString("Title"));
                    } catch (Exception e) {
                    }
                    try {
                        recipe.setDatePublished(recipeDocument.getDate("DatePublished").toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
                    } catch (Exception e) {
                    }
                    try {
                        recipe.setPictureUrl(recipeDocument.getString("ImageUrl"));
                    } catch (Exception e) {
                    }

                    return recipe;

                }).toList();

                user.setRecipeList(recipes);

            } catch (Exception e) {
                user.setRecipeList(new ArrayList<>());
            }

            return user;

        } else if (type.equals("ADMIN")) {
            Admin user = new Admin();
            user.setId(id);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setUsername(username);
            user.setPassword(password);
            user.setAddress(address);
            user.setCreationDate(creationDate);
            user.setUserType(UserTypeEnum.ADMIN);

            try {
                String role = document.getString("Role");
                user.setRole(RoleTypeEnum.valueOf(role));
            } catch (Exception e) {
            }
            return user;
        }

        return null;
    }


}
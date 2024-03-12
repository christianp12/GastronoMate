package it.unipi.lsmd.gastronomate.dto;

import it.unipi.lsmd.gastronomate.model.Address;
import it.unipi.lsmd.gastronomate.model.enums.AccountSatusTypeEnum;
import it.unipi.lsmd.gastronomate.model.enums.UserTypeEnum;
import it.unipi.lsmd.gastronomate.model.user.Admin;
import it.unipi.lsmd.gastronomate.model.user.NormalUser;
import it.unipi.lsmd.gastronomate.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends UserSummaryDTO {

    private String id;

    private String fullName;
    private String email;
    private String password;

    private String description;

    private Address address;


    private LocalDateTime creationDate;
    private LocalDateTime dateOfBirth;

    private AccountSatusTypeEnum accountStatus;
    private UserTypeEnum userType;

    private List<RecipeSummaryDTO> recipes;

    private Integer followers;
    private Integer followed;



    public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setAddress(user.getAddress());

        userDTO.setCreationDate(user.getCreationDate());

        if (user instanceof NormalUser) {
            NormalUser normalUser = (NormalUser) user;
            userDTO.setDescription(normalUser.getDescription());

            userDTO.setRecipes(normalUser.getRecipeList().stream().map(recipe -> {
                RecipeSummaryDTO recipeSummaryDTO = new RecipeSummaryDTO();

                recipeSummaryDTO.setRecipeId(recipe.getId());
                recipeSummaryDTO.setTitle(recipe.getTitle());
                recipeSummaryDTO.setPictureUrl(recipe.getPictureUrl());
                recipeSummaryDTO.setDatePublished(recipe.getDatePublished());

                return recipeSummaryDTO;
            }).toList());

            userDTO.setDateOfBirth(normalUser.getDateOfBirth());
            userDTO.setProfilePictureUrl(normalUser.getProfilePictureUrl());

            userDTO.setFollowers( (normalUser.getFollowers() == null) ? 0 : normalUser.getFollowers());
            userDTO.setFollowed( (normalUser.getFollowed() == null) ? 0 : normalUser.getFollowed());
        }

        return userDTO;
    }
}
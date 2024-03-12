package it.unipi.lsmd.gastronomate.model.user;

import it.unipi.lsmd.gastronomate.model.Recipe;
import it.unipi.lsmd.gastronomate.model.Review;
import it.unipi.lsmd.gastronomate.model.enums.AccountSatusTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NormalUser extends User {

    private LocalDateTime dateOfBirth;
    private String profilePictureUrl;
    private String description;
    private AccountSatusTypeEnum accountStatus;
    private List<Recipe> recipeList;
    private List<Review> reviewList;
    private Integer followers;
    private Integer followed;

    public String toString(){
        return "NormalUser{" +
                "id='" + super.getId() + '\'' +
                ", fullName='" + super.getFullName() + '\'' +
                ", email='" + super.getEmail() + '\'' +
                ", username='" + super.getUsername() + '\'' +
                ", password='" + super.getPassword() + '\'' +
                ", address=" + super.getAddress() +
                ", userType=" + super.getUserType() +
                ", dateCreated=" + super.getCreationDate() +
                ", dateOfBirth=" + dateOfBirth +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", accountStatus=" + accountStatus +
                ", recipeList=" + recipeList +
                ", reviewList=" + reviewList +
                ", followers=" + followers +
                ", following=" + followed +
                '}';
    }


}
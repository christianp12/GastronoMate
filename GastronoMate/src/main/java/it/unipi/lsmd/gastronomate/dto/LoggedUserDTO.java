package it.unipi.lsmd.gastronomate.dto;

import it.unipi.lsmd.gastronomate.model.enums.UserTypeEnum;
import it.unipi.lsmd.gastronomate.model.user.Admin;
import it.unipi.lsmd.gastronomate.model.user.NormalUser;
import it.unipi.lsmd.gastronomate.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoggedUserDTO {

    private String username;
    private String profilePicture;
    private UserTypeEnum userType;


    public static LoggedUserDTO fromUser(User user) {

        if(user instanceof NormalUser normalUser){
            return new LoggedUserDTO(normalUser.getUsername(),normalUser.getProfilePictureUrl(), null);
        }

        if (user instanceof Admin admin){
            return new LoggedUserDTO(admin.getUsername(),null, UserTypeEnum.ADMIN);
        }

        return null;
    }
}
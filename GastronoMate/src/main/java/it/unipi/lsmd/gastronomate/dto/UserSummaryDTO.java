package it.unipi.lsmd.gastronomate.dto;

import it.unipi.lsmd.gastronomate.model.user.NormalUser;
import it.unipi.lsmd.gastronomate.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {

    private String username;
    private String profilePictureUrl;

    public static UserSummaryDTO fromUser(User user) {
        UserSummaryDTO userSummaryDTO = new UserSummaryDTO();

        userSummaryDTO.setUsername(user.getUsername());

        if(user instanceof NormalUser)
            userSummaryDTO.setProfilePictureUrl(((NormalUser) user).getProfilePictureUrl());

        return userSummaryDTO;
    }
}

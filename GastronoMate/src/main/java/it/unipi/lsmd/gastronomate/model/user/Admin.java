package it.unipi.lsmd.gastronomate.model.user;

import it.unipi.lsmd.gastronomate.model.enums.RoleTypeEnum;
import it.unipi.lsmd.gastronomate.model.user.User;
import lombok.Data;

@Data
public class Admin extends User {
    private RoleTypeEnum role;  //default is manager
}

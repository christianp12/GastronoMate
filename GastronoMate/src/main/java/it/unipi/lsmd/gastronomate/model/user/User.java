package it.unipi.lsmd.gastronomate.model.user;

import it.unipi.lsmd.gastronomate.model.Address;
import it.unipi.lsmd.gastronomate.model.enums.UserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {
   private String id;
   private String fullName;
   private String email;
   private String username;
   private String password;
   private Address address;
   private UserTypeEnum userType; //default is normal user
   private LocalDateTime creationDate;
}
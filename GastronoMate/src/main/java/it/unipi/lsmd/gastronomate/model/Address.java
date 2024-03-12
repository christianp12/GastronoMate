package it.unipi.lsmd.gastronomate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String city;
    private String state;
    private String country;
}
package com.example.store.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {
    Long id;
    String name;
    Type type;
    Timestamp created;
    public enum Type{
        ALL, CHEESE, PROTEIN, WRAP, VEGGIES
    }
}

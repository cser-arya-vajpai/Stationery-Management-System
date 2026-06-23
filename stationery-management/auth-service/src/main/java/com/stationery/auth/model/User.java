package com.stationery.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity    //tells Spring Data JPA(our DB library) that this class represents a db table. JPA will automatically generate the table if it does not exist and map rows from db to java objects of this class.
@Table(name = "users") //tells JPA to explicitely name table as "users" 
@Data  //to generate all getters, setters, toString() and equals() automatically, for every field.
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
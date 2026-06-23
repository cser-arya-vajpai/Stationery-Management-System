package com.stationery.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//This class maps directly to the stationery_items table in the inventory_db database.


@Entity   //This class is to be mapped to the database.       
@Table(name = "stationery_items")  //explicitely naming our table.
@Data   //generates getters, setters, string representations automatically for us. 
@Builder //a helper class that makes objects but in a chained way. 
@NoArgsConstructor  //non-parameterized constructor is generated. Required by Hibernate Engine. 
@AllArgsConstructor //parameterized constructor is generated.
public class StationeryItem {

    @Id    //Marks the below field as primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)   //Implements auto-increment for this field. 
    private Long id;

    @Column(nullable = false)  //Column is used to define some settings in the Columns of our table. Here, we want Name Column should have no entry as null.
    private String name;

    @Enumerated(EnumType.STRING) //Means, It has to save only and only string values, pre-defined in Category.java, NOT  NUMBERS.
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer minimumQuantity;

    @Column(updatable = false)  //You cannot update it later.
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String lastUpdatedBy;

    @PrePersist //Tells JPA: Execute the method below immediately before saving this entity to the database fro the first time.
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate //Updates the updatedAt variable to the current server system time. 
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
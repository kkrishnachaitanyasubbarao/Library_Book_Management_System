package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;


@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Boolean isAvailable = true;

    @Column(nullable = false)
    private Integer totalCopies = 0;

    @Column(nullable = false)
    private Integer availableCopies = 0;

    @Column(nullable = false)
    private Boolean deleted = false;

    public void decrementAvailableCopies() {
        if (availableCopies > 0) {
            availableCopies--;
            isAvailable = availableCopies > 0;
        }
    }

    public void incrementAvailableCopies() {
        if (availableCopies < totalCopies) {
            availableCopies++;
            isAvailable = true;
        }
    }

}

package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "borrowers")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Borrower {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipType membershipType = MembershipType.BASIC;

    @Column(nullable = false)
    private Integer maxBorrowLimit;

    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void setMaxBorrowLimit() {
        if (membershipType != null) {
            this.maxBorrowLimit = membershipType.getMaxBorrowLimit();
        }
    }

}

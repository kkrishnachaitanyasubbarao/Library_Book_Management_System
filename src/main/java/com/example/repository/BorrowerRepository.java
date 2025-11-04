package com.example.repository;

import com.example.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, UUID> {

    Optional<Borrower> findByEmail(String email);

    @Query("SELECT DISTINCT br.borrower FROM BorrowRecord br " +
            "WHERE br.active = true AND br.returnDate IS NULL " +
            "AND br.dueDate < CURRENT_DATE")
    List<Borrower> findBorrowersWithOverdueBooks();


}

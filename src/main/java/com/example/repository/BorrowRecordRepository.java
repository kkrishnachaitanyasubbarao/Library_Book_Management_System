package com.example.repository;



import com.example.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, UUID> {

    List<BorrowRecord> findByBorrowerIdAndActiveTrueAndReturnDateIsNull(UUID borrowerId);

    List<BorrowRecord> findByBorrowerId(UUID borrowerId);

    Optional<BorrowRecord> findByBookIdAndBorrowerIdAndActiveTrueAndReturnDateIsNull(
            UUID bookId, UUID borrowerId);

    List<BorrowRecord> findByActiveTrueAndReturnDateIsNull();

    @Query("SELECT br.book.id as bookId, br.book.title as title, " +
            "COUNT(br) as borrowCount " +
            "FROM BorrowRecord br " +
            "GROUP BY br.book.id, br.book.title " +
            "ORDER BY COUNT(br) DESC")
    List<Object[]> findTopBorrowedBooks();

    @Query("SELECT br FROM BorrowRecord br " +
            "WHERE br.active = true AND br.returnDate IS NULL " +
            "AND br.dueDate < CURRENT_DATE")
    List<BorrowRecord> findOverdueRecords();

    @Query("SELECT br.borrower.id as borrowerId, " +
            "br.borrower.name as borrowerName, " +
            "COUNT(br) as totalBorrowed, " +
            "SUM(CASE WHEN br.returnDate IS NULL AND br.dueDate < CURRENT_DATE THEN 1 ELSE 0 END) as overdueCount, " +
            "SUM(br.fineAmount) as totalFines " +
            "FROM BorrowRecord br " +
            "GROUP BY br.borrower.id, br.borrower.name")
    List<Object[]> findBorrowerActivity();


}

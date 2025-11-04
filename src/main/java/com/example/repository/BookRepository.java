package com.example.repository;

import com.example.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    Optional<Book> findByTitleAndAuthor(String title, String author);

    Page<Book> findByDeletedFalse(Pageable pageable);

    Page<Book> findByCategoryAndDeletedFalse(String category, Pageable pageable);

    Page<Book> findByIsAvailableTrueAndDeletedFalse(Pageable pageable);

    Page<Book> findByCategoryAndIsAvailableTrueAndDeletedFalse(String category, Pageable pageable);

    List<Book> findByCategoryAndDeletedFalseAndIdNot(String category, UUID excludeId);

    List<Book> findByAuthorAndDeletedFalseAndIdNot(String author, UUID excludeId);

    @Query("SELECT b.category as category, " +
            "SUM(b.availableCopies) as available, " +
            "SUM(b.totalCopies) as total " +
            "FROM Book b WHERE b.deleted = false " +
            "GROUP BY b.category")
    List<Object[]> getAvailabilitySummary();




}

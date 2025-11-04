package com.example.service;

import com.example.dto.*;
import com.example.entity.Book;
import com.example.entity.BorrowRecord;
import com.example.entity.Borrower;
import com.example.entity.FinePolicy;
import com.example.exception.BookNotAvailableException;
import com.example.exception.BookNotFoundException;
import com.example.exception.BorrowLimitExceededException;
import com.example.exception.BorrowerNotFoundException;
import com.example.repository.BookRepository;
import com.example.repository.BorrowRecordRepository;
import com.example.repository.BorrowerRepository;
import com.example.repository.FinePolicyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final FinePolicyRepository finePolicyRepository;

    private static final BigDecimal DEFAULT_FINE_PER_DAY = new BigDecimal("5.00");

    @Transactional
    public BorrowRecordDTO borrowBook(BorrowRequestDTO request) {
        // Validate borrower
        Borrower borrower = borrowerRepository.findById(request.getBorrowerId())
                .orElseThrow(() -> new BorrowerNotFoundException(
                        "Borrower not found with id: " + request.getBorrowerId()));

        // Validate book
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException(
                        "Book not found with id: " + request.getBookId()));

        if (book.getDeleted()) {
            throw new BookNotFoundException("Book has been deleted");
        }

        // Check borrow limit
        List<BorrowRecord> activeRecords = borrowRecordRepository
                .findByBorrowerIdAndActiveTrueAndReturnDateIsNull(borrower.getId());

        if (activeRecords.size() >= borrower.getMaxBorrowLimit()) {
            throw new BorrowLimitExceededException(
                    "Borrower has reached maximum borrow limit of " + borrower.getMaxBorrowLimit());
        }

        // Check availability
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book is not available");
        }

        // Create borrow record
        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setBorrower(borrower);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));
        record.setActive(true);

        // Update book availability
        book.decrementAvailableCopies();
        bookRepository.save(book);

        record = borrowRecordRepository.save(record);
        return convertToDTO(record);
    }

    @Transactional
    public BorrowRecordDTO returnBook(ReturnRequestDTO request) {
        // Find active borrow record
        BorrowRecord record = borrowRecordRepository
                .findByBookIdAndBorrowerIdAndActiveTrueAndReturnDateIsNull(
                        request.getBookId(), request.getBorrowerId())
                .orElseThrow(() -> new IllegalStateException(
                        "No active borrow record found for this book and borrower"));

        LocalDate returnDate = LocalDate.now();
        record.setReturnDate(returnDate);
        record.setActive(false);

        // Calculate fine if overdue
        if (returnDate.isAfter(record.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(record.getDueDate(), returnDate);
            BigDecimal finePerDay = getFinePerDay(record.getBook().getCategory());
            record.setFineAmount(finePerDay.multiply(new BigDecimal(daysLate)));
        }

        // Update book availability
        Book book = record.getBook();
        book.incrementAvailableCopies();
        bookRepository.save(book);

        record = borrowRecordRepository.save(record);
        return convertToDTO(record);
    }

    public List<BorrowRecordDTO> getActiveRecords() {
        List<BorrowRecord> records = borrowRecordRepository.findByActiveTrueAndReturnDateIsNull();
        return records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TopBookDTO> getTopBorrowedBooks(int limit) {
        List<Object[]> results = borrowRecordRepository.findTopBorrowedBooks();
        return results.stream()
                .limit(limit)
                .map(row -> new TopBookDTO(
                        (UUID) row[0],
                        (String) row[1],
                        ((Number) row[2]).longValue()
                ))
                .collect(Collectors.toList());
    }

    public List<BorrowerActivityDTO> getBorrowerActivity() {
        List<Object[]> results = borrowRecordRepository.findBorrowerActivity();
        return results.stream()
                .map(row -> new BorrowerActivityDTO(
                        (UUID) row[0],
                        (String) row[1],
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue(),
                        (BigDecimal) row[4]
                ))
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 10 * * ?") // Running the job daily at 10 AM
    public void flagOverdueRecords() {
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords();
        System.out.println("Found " + overdueRecords.size() + " overdue records");

    }

    private BigDecimal getFinePerDay(String category) {
        return finePolicyRepository.findByCategory(category)
                .map(FinePolicy::getFinePerDay)
                .orElse(DEFAULT_FINE_PER_DAY);
    }

    private BorrowRecordDTO convertToDTO(BorrowRecord record) {
        BorrowRecordDTO dto = new BorrowRecordDTO();
        dto.setId(record.getId());
        dto.setBookId(record.getBook().getId());
        dto.setBookTitle(record.getBook().getTitle());
        dto.setBorrowerId(record.getBorrower().getId());
        dto.setBorrowerName(record.getBorrower().getName());
        dto.setBorrowDate(record.getBorrowDate());
        dto.setDueDate(record.getDueDate());
        dto.setReturnDate(record.getReturnDate());
        dto.setFineAmount(record.getFineAmount());
        dto.setActive(record.getActive());
        dto.setOverdue(record.isOverdue());
        return dto;
    }


}

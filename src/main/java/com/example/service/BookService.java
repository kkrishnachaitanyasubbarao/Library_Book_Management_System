package com.example.service;

import com.example.dto.AvailabilitySummaryDTO;
import com.example.dto.BookDTO;
import com.example.entity.Book;
import com.example.entity.BorrowRecord;
import com.example.exception.BookNotFoundException;
import com.example.repository.BookRepository;
import com.example.repository.BorrowRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    @Transactional
    public BookDTO addOrUpdateBook(BookDTO bookDTO) {
        // Checking  if book already exists
        Book book = bookRepository.findByTitleAndAuthor(bookDTO.getTitle(), bookDTO.getAuthor())
                .orElse(null);

        if (book != null) {
            // Increase total copies
            book.setTotalCopies(book.getTotalCopies() + bookDTO.getTotalCopies());
            book.setAvailableCopies(book.getAvailableCopies() + bookDTO.getTotalCopies());
            book.setIsAvailable(true);
        } else {
            // Create new book
            book = new Book();
            book.setTitle(bookDTO.getTitle());
            book.setAuthor(bookDTO.getAuthor());
            book.setCategory(bookDTO.getCategory());
            book.setTotalCopies(bookDTO.getTotalCopies());
            book.setAvailableCopies(bookDTO.getTotalCopies());
            book.setIsAvailable(bookDTO.getTotalCopies() > 0);
            book.setDeleted(false);
        }

        book = bookRepository.save(book);
        return convertToDTO(book);
    }

    public Page<BookDTO> getBooks(String category, Boolean available, Pageable pageable) {
        Page<Book> books;

        if (category != null && available != null && available) {
            books = bookRepository.findByCategoryAndIsAvailableTrueAndDeletedFalse(category, pageable);
        } else if (category != null) {
            books = bookRepository.findByCategoryAndDeletedFalse(category, pageable);
        } else if (available != null && available) {
            books = bookRepository.findByIsAvailableTrueAndDeletedFalse(pageable);
        } else {
            books = bookRepository.findByDeletedFalse(pageable);
        }

        return books.map(this::convertToDTO);
    }

    @Transactional
    public BookDTO updateBook(UUID id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        if (book.getDeleted()) {
            throw new BookNotFoundException("Book has been deleted");
        }

        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setCategory(bookDTO.getCategory());

        // Update copies
        int difference = bookDTO.getTotalCopies() - book.getTotalCopies();
        book.setTotalCopies(bookDTO.getTotalCopies());
        book.setAvailableCopies(book.getAvailableCopies() + difference);
        book.setIsAvailable(book.getAvailableCopies() > 0);

        book = bookRepository.save(book);
        return convertToDTO(book);
    }

    @Transactional
    public void deleteBook(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        // Checking  if there are any active borrow records
        List<BorrowRecord> activeRecords = borrowRecordRepository
                .findByActiveTrueAndReturnDateIsNull()
                .stream()
                .filter(record -> record.getBook().getId().equals(id))
                .collect(Collectors.toList());

        if (!activeRecords.isEmpty()) {
            throw new IllegalStateException("Cannot delete book with active borrow records");
        }

        book.setDeleted(true);
        bookRepository.save(book);
    }

    public List<BookDTO> getSimilarBooks(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        // Finding books in same category or by same author
        List<Book> similarBooks = bookRepository
                .findByCategoryAndDeletedFalseAndIdNot(book.getCategory(), id);

        if (similarBooks.size() < 5) {
            List<Book> authorBooks = bookRepository
                    .findByAuthorAndDeletedFalseAndIdNot(book.getAuthor(), id);
            similarBooks.addAll(authorBooks);
        }

        return similarBooks.stream()
                .distinct()
                .limit(5)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AvailabilitySummaryDTO> getAvailabilitySummary() {
        List<Object[]> results = bookRepository.getAvailabilitySummary();
        return results.stream()
                .map(row -> new AvailabilitySummaryDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue()
                ))
                .collect(Collectors.toList());
    }

    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setCategory(book.getCategory());
        dto.setIsAvailable(book.getIsAvailable());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());
        return dto;
    }







}

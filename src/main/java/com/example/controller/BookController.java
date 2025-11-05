package com.example.controller;


import com.example.dto.AvailabilitySummaryDTO;
import com.example.dto.BookDTO;
import com.example.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book Management ", description = " Managing books ")

public class BookController {

    private final BookService bookService;

    @PostMapping("/add-book")
    @Operation(summary = " Add a new book ")
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody BookDTO bookDTO) {
        BookDTO savedBook = bookService.addOrUpdateBook(bookDTO);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @GetMapping("/get-books")
    @Operation(summary = "Get all books with optional filters")
    public ResponseEntity<Page<BookDTO>> getBooks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<BookDTO> books = bookService.getBooks(category, available, pageable);
        return ResponseEntity.ok(books);
    }

    @PutMapping("/{id}")
    @Operation(summary = " Update book details ")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable UUID id,
            @Valid @RequestBody BookDTO bookDTO) {
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a book")
    public ResponseEntity<String> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully");
    }

    @GetMapping("/similar/{id}")
    @Operation(summary = "Get similar books by category or author")
    public ResponseEntity<List<BookDTO>> getSimilarBooks(@PathVariable UUID id) {
        List<BookDTO> similarBooks = bookService.getSimilarBooks(id);
        return ResponseEntity.ok(similarBooks);
    }

    @GetMapping("/availability-summary")
    @Operation(summary = "Get availability summary by category")
    public ResponseEntity<List<AvailabilitySummaryDTO>> getAvailabilitySummary() {
        List<AvailabilitySummaryDTO> summary = bookService.getAvailabilitySummary();
        return ResponseEntity.ok(summary);
    }

}

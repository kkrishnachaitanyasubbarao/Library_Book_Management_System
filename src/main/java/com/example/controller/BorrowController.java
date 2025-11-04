package com.example.controller;

import com.example.dto.BorrowRecordDTO;
import com.example.dto.BorrowRequestDTO;
import com.example.dto.ReturnRequestDTO;
import com.example.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/borrows")
@RequiredArgsConstructor
@Tag(name = "Borrow Management", description = "APIs for borrowing and returning books")
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping("/borrow-a-book")
    @Operation(summary = "Borrow a book")
    public ResponseEntity<BorrowRecordDTO> borrowBook(@Valid @RequestBody BorrowRequestDTO request) {
        BorrowRecordDTO record = borrowService.borrowBook(request);
        return new ResponseEntity<>(record, HttpStatus.CREATED);
    }

    @PostMapping("/return")
    @Operation(summary = "Return a borrowed book")
    public ResponseEntity<BorrowRecordDTO> returnBook(@Valid @RequestBody ReturnRequestDTO request) {
        BorrowRecordDTO record = borrowService.returnBook(request);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/records/active")
    @Operation(summary = "Get all currently borrowed books")
    public ResponseEntity<List<BorrowRecordDTO>> getActiveRecords() {
        List<BorrowRecordDTO> records = borrowService.getActiveRecords();
        return ResponseEntity.ok(records);
    }

}

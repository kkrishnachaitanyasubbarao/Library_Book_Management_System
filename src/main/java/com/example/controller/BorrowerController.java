package com.example.controller;


import com.example.dto.BorrowRecordDTO;
import com.example.dto.BorrowerDTO;
import com.example.service.BorrowerService;
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
@RequestMapping("/borrowers")
@RequiredArgsConstructor
@Tag(name = "Borrower Management", description = "APIs for managing borrowers")

public class BorrowerController {

    private final BorrowerService borrowerService;

    @PostMapping("/add-new-borrower")
    @Operation(summary = "Register a new borrower")
    public ResponseEntity<BorrowerDTO> registerBorrower(@Valid @RequestBody BorrowerDTO borrowerDTO) {
        BorrowerDTO savedBorrower = borrowerService.registerBorrower(borrowerDTO);
        return new ResponseEntity<>(savedBorrower, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/records")
    @Operation(summary = "Get borrow history of a borrower")
    public ResponseEntity<List<BorrowRecordDTO>> getBorrowHistory(@PathVariable UUID id) {
        List<BorrowRecordDTO> records = borrowerService.getBorrowHistory(id);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get all borrowers with overdue books")
    public ResponseEntity<List<BorrowerDTO>> getOverdueBorrowers() {
        List<BorrowerDTO> borrowers = borrowerService.getOverdueBorrowers();
        return ResponseEntity.ok(borrowers);
    }

}


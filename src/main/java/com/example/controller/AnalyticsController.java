package com.example.controller;

import com.example.dto.BorrowerActivityDTO;
import com.example.dto.TopBookDTO;
import com.example.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "APIs for analytics and reports")
public class AnalyticsController {
    private final BorrowService borrowService;

    @GetMapping("/top-borrowed-books")
    @Operation(summary = "Get top 5 most borrowed books")
    public ResponseEntity<List<TopBookDTO>> getTopBorrowedBooks(
            @RequestParam(defaultValue = "5") int limit) {
        List<TopBookDTO> topBooks = borrowService.getTopBorrowedBooks(limit);
        return ResponseEntity.ok(topBooks);
    }

    @GetMapping("/borrower-activity")
    @Operation(summary = "Get borrower activity statistics")
    public ResponseEntity<List<BorrowerActivityDTO>> getBorrowerActivity() {
        List<BorrowerActivityDTO> activity = borrowService.getBorrowerActivity();
        return ResponseEntity.ok(activity);
    }

}

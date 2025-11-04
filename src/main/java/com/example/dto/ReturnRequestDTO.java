package com.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestDTO {

    @NotNull(message = "Book ID is required")
    private UUID bookId;

    @NotNull(message = "Borrower ID is required")
    private UUID borrowerId;


}

package com.example.service;


import com.example.dto.BorrowRecordDTO;
import com.example.dto.BorrowerDTO;
import com.example.entity.BorrowRecord;
import com.example.entity.Borrower;
import com.example.repository.BorrowRecordRepository;
import com.example.repository.BorrowerRepository;
import com.example.exception.BorrowerNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    @Transactional
    public BorrowerDTO registerBorrower(BorrowerDTO borrowerDTO) {
        // Checking  if email already exists
        borrowerRepository.findByEmail(borrowerDTO.getEmail())
                .ifPresent(b -> {
                    throw new IllegalStateException("Email already registered");
                });

        Borrower borrower = new Borrower();
        borrower.setName(borrowerDTO.getName());
        borrower.setEmail(borrowerDTO.getEmail());
        borrower.setMembershipType(borrowerDTO.getMembershipType());

        borrower = borrowerRepository.save(borrower);
        return convertToDTO(borrower);
    }

    public List<BorrowRecordDTO> getBorrowHistory(UUID borrowerId) {
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new BorrowerNotFoundException("Borrower not found with id: " + borrowerId));

        List<BorrowRecord> records = borrowRecordRepository.findByBorrowerId(borrowerId);
        return records.stream()
                .map(this::convertRecordToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowerDTO> getOverdueBorrowers() {
        List<Borrower> borrowers = borrowerRepository.findBorrowersWithOverdueBooks();
        return borrowers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BorrowerDTO convertToDTO(Borrower borrower) {
        BorrowerDTO dto = new BorrowerDTO();
        dto.setId(borrower.getId());
        dto.setName(borrower.getName());
        dto.setEmail(borrower.getEmail());
        dto.setMembershipType(borrower.getMembershipType());
        dto.setMaxBorrowLimit(borrower.getMaxBorrowLimit());
        return dto;
    }

    private BorrowRecordDTO convertRecordToDTO(BorrowRecord record) {
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

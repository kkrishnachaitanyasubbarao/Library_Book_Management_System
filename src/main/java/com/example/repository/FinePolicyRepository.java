package com.example.repository;

import com.example.entity.FinePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FinePolicyRepository extends JpaRepository<FinePolicy, Long> {

    Optional<FinePolicy> findByCategory(String category);

}

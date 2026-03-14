package com.bawal.repository;

import com.bawal.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByVariantIdOrderByCreatedAtDesc(Long variantId);
    List<StockMovement> findAllByOrderByCreatedAtDesc();
}

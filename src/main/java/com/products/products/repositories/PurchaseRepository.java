package com.products.products.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.products.products.models.Purchase;


@Repository
public interface PurchaseRepository extends CrudRepository<Purchase, Long> {

    // Query to count total purchases for a specific product from the purchase table
    @Query(value = "SELECT quantity FROM purchase WHERE product_id = :productId", nativeQuery = true)
    Integer getQuantityForProduct(@Param("productId") Long productId);
    
    // Method to find the Purchase record by product ID
    @Query("SELECT p FROM Purchase p WHERE p.product.id = :productId")
    Purchase findByProductId(@Param("productId") Long productId);
    
    @Query("SELECT p FROM Purchase p WHERE p.product.id = :productId AND p.company.id = :companyId")
    Purchase findByProductIdAndCompanyId(@Param("productId") Long productId, @Param("companyId") Long companyId);
    
  @Query("SELECT p FROM Purchase p WHERE p.company.id = :companyId")
  List<Purchase> findByCompanyId(@Param("companyId") Long companyId);
}

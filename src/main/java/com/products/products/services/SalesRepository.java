package com.products.products.services;

import com.products.products.models.Sale; // Ensure this matches the path of your Sale model
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<Sale, Integer> {
    // You can define custom query methods here if needed
}

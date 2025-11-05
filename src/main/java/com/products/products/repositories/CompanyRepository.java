package com.products.products.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.products.products.models.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    // Custom queries can go here if needed
}

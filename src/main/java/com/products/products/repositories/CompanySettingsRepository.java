package com.products.products.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.products.products.models.Company;
import com.products.products.models.CompanySettings; // Assuming you have a CompanySettings entity.

public interface CompanySettingsRepository extends JpaRepository<CompanySettings, Long> {
    // Define custom query methods if needed, e.g.:
    // CompanySettings findById(Long id);
	CompanySettings findFirstByOrderByIdDesc();
	// Fetch CompanySettings by associated Company
    CompanySettings findByCompany(Company company);

}

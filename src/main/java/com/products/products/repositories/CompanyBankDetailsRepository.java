package com.products.products.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.products.products.models.CompanyBankDetails;

@Repository
public interface CompanyBankDetailsRepository extends JpaRepository<CompanyBankDetails, Long> {
    
    // Fetch bank details by linked CompanySettings
    CompanyBankDetails findByCompanySettingsId(Long companySettingsId);
}
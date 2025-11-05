package com.products.products.repositories;

import com.products.products.models.Invoice;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByCustomerPhoneNumber(String customerPhoneNumber); // Make sure this field is correct
    @Query("SELECT i FROM Invoice i ORDER BY i.date DESC")
    Page<Invoice> findAllInvoices(Pageable pageable);
    @Query("SELECT i FROM Invoice i WHERE i.signingAuthorityEmail = :email ORDER BY i.date DESC")
    List<Invoice> findAllBySignerEmail(String email);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

}


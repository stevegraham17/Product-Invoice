package com.products.products.contoller;

import com.products.products.models.*;

import com.products.products.services.CompanyService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/settings")
    public ResponseEntity<String> createCompanySettings(@Valid @RequestBody CompanySettings settings) {
        try {
            companyService.createCompanySettingsForUser(settings);
            return ResponseEntity.ok("Company Settings created successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create Company Settings: " + e.getMessage());
        }
    }

    @PostMapping("/customer")
    public ResponseEntity<String> createCustomer(@Valid @RequestBody Customer customer) {
        companyService.createCustomerForUser(customer);
        return ResponseEntity.ok("Customer created successfully.");
    }

    @PostMapping("/invoice")
    public ResponseEntity<String> createInvoice(@Valid @RequestBody Invoice invoice) {
        companyService.createInvoiceForUser(invoice);
        return ResponseEntity.ok("Invoice created successfully.");
    }

    @PostMapping("/product")
    public ResponseEntity<String> createProduct(@Valid @RequestBody Product product) {
        companyService.createProductForUser(product);
        return ResponseEntity.ok("Product created successfully.");
    }

    @PostMapping("/purchase")
    public ResponseEntity<String> createPurchase(@Valid @RequestBody Purchase purchase) {
        companyService.createPurchaseForUser(purchase);
        return ResponseEntity.ok("Purchase created successfully.");
    }
}

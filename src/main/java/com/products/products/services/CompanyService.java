package com.products.products.services;

// Import necessary classes
import com.products.products.models.*;
import com.products.products.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanySettingsRepository companySettingsRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private ProductsRepository productRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;

    // Method to retrieve the logged-in user's company securely
    public Company getLoggedInUserCompany() {
        try {
            String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            logger.info("Retrieving company for username: {}", username);
            
            User user = userRepository.findByUsername(username);
            if (user == null) {
                logger.error("No user found with username: {}", username);
                return null;
            }

            if (user.getCompany() == null) {
                logger.error("User found, but company is null for username: {}", username);
                return null;
            }
            
            logger.info("Retrieved company_id: {}", user.getCompany().getId());
            return user.getCompany();
        } catch (Exception e) {
            logger.error("Error while retrieving company for the logged-in user: {}", e.getMessage());
            return null;
        }
    }


    // Method to assign company to CompanySettings
    public void createCompanySettingsForUser(CompanySettings settings) {
        Company company = getLoggedInUserCompany();
        if (company != null) {
            settings.setCompany(company);
            logger.info("Setting company_id: {} in CompanySettings", company.getId());
            companySettingsRepository.save(settings);
        } else {
            logger.error("Failed to set CompanySettings - company is null for the logged-in user.");
        }
    }

    // Similar logging added for other methods
    public void createCustomerForUser(Customer customer) {
        Company company = getLoggedInUserCompany();
        if (company != null) {
            customer.setCompany(company);
            logger.info("Setting company_id: {} in Customer", company.getId());
            customerRepository.save(customer);
        } else {
            logger.error("Failed to set Customer - company is null for the logged-in user.");
        }
    }

    public void createInvoiceForUser(Invoice invoice) {
        Company company = getLoggedInUserCompany();
        if (company != null) {
            invoice.setCompany(company);
            logger.info("Setting company_id: {} in Invoice", company.getId());
            invoiceRepository.save(invoice);
        } else {
            logger.error("Failed to set Invoice - company is null for the logged-in user.");
        }
    }

    public void createProductForUser(Product product) {
        Company company = getLoggedInUserCompany();
        if (company != null) {
            product.setCompany(company);
            logger.info("Setting company_id: {} in Product", company.getId());
            productRepository.save(product);
        } else {
            logger.error("Failed to set Product - company is null for the logged-in user.");
        }
    }

    public void createPurchaseForUser(Purchase purchase) {
        Company company = getLoggedInUserCompany();
        if (company != null) {
            purchase.setCompany(company);
            logger.info("Setting company_id: {} in Purchase", company.getId());
            purchaseRepository.save(purchase);
        } else {
            logger.error("Failed to set Purchase - company is null for the logged-in user.");
        }
    }
}

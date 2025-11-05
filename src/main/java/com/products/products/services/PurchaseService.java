package com.products.products.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.products.products.models.Company;
import com.products.products.models.Product;
import com.products.products.models.Purchase;
import com.products.products.models.User;
import com.products.products.repositories.CompanyRepository;
import com.products.products.repositories.PurchaseRepository;
import com.products.products.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductsRepository productsRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    
    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository, ProductsRepository productsRepository, UserRepository userRepository, CompanyRepository companyRepository) {
        this.purchaseRepository = purchaseRepository;
        this.productsRepository = productsRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }
    
    public List<Product> getProductsForCurrentUser() {
        // Retrieve the username of the logged-in user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        
        // Get the company ID associated with the username
        Long companyId = getCompanyIdFromUser(username);
        
        if (companyId != null) {
            // Retrieve products for the company
            return productsRepository.findByCompanyId(companyId);
        } else {
            throw new RuntimeException("Failed to retrieve products: No company ID found for user " + username);
        }
    }

    
    

    public List<Purchase> findAll() {
        // Implement logic to retrieve all purchases, possibly filtering by user/session
        return (List<Purchase>) purchaseRepository.findAll();
    }

    public void save(Purchase purchase) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long companyId = getCompanyIdFromUser(username);

        if (companyId != null) {
            // Retrieve the Company entity using the companyId
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

            purchase.setCompany(company); // Set the Company entity
            purchaseRepository.save(purchase); // Save the purchase
        } else {
            throw new RuntimeException("Failed to retrieve company_id for user: " + username);
        }
    }

    private Long getCompanyIdFromUser(String username) {
        User user = userRepository.findByUsername(username);
        return user != null && user.getCompany() != null ? user.getCompany().getId() : null;
    }


    public void deletePurchase(Long id) {
        // Check if the purchase exists
        if (purchaseRepository.existsById(id)) {
            // Delete the purchase
            purchaseRepository.deleteById(id);
        } else {
            // Optionally handle the case where the purchase does not exist
            throw new EntityNotFoundException("Purchase with id " + id + " not found");
        }
    }
    public List<Purchase> getAllPurchases() {
        return (List<Purchase>) purchaseRepository.findAll();
    }

    public int getTotalQuantityForProduct(Long productId) {
        return purchaseRepository.getQuantityForProduct(productId);
    }

}

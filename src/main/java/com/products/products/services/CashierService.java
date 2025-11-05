package com.products.products.services;

import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.products.products.models.User; // Import your User model
import com.products.products.models.Company; // Import your Company model
import com.products.products.repositories.UserRepository;
import com.products.products.repositories.CompanyRepository;

@Service
public class CashierService {
    
    @Autowired
    private UserRepository userRepository; // Adjust according to your user repository
    @Autowired
    private CompanyRepository companyRepository; // Adjust according to your company repository

    public boolean registerCashier(String email) {
        User currentUser = getCurrentLoggedInUser();
        
        if (currentUser == null) {
            return false; // User is not authenticated
        }

        // Get the current user's company
        Company company = currentUser.getCompany();

        if (company != null) {
            // Create a new cashier
            User newCashier = new User();
            newCashier.setEmail(email);
            newCashier.setAccountType(User.AccountType.CASHIER); // Ensure this matches your User model
            newCashier.setCompany(company); // Set the company reference directly

            userRepository.save(newCashier); // Save the new cashier
            return true;
        }
        return false;
    }

 // Method to get the currently logged-in user
    public User getCurrentLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username);
        }
        throw new IllegalStateException("User not authenticated");
    }


    // Method to get cashiers associated with the current user's company
    public List<User> getCashiersByCurrentCompany() {
        User currentUser = getCurrentLoggedInUser();

        if (currentUser != null && currentUser.getCompany() != null) {
            return userRepository.findByCompany(currentUser.getCompany()); // Assuming this method exists in UserRepository
        }
        return List.of(); // Return an empty list if no cashiers found
    }
}

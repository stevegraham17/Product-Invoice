package com.products.products.repositories;

import com.products.products.models.Company;
import com.products.products.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    Optional<User> findByEmail(String email); // This is fine
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Method to find users (cashiers) by their associated company
    List<User> findByCompany(Company company);
}

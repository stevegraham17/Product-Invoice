package com.products.products.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private String username;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = true)
    private Company company;
  // Change this from String to Company

    private String password;

    @Column(unique = true) // Ensure the email is unique
    private String email;

    @Enumerated(EnumType.STRING)  
    private AccountType accountType;
  
    private boolean forcePasswordReset = false; 

    

    
    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Company getCompany() {  // Updated getter for company
        return company;
    }

    public void setCompany(Company company) {  // Updated setter for company
        this.company = company;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
   

    public String getEmail() { // Getter for email
        return email;
    }

    public void setEmail(String email) { // Setter for email
        this.email = email;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
    
    


    public boolean isForcePasswordReset() {
		return forcePasswordReset;
	}

	public void setForcePasswordReset(boolean forcePasswordReset) {
		this.forcePasswordReset = forcePasswordReset;
	}




	// Define the AccountType enum inside the User class
    public enum AccountType {
        ADMIN, CASHIER, SIGNER
    }
}

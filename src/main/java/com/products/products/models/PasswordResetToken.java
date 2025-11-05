package com.products.products.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"token"}), 
                            @UniqueConstraint(columnNames = {"user_id"})}) // Ensure user_id is unique
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="token",nullable = false) // Ensure token cannot be null
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true) // Ensure user_id is unique
    private User user;
    
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    // Method to check if the token has expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    // Override toString() for better debugging
    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", expiryDate=" + expiryDate +
                '}';
    }
}

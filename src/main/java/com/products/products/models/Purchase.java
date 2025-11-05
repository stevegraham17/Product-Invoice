package com.products.products.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // This creates a many-to-one relationship with Product
    @JoinColumn(name = "product_id", nullable = false) // Foreign key column
  
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore 
    private Company company;
    
    private int quantity;

    private LocalDateTime createdAt; // Assuming you want to track when the purchase was made

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	@Override
    public String toString() {
        return "Purchase{" +
                "id=" + id +
                ", product=" + product + // This will call Product's toString()
                ", quantity=" + quantity +
                ", createdAt=" + createdAt +
                '}';
    }
    
}

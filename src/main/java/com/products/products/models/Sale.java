package com.products.products.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sales") // The name of the table in the database
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Primary key

    @ManyToOne // Many sales can relate to one product
    @JoinColumn(name = "product_id", nullable = false) // Foreign key to the products table
    private Product product; // Reference to the Product entity

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // Quantity of the product sold

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP) // Timestamp of when the sale was made
    private Date createdAt;

    // Default constructor
    public Sale() {
        this.createdAt = new Date(); // Set default createdAt to current date
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product; // Return the Product object
    }

    public void setProduct(Product product) {
        this.product = product; // Set the Product object
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // Optional: Override toString for better logging
    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", product=" + (product != null ? product.getName() : "null") + // Use product name for logging
                ", quantity=" + quantity +
                ", createdAt=" + createdAt +
                '}';
    }
}

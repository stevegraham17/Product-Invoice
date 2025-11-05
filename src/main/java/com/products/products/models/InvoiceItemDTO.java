package com.products.products.models;

public class InvoiceItemDTO {
    private Long id; // Item ID
    private String productName; // Name of the product
    private int quantity; // Quantity of the product
    private double price; // Price of the product
    private double totalPrice; // Total price for this item (quantity * price)

    // Constructor that matches the parameters used in the map function
    public InvoiceItemDTO(String productName, int quantity, double price) {
    	this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = quantity * price; // Calculate total price based on quantity and price
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = quantity * this.price; // Update total price if quantity changes
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        this.totalPrice = this.quantity * price; // Update total price if price changes
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    // Optionally, you can remove the setter for totalPrice to keep it calculated
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

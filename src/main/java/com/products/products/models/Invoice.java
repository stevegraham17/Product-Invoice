package com.products.products.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    
    @Lob
    private byte[] signatureImage;  
    
    public byte[] getSignatureImage() { return signatureImage; }
    public void setSignatureImage(byte[] signatureImage) { this.signatureImage = signatureImage; }
    
    private String customerGstin;
    
    private LocalDateTime date;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "invoice") // Link Invoice to its items
    private List<Item> items = new ArrayList<>();

    @Column(unique = true)
    private String invoiceNumber;

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    
    private double totalAmount;

    private String customerName; // Field for customer name
    private String customerPhoneNumber; // Field for customer phone number
    private String orderType; // New field for order type
 // B2B-only fields
    private String transportMode;   
    private String vehicleNumber;   
    private boolean reverseCharge;  
    private LocalDate supplyDate;   
    private LocalDate dueDate;  
    private String pdfFilePath;   // location of generated invoice
    private String signedFilePath; // (for later, signed invoice)
    private Boolean signed = false;
    private LocalDateTime signedAt;
    private String signingAuthorityEmail;
    public String getTransportMode() {
		return transportMode;
	}

	public void setTransportMode(String transportMode) {
		this.transportMode = transportMode;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public boolean isReverseCharge() {
		return reverseCharge;
	}

	public void setReverseCharge(boolean reverseCharge) {
		this.reverseCharge = reverseCharge;
	}

	public LocalDate getSupplyDate() {
		return supplyDate;
	}

	public void setSupplyDate(LocalDate supplyDate) {
		this.supplyDate = supplyDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	// Constructor to initialize date
    public Invoice() {
        this.date = LocalDateTime.now();
    }

    // Getters and Setters for orderType
    
    public String getOrderType() {
        return orderType;
    }

    public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        calculateTotalAmount(); // Update total amount when items are set
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCustomerName() {
        return customerName;
    }
    
    

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    // Method to calculate total amount
    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
            .mapToDouble(Item::getTotalPrice)
            .sum();
    }

    // Method to add an item to the invoice
    public void addItem(Item item) {
        item.setInvoice(this); // Set the reference back to this invoice
        this.items.add(item);
        calculateTotalAmount(); // Update total amount whenever an item is added
    }

  

	public String getCustomerGstin() {
		return customerGstin;
	}

	public void setCustomerGstin(String customerGstin) {
		this.customerGstin = customerGstin;
	}

	public String getPdfFilePath() {
		return pdfFilePath;
	}

	public void setPdfFilePath(String pdfFilePath) {
		this.pdfFilePath = pdfFilePath;
	}

	public String getSignedFilePath() {
		return signedFilePath;
	}

	public void setSignedFilePath(String signedFilePath) {
		this.signedFilePath = signedFilePath;
	}

	public Boolean getSigned() {
		return signed;
	}

	public void setSigned(Boolean signed) {
		this.signed = signed;
	}

	public LocalDateTime getSignedAt() {
		return signedAt;
	}

	public void setSignedAt(LocalDateTime signedAt) {
		this.signedAt = signedAt;
	}

	public String getSigningAuthorityEmail() {
		return signingAuthorityEmail;
	}
	public void setSigningAuthorityEmail(String signingAuthorityEmail) {
		this.signingAuthorityEmail = signingAuthorityEmail;
	}

	@Entity
    public static class Item {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String productName;
        private int quantity;
        private double price;

        @ManyToOne // Link item back to the invoice
        @JoinColumn(name = "invoice_id")
        private Invoice invoice;
        
        @ManyToOne
        @JoinColumn(name = "company_id", nullable = false)
        private Company company;

        public Item() {}

        public Item(String productName, int quantity, double price) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
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
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getTotalPrice() {
            return quantity * price; // Method to calculate total price for the item
        }

        // Setter for Invoice reference
        public void setInvoice(Invoice invoice) {
            this.invoice = invoice;
        }

        // Getter for Invoice reference
        public Invoice getInvoice() {
            return invoice;
        }

		public Company getCompany() {
			return company;
		}

		public void setCompany(Company company) {
			this.company = company;
		}
        
        
    }
}

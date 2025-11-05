package com.products.products.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
public class CompanySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // Ensures that the company name cannot be null
    private String companyName;

    @Column(nullable = false) // Ensures that the company address cannot be null
    private String companyAddress;

    @Column(nullable = false) // Ensures that the company phone cannot be null
    private String companyPhone;

    private String logoPath; // Optional: path to store the logo file
    
    @Column(length = 15) // GSTIN is 15 characters
    private String gstNumber;

    @Column(length = 10) // PAN is 10 characters
    private String panNumber;
    
    @Column(name = "company_email")
    private String companyEmail;

    
    public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	@ManyToOne
    @JoinColumn(name = "company_id", nullable = true)
    @JsonIgnore // Ignore this field during JSON serialization
    private Company company;

    // Default constructor
    public CompanySettings() {
    	
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

	

	public String getGstin() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPan() {
		// TODO Auto-generated method stub
		return null;
	}

	

	

	public String getCompanyEmail() {
		return companyEmail;
	}

	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}

	public String getAccountNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getIfsc() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBankName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getIfscCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBankBranch() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getState() {
		// TODO Auto-generated method stub
		return null;
	}
}

package com.products.products.models;

import com.products.products.models.CompanyBankDetails;

public class CompanySettingsDTO {
	  private String companyName;
	    private String companyPhone;
	    private String companyAddress;
	    private String companyEmail;
	    private String logoPath;
	    private String gstNumber;
	    private String panNumber;
	    private CompanyBankDetails bankDetails;
		public String getCompanyName() {
			return companyName;
		}
		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}
		public String getCompanyPhone() {
			return companyPhone;
		}
		public void setCompanyPhone(String companyPhone) {
			this.companyPhone = companyPhone;
		}
		public String getCompanyAddress() {
			return companyAddress;
		}
		public void setCompanyAddress(String companyAddress) {
			this.companyAddress = companyAddress;
		}
		public String getCompanyEmail() {
			return companyEmail;
		}
		public void setCompanyEmail(String companyEmail) {
			this.companyEmail = companyEmail;
		}
		public String getLogoPath() {
			return logoPath;
		}
		public void setLogoPath(String logoPath) {
			this.logoPath = logoPath;
		}
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
		public CompanyBankDetails getBankDetails() {
			return bankDetails;
		}
		public void setBankDetails(CompanyBankDetails bankDetails) {
			this.bankDetails = bankDetails;
		}
}

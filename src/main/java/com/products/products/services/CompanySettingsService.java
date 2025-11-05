package com.products.products.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.products.products.repositories.CompanyBankDetailsRepository;
import com.products.products.repositories.CompanySettingsRepository;
import com.products.products.models.CompanySettings;
import com.products.products.models.Company; // Ensure to import Company
import com.products.products.models.CompanyBankDetails;
import com.products.products.services.CompanyService; // Ensure to import CompanyService

import java.io.File;
import java.io.IOException;

@Service
public class CompanySettingsService {

    @Autowired
    private CompanySettingsRepository companySettingsRepository;

    @Autowired
    private CompanyService companyService; // Inject the CompanyService
    @Autowired
    private CompanyBankDetailsRepository bankDetailsRepository;
    
    private static final String LOGO_PATH = "C:\\Users\\etiqu\\OneDrive\\Documents\\Java Product Invoice\\products\\src\\main\\resources\\static";

   
    
    public void updateCompanySettings(
            MultipartFile logo,
            String companyName,
            String companyEmail,       // ✅ Added
            String companyAddress,
            String companyPhone,
            String gstNumber,
            String panNumber,
            String bankName,
            String bankBranch,
            String accountNumber,
            String ifscCode) {

        String logoPath = null;

        // ✅ Handle logo upload
        if (logo != null && !logo.isEmpty()) {
            try {
                File logoFile = new File(LOGO_PATH + File.separator + logo.getOriginalFilename());
                logo.transferTo(logoFile);
                logoPath = logo.getOriginalFilename();
                System.out.println("logoPath"+ logoPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store the logo file", e);
            }
        }

        // ✅ Get or create settings
        CompanySettings settings = getCompanySettings();
        if (settings == null) {
            settings = new CompanySettings();
        }

        Company company = companyService.getLoggedInUserCompany();
        if (company == null) throw new RuntimeException("No company found for logged-in user");

        // ✅ Update company details
        settings.setCompany(company);
        settings.setCompanyName(companyName);
        settings.setCompanyEmail(companyEmail);   // ✅ Added
        settings.setCompanyAddress(companyAddress);
        settings.setCompanyPhone(companyPhone);
        if (logoPath != null) {
            settings.setLogoPath(logoPath);
        }
        settings.setGstNumber(gstNumber);
        settings.setPanNumber(panNumber);

        // ✅ Save settings
        CompanySettings savedSettings = companySettingsRepository.save(settings);

        // ✅ Save or update bank details
        CompanyBankDetails bankDetails = bankDetailsRepository.findByCompanySettingsId(savedSettings.getId());
        if (bankDetails == null) {
            bankDetails = new CompanyBankDetails();
            bankDetails.setCompanySettings(savedSettings);
        }
        bankDetails.setBankName(bankName);
        bankDetails.setBankBranch(bankBranch);
        bankDetails.setAccountNumber(accountNumber);
        bankDetails.setIfscCode(ifscCode);

        bankDetailsRepository.save(bankDetails);
    }



    public CompanySettings getCompanySettings() {
        // Fetch settings for the currently logged-in user's company
        Company company = companyService.getLoggedInUserCompany(); // Ensure you have a method to get the logged-in user's company
        if (company == null) {
            throw new RuntimeException("No company found for the logged-in user.");
        }

        // Use findByCompany to get the CompanySettings
        CompanySettings settings = companySettingsRepository.findByCompany(company);
        
        // If settings are not found, you may return a new instance or handle it as needed
        return (settings != null) ? settings : new CompanySettings(); // Return new settings if none found
    }

    
    public void updateCompanySettingsWithoutLogo(
            String companyName,
            String companyEmail,        // ✅ added
            String companyAddress,
            String companyPhone,
            String gstNumber,
            String panNumber,
            String bankName,
            String bankBranch,
            String accountNumber,
            String ifscCode) {

        // ✅ Fetch existing settings
        CompanySettings settings = getCompanySettings();
        if (settings == null) {
            settings = new CompanySettings();
        }

        Company company = companyService.getLoggedInUserCompany();
        if (company == null) throw new RuntimeException("No company found for logged-in user");

        // ✅ Preserve existing logo path (do not overwrite with null)
        String existingLogoPath = settings.getLogoPath();

        // ✅ Update fields
        settings.setCompany(company);
        settings.setCompanyName(companyName);
        settings.setCompanyEmail(companyEmail);   // ✅ added
        settings.setCompanyAddress(companyAddress);
        settings.setCompanyPhone(companyPhone);
        settings.setGstNumber(gstNumber);
        settings.setPanNumber(panNumber);

        // ✅ Keep old logo if not replaced
        settings.setLogoPath(existingLogoPath);

        // ✅ Save updated settings
        CompanySettings savedSettings = companySettingsRepository.save(settings);

        // ✅ Handle bank details
        CompanyBankDetails bankDetails = bankDetailsRepository.findByCompanySettingsId(savedSettings.getId());
        if (bankDetails == null) {
            bankDetails = new CompanyBankDetails();
            bankDetails.setCompanySettings(savedSettings);
        }

        bankDetails.setBankName(bankName);
        bankDetails.setBankBranch(bankBranch);
        bankDetails.setAccountNumber(accountNumber);
        bankDetails.setIfscCode(ifscCode);

        bankDetailsRepository.save(bankDetails);
    }

}

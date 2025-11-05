package com.products.products.contoller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.products.products.models.CompanyBankDetails;
import com.products.products.models.CompanySettings;
import com.products.products.models.CompanySettingsDTO;
import com.products.products.repositories.CompanyBankDetailsRepository;
import com.products.products.services.CompanySettingsService;

import org.springframework.http.ResponseEntity;

@RestController

@RequestMapping("/company-settings")
public class CompanySettingsController {
	
	@Autowired
	private CompanyBankDetailsRepository bankDetailsRepository;
	
    @Autowired
    private CompanySettingsService companySettingsService;

    @PostMapping("/update")
    public ResponseEntity<?> updateCompanySettings(
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam("companyName") String companyName,
            @RequestParam("companyEmail") String companyEmail,   // ✅ added
            @RequestParam("companyAddress") String companyAddress,
            @RequestParam("companyPhone") String companyPhone,
            @RequestParam(value = "gstNumber", required = false) String gstNumber,
            @RequestParam(value = "panNumber", required = false) String panNumber,
            @RequestParam(value = "bankName", required = false) String bankName,
            @RequestParam(value = "bankBranch", required = false) String bankBranch,
            @RequestParam(value = "accountNumber", required = false) String accountNumber,
            @RequestParam(value = "ifscCode", required = false) String ifscCode) {
        try {
            if (logo != null && !logo.isEmpty()) {
                companySettingsService.updateCompanySettings(
                    logo, companyName, companyEmail, companyAddress, companyPhone,
                    gstNumber, panNumber, bankName, bankBranch, accountNumber, ifscCode
                );
            } else {
                companySettingsService.updateCompanySettingsWithoutLogo(
                    companyName, companyEmail, companyAddress, companyPhone,
                    gstNumber, panNumber, bankName, bankBranch, accountNumber, ifscCode
                );
            }

            return ResponseEntity.ok().body(Collections.singletonMap("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("success", false));
        }
    }


    @GetMapping
    public ResponseEntity<CompanySettingsDTO> getCompanySettings() {
        // Fetch company settings
        CompanySettings settings = companySettingsService.getCompanySettings();
        
        // Fetch bank details linked to this company settings
        CompanyBankDetails bankDetails = bankDetailsRepository.findByCompanySettingsId(settings.getId());

        // Prepare DTO
        CompanySettingsDTO dto = new CompanySettingsDTO();
        dto.setCompanyName(settings.getCompanyName());
        dto.setCompanyPhone(settings.getCompanyPhone());
        dto.setCompanyAddress(settings.getCompanyAddress());
        dto.setCompanyEmail(settings.getCompanyEmail());
        dto.setLogoPath(settings.getLogoPath());
        dto.setGstNumber(settings.getGstNumber());
        dto.setPanNumber(settings.getPanNumber());
        dto.setBankDetails(bankDetails);

        return ResponseEntity.ok(dto);
    }

}

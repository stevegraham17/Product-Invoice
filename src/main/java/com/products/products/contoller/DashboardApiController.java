package com.products.products.contoller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.products.products.services.ProductsRepository;
import com.products.products.models.Product;
import com.products.products.models.Purchase;
import com.products.products.repositories.PurchaseRepository;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {
    
    private final ProductsRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    
    public DashboardApiController(ProductsRepository productRepository, 
                                 PurchaseRepository purchaseRepository) {
        this.productRepository = productRepository;
        this.purchaseRepository = purchaseRepository;
    }
    
    // Get products by company ID
    @GetMapping("/products")
    public List<Product> getProductsForDashboard(@RequestParam Long companyId) {
    	System.out.println("---products logged---");
        return productRepository.findByCompanyId(companyId);
    }
    
    // Get purchases by company ID - USING YOUR EXISTING METHOD
    @GetMapping("/purchases") 
    public List<Purchase> getPurchasesForDashboard(@RequestParam Long companyId) {
        return purchaseRepository.findByCompanyId(companyId);
    }
}
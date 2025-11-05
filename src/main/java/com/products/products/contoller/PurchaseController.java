package com.products.products.contoller; 

import com.products.products.models.Purchase;
import com.products.products.models.Product;
import com.products.products.services.ProductsRepository;
import com.products.products.services.PurchaseService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final ProductsRepository productsRepository;

    @Autowired
    public PurchaseController(PurchaseService purchaseService, ProductsRepository productsRepository) {
        this.purchaseService = purchaseService;
        this.productsRepository = productsRepository;
    }
    @GetMapping("/manage-purchase")
    public String managePurchase(
            Model model,
            @RequestParam(required = false) String successMessage,
            @RequestParam(required = false) String errorMessage,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        // Get the authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "Guest";

        // Fetch products for the current user's company
        List<Product> productsForCurrentUser = purchaseService.getProductsForCurrentUser();
        productsForCurrentUser.forEach(product -> 
            System.out.println("Product: " + product) // Debug log
        );

        // Fetch purchases for this user/company (adjust service method if needed)
        List<Purchase> purchasesForCurrentUser = purchaseService.findAll();

        // Add attributes
        model.addAttribute("username", username);
        model.addAttribute("products", productsForCurrentUser);
        model.addAttribute("purchases", purchasesForCurrentUser);
        model.addAttribute("successMessage", successMessage);
        model.addAttribute("errorMessage", errorMessage);

        // If AJAX request, return only the fragment
        if ("XMLHttpRequest".equals(requestedWith)) {
        	return "admin/manage-sales :: manage-purchase-section-content";
        }

        // Full page fallback
        return "products/manage-purchase";
    }

    
  /*  @GetMapping("/manage-purchase")
    public String managePurchase(Model model,
                                 @RequestParam(required = false) String successMessage,
                                 @RequestParam(required = false) String errorMessage) {
        // Get the authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "Guest";

        // Fetch products for the current user's company
        List<Product> productsForCurrentUser = purchaseService.getProductsForCurrentUser();
        productsForCurrentUser.forEach(purchase -> System.out.println("Purchase: " + purchase)); // Log to check 
        model.addAttribute("products", productsForCurrentUser); // Only products for the user's company
        model.addAttribute("purchases", purchaseService.findAll()); // Fetch all purchases for the current user

        // Pass success and error messages to the template (if they exist)
        model.addAttribute("successMessage", successMessage);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("username", username);
        model.addAttribute("products", productsForCurrentUser);
        model.addAttribute("purchases", purchaseService.findAll());

        

        return "products/manage-purchase"; // Return the template name
    }
*/



    // POST method to handle adding a purchase
 // POST method to handle adding a purchase
 // POST method to handle adding a purchase
 /*   @PostMapping("/manage-purchase")
    public String addPurchase(@RequestParam Long productId, @RequestParam int quantity) {
        // Fetch the product by ID
        Optional<Product> productOptional = productsRepository.findById(productId);
        
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            
            // Check if the product is already in the purchases
            List<Purchase> existingPurchases = purchaseService.findAll(); // Fetch all existing purchases
            Optional<Purchase> existingPurchaseOpt = existingPurchases.stream()
                    .filter(p -> p.getProduct().getId() == productId.intValue()) // Compare int to int
                    .findFirst();

            if (existingPurchaseOpt.isPresent()) {
                // If it exists, increase the quantity
                Purchase existingPurchase = existingPurchaseOpt.get();
                existingPurchase.setQuantity(existingPurchase.getQuantity() + quantity);
                purchaseService.save(existingPurchase); // Save the updated purchase
            } else {
                // If it doesn't exist, create a new purchase
                Purchase purchase = new Purchase();
                purchase.setProduct(product);
                purchase.setQuantity(quantity);
                purchase.setCreatedAt(LocalDateTime.now()); // Set the current time
                purchaseService.save(purchase); // Save the new purchase
            }
            
            // Redirect with success message
            return "redirect:/manage-purchase?successMessage=Purchase added successfully!";
        } else {
            // Redirect with error message if the product is not found
            return "redirect:/manage-purchase?errorMessage=Product not found!";
        }
    }*/
    
    @PostMapping("/manage-purchase")
    public String addPurchase(@RequestParam Long productId,
                              @RequestParam int quantity,
                              HttpServletRequest request,
                              Model model) {

        Optional<Product> productOptional = productsRepository.findById(productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            Optional<Purchase> existingPurchaseOpt = purchaseService.findAll().stream()
            		.filter(p -> p.getProduct().getId() == productId.intValue()) 
                    .findFirst();

            if (existingPurchaseOpt.isPresent()) {
                Purchase existingPurchase = existingPurchaseOpt.get();
                existingPurchase.setQuantity(existingPurchase.getQuantity() + quantity);
                purchaseService.save(existingPurchase);
            } else {
                Purchase purchase = new Purchase();
                purchase.setProduct(product);
                purchase.setQuantity(quantity);
                purchase.setCreatedAt(LocalDateTime.now());
                purchaseService.save(purchase);
            }

            model.addAttribute("successMessage", "Purchase added successfully!");
        } else {
            model.addAttribute("errorMessage", "Product not found!");
        }

        // ✅ Always add updated data for re-render
        model.addAttribute("purchases", purchaseService.findAll());
        model.addAttribute("products", productsRepository.findAll());

        // ✅ If AJAX request, return only the manage-purchase section fragment
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/manage-sales :: manage-purchase-section-content";
        }

        // Otherwise full reload (normal redirect)
        return "redirect:/manage-purchase";
    }

    
   /* @PostMapping("/purchase/delete")
    public String deletePurchase(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        purchaseService.deletePurchase(id);
        redirectAttributes.addFlashAttribute("successMessage", "Purchase deleted successfully.");
        return "redirect:/manage-purchase"; // Redirect to manage purchase page
       // return "products/index :: manage-purchase-section-content";
    }*/
    @PostMapping("/purchase/delete")
    @ResponseBody
    public String deletePurchase(@RequestParam("id") Long id) {
        purchaseService.deletePurchase(id);
        return "Deleted successfully";
    }





}

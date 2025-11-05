package com.products.products.contoller;

import java.io.InputStream;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.products.products.models.Company;
import com.products.products.models.Product;
import com.products.products.models.ProductDto;
import com.products.products.models.Purchase;
import com.products.products.models.User;
import com.products.products.repositories.PurchaseRepository;
import com.products.products.services.ProductsRepository;
import com.products.products.services.UserService;
import com.products.products.utils.BarcodeGenerator;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductsController {
	  @Autowired
	    private PurchaseRepository purchaseRepo;
	
    @Autowired
    private ProductsRepository repo;
    @Autowired
    private UserService userService; 
    
  /*  @GetMapping({"", "/"})
    public String showProductList( @RequestParam(defaultValue = "dashboard") String section, Model model) {
        Company currentCompany = userService.getCurrentLoggedInUserCompany();
        
        if (currentCompany != null) {
            List<Product> products = repo.findByCompanyId(currentCompany.getId());
            model.addAttribute("products", products);
        } else {
            model.addAttribute("products", List.of()); // Empty list if no company is associated
        }

        // Get the username from the authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "Guest"; // Fallback to "Guest" if not authenticated
        model.addAttribute("username", username); // Add the username to the model
        
        model.addAttribute("systemType", "admin"); // This identifies it as admin system
        model.addAttribute("currentSection", section); // Current active section
        return "products/index";
      
    }
    */
    @GetMapping({"", "/"})
     // Only admins can access
    public String showAdminProductList(
            @RequestParam(defaultValue = "dashboard") String section, 
            Model model) {

        Company currentCompany = userService.getCurrentLoggedInUserCompany();

        List<Product> products = (currentCompany != null)
            ? repo.findByCompanyId(currentCompany.getId())
            : List.of();

        model.addAttribute("products", products);
        
    
        
        
        // Get the logged-in username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "Guest";
        model.addAttribute("username", username);

        model.addAttribute("systemType", "admin"); // important for Thymeleaf
        model.addAttribute("currentSection", section);
        
        if (currentCompany != null) {
            model.addAttribute("companyId", currentCompany.getId());
        } else {
            model.addAttribute("companyId", null);
        }

        return "base"; // main layout
    }

    
    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        
     // Get username from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            model.addAttribute("username", authentication.getName());
        } else {
            model.addAttribute("username", "User");
        }
        
        return "products/createproduct";
    }
    
    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result
    ) {
        if (productDto.getImageFile() == null || productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
        }

        if (result.hasErrors()) {
            return "products/createproduct";
        }

        // Save product image
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFileName(storageFileName);

        Company currentCompany = userService.getCurrentLoggedInUserCompany();
        if (currentCompany != null) {
            product.setCompany(currentCompany);
        } else {
            System.out.println("Error: Could not find company for the logged-in user.");
        }

        // 🔹 Generate Barcode
        try {
            String barcodeValue = "PRD-" + System.currentTimeMillis();  // unique barcode
            String barcodeDir = "public/images/barcodes";

            // Ensure barcode folder exists
            Path barcodePath = Paths.get(barcodeDir);
            if (!Files.exists(barcodePath)) {
                Files.createDirectories(barcodePath);
            }

            // Generate and save barcode image
            String barcodeImagePath = BarcodeGenerator.generateBarcodeImage(barcodeValue, barcodeDir);

            product.setBarcode(barcodeValue);
            product.setBarcodeImagePath(barcodeImagePath);
        } catch (Exception e) {
            System.out.println("Barcode generation failed: " + e.getMessage());
        }

        repo.save(product);

        return "redirect:/products";
    }

  @GetMapping ("/edit")
    
    public String showEditPage(
    		Model model,
    		@RequestParam int id
    		) {
    try {
    	Product product = repo.findById((long) id).get();
    	model.addAttribute("product", product);
    	
    	
    	ProductDto productDto = new ProductDto ();
    	productDto.setName (product.getName());
    	productDto.setBrand (product.getBrand () );
    	productDto.setCategory (product.getCategory());
    	productDto.setPrice (product.getPrice());
    	productDto.setDescription (product.getDescription());
    	model.addAttribute("productDto", productDto);
    	
    	 // Get username from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            model.addAttribute("username", authentication.getName());
        } else {
            model.addAttribute("username", "User");
        }
    }
    catch (Exception ex) {
    	System.out.println("Exception:" + ex.getMessage() );
    	return "redirect:/products";
    }
    return "products/EditProduct";
    }
  
    @PostMapping("/edit")
    public String updateProduct(
            @RequestParam("id") Long id,
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return "products/EditProduct";
        }

        try {
            Product product = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

            // update editable fields
            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            // handle image update if a new file is uploaded
            MultipartFile image = productDto.getImageFile();
            if (image != null && !image.isEmpty()) {
                Date updatedAt = new Date();
                String storageFileName = updatedAt.getTime() + "_" + image.getOriginalFilename();

                String uploadDir = "public/images/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }

                product.setImageFileName(storageFileName);
            }

            // 🔹 keep existing barcode and image path (do NOT regenerate)
            // product.setBarcode(product.getBarcode());          // redundant but for clarity
            // product.setBarcodeImagePath(product.getBarcodeImagePath());

            repo.save(product);

        } catch (Exception e) {
            System.out.println("Update Exception: " + e.getMessage());
        }

        return "redirect:/products";
    }

    
    
    
    @GetMapping("/delete")
    public String deleteProduct (
    		@RequestParam int id
    		) {
    try {
    	Product product = repo.findById((long) id).get();
    	
    	// delete product image
    	Path imagePath = Paths.get("public/images/" + product.getImageFileName());
    	
    	try {
    		Files.delete(imagePath);
    	}
    	catch (Exception ex) {
    		System.out.println("Exception:" + ex.getMessage());
    	}
    	
    	repo.delete(product);
    }
    catch (Exception ex) {
    	System.out.println("Exception:" + ex.getMessage());
    }
 
    return "redirect:/products";
    }
    
 // Add this to your ProductsController
    @GetMapping("/search")
    public @ResponseBody List<Product> searchProducts(@RequestParam String term) {
        // Get the current logged-in user's company
        Company currentCompany = userService.getCurrentLoggedInUserCompany();
        System.out.println("productsss: " );
        if (currentCompany != null) {
            // Perform search query based on the term and the current user's company
            List<Product> products = repo.findByNameContainingIgnoreCaseAndCompanyId(term, currentCompany.getId());
            return products;
        } else {
            return List.of(); // Return an empty list if no company is found
        }
    }
    @GetMapping("/find-by-barcode")
    public ResponseEntity<ProductDto> findProductByBarcode(@RequestParam String barcode) {
        System.out.println("Received barcode: '" + barcode + "'");

        Optional<Product> optionalProduct = repo.findByBarcode(barcode);
        if (optionalProduct.isEmpty()) {
            System.out.println("Product not found in DB for barcode: " + barcode);
            return ResponseEntity.status(404).body(null);
        }

        Product product = optionalProduct.get();
        System.out.println("Product found: " + product.getName() + ", Price: " + product.getPrice());
        ProductDto dto = new ProductDto();
        dto.setId((long) product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setBrand(product.getBrand());
        dto.setCategory(product.getCategory());
        // set other fields if needed

        return ResponseEntity.ok(dto);
      
       
    }


    
}

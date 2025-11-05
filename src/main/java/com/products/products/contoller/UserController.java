package com.products.products.contoller;

import com.products.products.models.Company;
import com.products.products.models.User;
import com.products.products.models.UserRegistrationDto;
import com.products.products.repositories.CompanyRepository;
import com.products.products.repositories.UserRepository;
import com.products.products.services.UserService;
import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    private final UserRepository userRepository;
    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    
    

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "register"; // Return the view name for registration
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto registrationDto,
            BindingResult result, Model model) {

        // Password validation
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        // Check for username uniqueness
        if (userService.usernameExists(registrationDto.getUsername())) {
            result.rejectValue("username", "error.username", "Username is already taken");
        }

        // If the account type is CASHIER, ensure company is not set
        if ("CASHIER".equals(registrationDto.getAccountType())) {
            if (userService.emailExists(registrationDto.getEmail())) {
                result.rejectValue("email", "error.email", "Email is already registered");
            }
            registrationDto.setCompanyId(null); // Ensure company ID is null for Cashiers
        } else {
            // Check for company name if not a Cashier
            if (registrationDto.getCompanyName() == null || registrationDto.getCompanyName().isBlank()) {
                result.rejectValue("companyName", "error.companyName", "Company name is required");
            }
        }

        // Return if there are validation errors
        if (result.hasErrors()) {
            return "register";
        }

        // Create user and save
        userService.registerUser(registrationDto);

        return "redirect:/login"; // Redirect after successful registration
    }





    @GetMapping("/login")
    public String showLoginForm() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String accountType = userService.getAccountType(authentication.getName());
            if ("ADMIN".equals(accountType)) {
                return "redirect:/products";  // Redirect to products page for Admin
            } else if ("CASHIER".equals(accountType)) {
                return "redirect:/billing";   // Redirect to billing page for Cashier
            }
        }
        return "login"; // Show login page for non-authenticated users
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute UserLoginDto loginDto, BindingResult result) {
        if (result.hasErrors()) {
            return "login"; // Return to login page if there are validation errors
        }
        // Normally, Spring Security handles authentication, but you can perform additional checks here if needed.
        return "redirect:/"; // Redirect to the homepage or appropriate page after login
    }
    
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = userRepository.existsByUsername(username);
        return ResponseEntity.ok(Map.of("exists", exists)); // Return existence status
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userRepository.existsByEmail(email); // Ensure you have this method in your repository
        return ResponseEntity.ok(Map.of("exists", exists)); // Return existence status
    }




    
}

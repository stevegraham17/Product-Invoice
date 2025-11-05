package com.products.products.contoller;

import com.products.products.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password"; // Create a view for this
    }

    @PostMapping("/forgot-password")
    public String sendResetEmail(@RequestParam String email, Model model) {
        if (userService.isEmailRegistered(email)) {
            userService.sendPasswordResetEmail(email);
            model.addAttribute("message", "A password reset link has been sent to your email.");
        } else {
            model.addAttribute("error", "Email address not found.");
        }
        return "forgot-password"; // Return to the same view
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        // You may want to check if the token is valid before showing the form
        model.addAttribute("token", token);
        return "products/reset-password"; // Create a view for this
    }
    
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam("new_password1") String newPassword,
                                @RequestParam("new_password2") String confirmPassword,
                                Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "products/reset-password"; // reload page with error
        }

        try {
            userService.resetPassword(token, newPassword);
            return "redirect:/login"; 
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "products/reset-password"; 
        }
    }

    @GetMapping("/signer/reset-password")
    public String showSignerResetPasswordPage(Authentication auth, Model model) {
        String email = auth.getName();
        model.addAttribute("email", email);
        return "products/signer-reset-password";  // separate page
    }

    @PostMapping("/signer/reset-password")
    public String signerResetPassword(
            @RequestParam("new_password1") String newPassword,
            @RequestParam("new_password2") String confirmPassword,
            Authentication auth,
            Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "products/signer-reset-password"; 
        }

        try {
            String email = auth.getName();
            userService.resetPasswordByEmail(email, newPassword); // same as before
            return "sign-invoice"; // redirect after password reset
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "products/signer-reset-password"; 
        }
    }


  /*  @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword, Model model) {
        try {
            userService.resetPassword(token, newPassword);
            return "redirect:/login"; // Redirect to login page after successful reset
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "products/reset-password"; // Show error message
        }
    }*/
}

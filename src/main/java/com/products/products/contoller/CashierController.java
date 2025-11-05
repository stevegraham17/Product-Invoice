package com.products.products.contoller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.products.products.models.AccountType;
import com.products.products.models.Company;
import com.products.products.models.User;
import com.products.products.services.UserService;

@Controller
public class CashierController {

    @Autowired
    private UserService userService;

    @PostMapping("/add-cashier")
    public String addCashier(@RequestParam("cashierEmail") String cashierEmail, Principal principal, Model model) {
        String adminEmail = principal.getName();

        boolean success = userService.assignCompanyToCashier(cashierEmail, adminEmail);

        if (success) {
            model.addAttribute("message", "Cashier added successfully and assigned to your company.");
        } else {
            model.addAttribute("error", "Failed to add cashier. Please check if the emails match and the user exists.");
        }
        return "products/index"; // Replace with the actual view name for the admin dashboard
    }
}

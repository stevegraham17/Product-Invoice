package com.products.products.contoller; // Corrected package name

import com.products.products.models.Invoice; // Assuming you have an Invoice model
import com.products.products.models.Invoice.Item;
import com.products.products.models.Product; // Assuming you have a Product model
import com.products.products.models.Sale; // Assuming you have a Sale model
import com.products.products.repositories.InvoiceRepository; // Repository to fetch invoice details
import com.products.products.services.ProductsRepository; // For fetching products
import com.products.products.services.SalesRepository; // Assuming you have a SalesRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/sales")
public class SalesController {

    @Autowired
    private ProductsRepository productsRepository; // For fetching products

    @Autowired
    private SalesRepository salesRepository; // Repository for sales

    @Autowired
    private InvoiceRepository invoiceRepository; // Repository to fetch invoice details

    @GetMapping("/manage")
    public String showSalesPage(Model model) {
        List<Product> products = productsRepository.findAll(); // Fetch all products
        List<Sale> sales = salesRepository.findAll(); // Fetch all sales
        List<Invoice> invoices = invoiceRepository.findAll(); // Fetch all invoices
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "Guest";
        model.addAttribute("username", username);

        model.addAttribute("products", products);
        model.addAttribute("sales", sales); // Add sales to model to display them
        model.addAttribute("invoices", invoices); // Add invoices to model for displaying order details
        return "products/sales"; // Return the sales management HTML template
    }

    @PostMapping("/create")
    public String createSale(@RequestParam int productId, @RequestParam int quantity, 
                             @RequestParam String customerName, @RequestParam String customerPhone, 
                             @RequestParam String orderType, Model model) {
        // Validate product existence
        if (!productsRepository.existsById((long) productId)) {
            model.addAttribute("errorMessage", "Invalid product selected."); // Add error message to the model
            return "products/sales"; // Return to the sales management page with an error
        }
        // Validate quantity
        if (quantity <= 0) {
            model.addAttribute("errorMessage", "Quantity must be greater than zero."); // Add error message
            return "products/sales"; // Return to the sales management page with an error
        }

        // Logic to create a new invoice
        Invoice invoice = new Invoice(); // Create a new Invoice object
        Product product = productsRepository.findById((long) productId).orElse(null); // Fetch the product by ID

        if (product != null) {
            Item item = new Item(product.getName(), quantity, product.getPrice()); // Create an Item
            invoice.addItem(item); // Use the method to add an item
        }

        invoice.setCustomerName(customerName); // Set customer name
        invoice.setCustomerPhoneNumber(customerPhone); // Set customer phone number
        invoice.setOrderType(orderType); // Set order type
        invoiceRepository.save(invoice); // Save the invoice in the database

        return "redirect:/sales/manage"; // Redirect to the sales management page after creation
    }


    @GetMapping("/delete")
    public String deleteSale(@RequestParam int id) {
        try {
            salesRepository.deleteById(id); // Delete sale by id
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage()); // Log any exception that occurs
        }
        return "redirect:/sales/manage"; // Redirect back to sales management page after deletion
    }

    // Additional method to fetch order details
    @GetMapping("/order-details")
    public String viewOrderDetails(Model model) {
        List<Invoice> invoices = invoiceRepository.findAll(); // Fetch all invoices
        model.addAttribute("orders", invoices); // Add orders to model for displaying
        return "products/manageSales"; // Return the manage sales template to view orders
    }
}

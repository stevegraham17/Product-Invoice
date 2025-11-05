package com.products.products.contoller;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.products.products.models.CustomerUtils;
import org.springframework.http.MediaType;
import com.products.products.models.Company;
import com.products.products.models.CompanySettings;
import org.springframework.transaction.annotation.Transactional;

import com.products.products.models.Customer;
import com.products.products.models.CustomerType;
import com.products.products.models.Invoice;

import com.products.products.models.InvoiceDTO;
import com.products.products.models.InvoiceItemDTO;
import com.products.products.models.Product;
import com.products.products.models.ProductDto;
import com.products.products.models.Purchase;
import com.products.products.models.User;
import com.products.products.repositories.CompanyRepository;
import com.products.products.repositories.CustomerRepository;
import com.products.products.repositories.InvoiceRepository;
import com.products.products.repositories.PurchaseRepository;
import com.products.products.repositories.UserRepository;
import com.products.products.services.CompanySettingsService;
import com.products.products.services.EmailService;
import com.products.products.services.InvoiceService;
import com.products.products.services.PdfService;
import com.products.products.services.ProductsRepository;
import com.products.products.services.UserService;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

@Controller
public class BillingController {
	@Autowired
    private EmailService emailService;
	
    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ProductsRepository repo;
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private CompanySettingsService companySettingsService;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private PurchaseRepository purchaseRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private PdfService pdfService;
    


    private static final double TAX_RATE = 0.18; // Example for 18% tax

/*    @GetMapping("/billing")
    public String showBillingPage(Model model, Authentication authentication) {
    	try { String username = authentication.getName();
        Long companyId = userRepository.findByUsername(username).getCompany().getId();

        // Retrieve products associated only with the current user's company
        List<Product> products = productsRepository.findByCompanyId(companyId); // Add custom method
        Map<Integer, Integer> productQuantities = new HashMap<>();

        for (Product product : products) {
            Integer productId = product.getId();
            Integer quantity = purchaseRepository.getQuantityForProduct(Long.valueOf(productId));
            productQuantities.put(productId, (quantity != null) ? quantity : 0);
        }

        model.addAttribute("products", products);
        model.addAttribute("productQuantities", productQuantities);
        model.addAttribute("username", username);

       return "billing"; // Return the billing view
       // return "products/billing-system/index";
    	}catch (Exception e) {
           // model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "error-page";
        }
    	
    }
    
  */  @GetMapping("/billing")
    @PreAuthorize("hasRole('CASHIER')")
    public String showBillingPage(
        @RequestParam(defaultValue = "dashboard") String section, 
        Model model, 
        Authentication authentication) {
        
        try { 
            String username = authentication.getName();
            Long companyId = userRepository.findByUsername(username).getCompany().getId();

            // Retrieve products associated only with the current user's company
            List<Product> products = productsRepository.findByCompanyId(companyId);
            Map<Integer, Integer> productQuantities = new HashMap<>();

            for (Product product : products) {
                Integer productId = product.getId();
                Integer quantity = purchaseRepository.getQuantityForProduct(Long.valueOf(productId));
                productQuantities.put(productId, (quantity != null) ? quantity : 0);
            }

            // Add existing attributes
            model.addAttribute("products", products);
            model.addAttribute("productQuantities", productQuantities);
            model.addAttribute("username", username);
            model.addAttribute("currentSection", section);
            
            // ADD THIS: System type to distinguish between billing and admin
            model.addAttribute("systemType", "billing"); // This tells the template it's the billing system

            // Return your base HTML template
            return "base"; // This should match your base HTML file name

        } catch (Exception e) {
            e.printStackTrace(); // Add logging
            return "error-page";
        }
    }
    @GetMapping("/api/invoices/{id}")
    public ResponseEntity<?> getInvoiceById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            // Handle invalid ID
            return ResponseEntity.badRequest().body("Invalid invoice ID");
        }

        Invoice invoice = invoiceService.getInvoiceById(id);
        if (invoice == null) {
            // Handle not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice not found");
        }

        InvoiceDTO invoiceDTO = invoiceService.convertToDTO(invoice);
        System.out.println("dffsfsdff"+invoiceDTO);
        return ResponseEntity.ok(invoiceDTO);
    }


//  @GetMapping("/api/invoices/{id}")
//  public ResponseEntity<?> getInvoiceById(@PathVariable String id) {
//      if (id == null || id.isBlank()) {
//          return ResponseEntity.badRequest().body("Invalid invoice ID");
//      }
//
//      Invoice invoice = invoiceService.getInvoiceByInvoiceNumber(id);
//      if (invoice == null) {
//          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice not found");
//      }
//
//      InvoiceDTO invoiceDTO = invoiceService.convertToDTO(invoice);
//      return ResponseEntity.ok(invoiceDTO);
//  }


    
    





    
    




    @Transactional
    @PostMapping("/billing")
    public ResponseEntity<?> generateBill(@RequestBody Map<String, Object> billingData) {
        List<Integer> quantities = (List<Integer>) billingData.get("quantities");

        // Retrieve productIds as List<Long>
        List<Long> productIds = ((List<Integer>) billingData.get("productIds"))
                                 .stream()
                                 .map(Integer::longValue)  // Convert each Integer to Long
                                 .collect(Collectors.toList());
        
        String customerGstin = (String) billingData.get("customerGstin");
       
        System.out.println("GST received: '" + customerGstin + "'");
        String invoiceNumber=(String) billingData.get("invoiceNumber");
        String customerName = (String) billingData.get("customerName");
        String customerPhone = (String) billingData.get("customerPhone");
        String orderType = (String) billingData.get("orderType");
        String transportMode = (String) billingData.get("transportMode");
        String vehicleNumber = (String) billingData.get("vehicleNumber");
        Boolean reverseCharge = billingData.get("reverseCharge") != null 
                                ? (Boolean) billingData.get("reverseCharge") 
                                : false;
        String supplyDateStr = (String) billingData.get("supplyDate");
        LocalDate supplyDate = (supplyDateStr != null && !supplyDateStr.trim().isEmpty())
                ? LocalDate.parse(supplyDateStr)
                : null;

        String dueDateStr = (String) billingData.get("dueDate");
        LocalDate dueDate = (dueDateStr != null && !dueDateStr.trim().isEmpty())
                ? LocalDate.parse(dueDateStr)
                : null;

        if (quantities.size() != productIds.size()) {
            return ResponseEntity.badRequest().body("Quantities and product IDs must match.");
        }
        if (customerName.isEmpty() || customerPhone.isEmpty()) {
            return ResponseEntity.badRequest().body("Customer name and phone cannot be empty.");
        }

        try {
            // Fetch the current user's company
            Company company = userService.getCurrentLoggedInUserCompany();

            // Handle customer creation or update
            Customer customer = customerRepository.findByPhoneNumber(customerPhone);
            if (customer == null) {
                customer = new Customer();
                customer.setName(customerName);
                customer.setPhoneNumber(customerPhone);
                customer.setCompany(company);

                // Set GST and type BEFORE saving
                customer.setGstin(customerGstin);
                if (customerGstin != null && !customerGstin.isEmpty()) {
                    customer.setType(CustomerType.BUSINESS);
                } else {
                    customer.setType(CustomerType.CONSUMER);
                }
                	
                customerRepository.save(customer);
            } else if (!customer.getName().equals(customerName)) {
                customer.setName(customerName);
                customer.setCompany(company);

                // Update GST and type
                customer.setGstin(customerGstin);
                if (customerGstin != null && !customerGstin.isEmpty()) {
                    customer.setType(CustomerType.BUSINESS);
                } else {
                    customer.setType(CustomerType.CONSUMER);
                }

                customerRepository.save(customer);
            }


            // Create the invoice using the createInvoice method
            Invoice invoice = createInvoice(quantities, productIds, company);

            // Deduct stock quantities based on the purchases
            for (int i = 0; i < quantities.size(); i++) {
                Long productId = productIds.get(i);
                int quantityToDeduct = quantities.get(i);

                // Find the corresponding purchase record
                Purchase purchase = purchaseRepository.findByProductIdAndCompanyId(productId, company.getId());

                if (purchase == null) {
                    return ResponseEntity.badRequest().body("No purchase record found for product ID " + productId);
                }

                int currentStock = purchase.getQuantity();
                if (currentStock < quantityToDeduct) {
                    return ResponseEntity.badRequest().body("Insufficient stock for product ID " + productId);
                }

                // Update the stock in the purchase record
                purchase.setQuantity(currentStock - quantityToDeduct);
                purchaseRepository.save(purchase); // Save the updated purchase record
            }

            // Set customer details to invoice
            invoice.setCustomerName(customer.getName());
            invoice.setCustomerPhoneNumber(customer.getPhoneNumber());
            invoice.setCustomerGstin(customerGstin);
            invoice.setOrderType(orderType);
            invoice.setDate(LocalDateTime.now());
            invoice.setInvoiceNumber(invoiceNumber);
            
         // Set B2B fields here
            invoice.setTransportMode(transportMode);
            invoice.setVehicleNumber(vehicleNumber);
            invoice.setReverseCharge(reverseCharge);
            invoice.setSupplyDate(supplyDate);
            invoice.setDueDate(dueDate);

            // Save the invoice to the database
            invoiceService.saveInvoice(invoice);
            CustomerType customerType = customer.getType();
            System.out.println("ControllerXXXXXXXXXX - invoice pdf service called: "+customerType);
            //CustomerType customerType = CustomerUtils.getCustomerType(invoice);
           // String pdfUrl = "/download-invoice?invoiceId=" + invoice.getId() + "&customerType=" + customerType.name();
            String pdfUrl = "/download-invoice?invoiceId=" + invoice.getId() 
            + "&customerType=" + customerType.name() 
            + "&signed=false";
            //String pdfUrl = "/download-invoice?invoiceId=" + invoice.getId();
            // Send email to manager/signatory
            
           
            String pdfPath = invoice.getPdfFilePath();
            System.out.println("ControllerXXXXXXXXXX - invoice pdf service called: ");
            
            
            
           /* if (pdfPath == null || pdfPath.isEmpty()) {
                throw new RuntimeException("PDF not found for this invoice");
            }
            String managerEmail = "stevegraham.karikalan@etiquette.ltd"; // Or fetch dynamically from CompanySettings
            String subject = "Invoice #" + invoice.getId() + " for E-Signature";

            // Construct body with direct link to signing page
            String body = "Dear Manager,<br><br>"
                    + "Please review and sign the attached invoice.<br><br>"
                    + "Sign here: <a href=\"http://localhost:8080/invoice-sign.html?invoiceId="
                    + invoice.getId() + "\">Click to Sign Invoice</a><br><br>"
                    + "Thank you.";

            try {
                emailService.sendInvoiceForESign(managerEmail, subject, body, pdfPath);
            } catch (MessagingException e) {
                e.printStackTrace();
                // Optional: log failure but don’t block invoice creation
            }*/

            return ResponseEntity.ok(Map.of("pdfUrl", pdfUrl));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating the invoice: " + e.getMessage());
        }
    }

    public Invoice createInvoice(List<Integer> quantities, List<Long> productIds, Company company) {
        Invoice invoice = new Invoice();
        invoice.setCompany(company);

        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Optional<Product> optionalProduct = productsRepository.findById(productId);

            if (optionalProduct.isPresent() && quantities.get(i) > 0) {
                Product product = optionalProduct.get();
                Invoice.Item item = new Invoice.Item(product.getName(), quantities.get(i), product.getPrice());
                item.setCompany(company);
                invoice.addItem(item);
            }
        }

        invoice.setDate(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }







    








 // In BillingController.java

    @GetMapping("/next-invoice-id")
    public ResponseEntity<Long> getNextInvoiceId() {
        try {
            Long nextInvoiceId = invoiceService.getNextInvoiceId();
            System.out.println("nextinvoiceid***************"+nextInvoiceId);
            return ResponseEntity.ok(nextInvoiceId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);
        }
    }
    @GetMapping("/customer-by-phone")
    public ResponseEntity<?> getCustomerByPhone(@RequestParam String phone) {
        Customer customer = customerRepository.findByPhoneNumber(phone);
        
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }
    }

    
 /*   @GetMapping("/get-customer-name")
    public ResponseEntity<?> getCustomerName(@RequestParam String phone) {
        Customer customer = customerRepository.findByPhoneNumber(phone);

        if (customer != null) {
            Map<String, String> response = new HashMap<>();
            response.put("name", customer.getName());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(Collections.singletonMap("name", "")); // Empty response if customer not found
        }
    }*/


    @GetMapping("/get-customer-details")
    public ResponseEntity<?> getCustomerDetails(@RequestParam String phone) {
        Customer customer = customerRepository.findByPhoneNumber(phone);

        Map<String, String> response = new HashMap<>();
        if (customer != null) {
            response.put("name", customer.getName());
            response.put("gstNo", customer.getGstin()); // Add GST number to response
        } else {
            response.put("name", "");
            response.put("gstNo", ""); // Empty values if customer not found
        }
        return ResponseEntity.ok(response);
    }

    
    

    public class NumberToWordsConverter {

        private static final String[] tensNames = {
            "", " Ten", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", " Seventy", " Eighty", " Ninety"
        };

        private static final String[] numNames = {
            "", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine", " Ten",
            " Eleven", " Twelve", " Thirteen", " Fourteen", " Fifteen", " Sixteen", " Seventeen",
            " Eighteen", " Nineteen"
        };

        private static String convertLessThanOneThousand(int number) {
            String current;
            if (number % 100 < 20) {
                current = numNames[number % 100];
                number /= 100;
            } else {
                current = numNames[number % 10];
                number /= 10;
                current = tensNames[number % 10] + current;
                number /= 10;
            }
            if (number == 0) return current.trim();
            return numNames[number] + " Hundred" + current;
        }

        public static String convert(double totalAmount) {
            if (totalAmount == 0) return "Zero Rupees Only";

            // Pad with zeros for easier handling of digits
            String mask = "000000000000";
            DecimalFormat df = new DecimalFormat(mask);
            String snumber = df.format(totalAmount);

            // Break the number into parts (Crores, Lakhs, Thousands, and Hundreds)
            int crore = Integer.parseInt(snumber.substring(0, 2));  // First 2 digits for Crores
            int lakh = Integer.parseInt(snumber.substring(2, 4));   // Next 2 digits for Lakhs
            int thousand = Integer.parseInt(snumber.substring(4, 6)); // Next 2 digits for Thousands
            int hundred = Integer.parseInt(snumber.substring(6, 9)); // Next 3 digits for Hundreds

            StringBuilder result = new StringBuilder();

            // Append Crores
            if (crore > 0) {
                result.append(convertLessThanOneThousand(crore)).append(" Crore ");
            }

            // Append Lakhs
            if (lakh > 0) {
                result.append(convertLessThanOneThousand(lakh)).append(" Lakh ");
            }

            // Append Thousands
            if (thousand > 0) {
                result.append(convertLessThanOneThousand(thousand)).append(" Thousand ");
            }

            // Append Hundreds
            if (hundred > 0) {
                result.append(convertLessThanOneThousand(hundred)).append(" ");
            }

            // Remove extra spaces and format result
            String finalResult = result.toString().trim();

            // Ensure the result ends with "Rupees Only"
            return finalResult + " Rupees Only";
        }
    }










    
    





  /*  @GetMapping("/download-invoice")
    public ResponseEntity<ByteArrayResource> downloadInvoice(
            @RequestParam("invoiceId") Long invoiceId,
            @RequestParam String customerType) {

        try {
            Invoice invoice = invoiceService.getInvoiceById(invoiceId);
            if (invoice == null) {
                return ResponseEntity.notFound().build();
            }

            // Pass customerType to PDF service
            byte[] pdfBytes = pdfService.generateInvoicePdf(invoice, CustomerType.valueOf(customerType));
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + invoice.getId() + ".pdf")
                    .contentLength(resource.contentLength())
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new ByteArrayResource(("Error generating PDF: " + e.getMessage()).getBytes()));
        }
    }*/
    
  /*  @GetMapping("/download-invoice")
    public ResponseEntity<ByteArrayResource> downloadInvoice(
            @RequestParam("invoiceId") Long invoiceId,
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false, defaultValue = "false") boolean signed) {
    	System.out.println("XXXXXXXXXXXXXXXXXXXX ");
        try {
            Invoice invoice = invoiceService.getInvoiceById(invoiceId);
            if (invoice == null) {
                return ResponseEntity.notFound().build();
            }
            System.out.println("Controller - invoice pdf service called: ");
            byte[] pdfBytes;
            System.out.println("Controller - invoice pdf service called: ");
            if (signed && invoice.getSignedFilePath() != null) {
                // ✅ Return the signed PDF from disk
                Path path = Paths.get(invoice.getSignedFilePath());
                pdfBytes = Files.readAllBytes(path);
            } else {
            	System.out.println("Controller - invoice pdf service called: ");
                // Generate PDF dynamically
                CustomerType type = (customerType != null) ? CustomerType.valueOf(customerType) : CustomerType.CONSUMER;
                pdfBytes = pdfService.generateInvoicePdf(invoice, type);
            }
            String pdfPath = invoice.getPdfFilePath();
            System.out.println("ControllerXXXXXXXXXX - invoice pdf service called: "+ pdfPath);
            
            
            
           if (pdfPath == null || pdfPath.isEmpty()) {
                throw new RuntimeException("PDF not found for this invoice");
            }
            String managerEmail = "stevegraham.karikalan@etiquette.ltd"; // Or fetch dynamically from CompanySettings
            String subject = "Invoice #" + invoice.getId() + " for E-Signature";

            // Construct body with direct link to signing page
           
            String body = "Dear Manager,<br><br>"
                    + "Please review and sign the attached invoice.<br><br>"
                    + "Sign here: <a href=\"http://localhost:8080/sign-invoice?invoiceId=" 
                    + invoice.getId() + "&pdfPath=" + URLEncoder.encode(pdfPath, StandardCharsets.UTF_8) 
                    + "\">Click to Sign Invoice</a><br><br>"
                    + "Thank you.";


            try {
                emailService.sendInvoiceForESign(managerEmail, subject, body, pdfPath);
            } catch (MessagingException e) {
                e.printStackTrace();
                // Optional: log failure but don’t block invoice creation
            }
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            
            
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + invoice.getId() + ".pdf")
                    .contentLength(resource.contentLength())
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new ByteArrayResource(("Error generating PDF: " + e.getMessage()).getBytes()));
        }
    }*/
    
    @GetMapping("/download-invoice")
    public ResponseEntity<ByteArrayResource> downloadInvoice(
            @RequestParam("invoiceId") Long invoiceId,
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false, defaultValue = "false") boolean signed) {
        try {
            // 1️⃣ Fetch invoice
            Invoice invoice = invoiceService.getInvoiceById(invoiceId);
            if (invoice == null) {
                return ResponseEntity.notFound().build();
            }
            CustomerType type = (customerType != null) ? CustomerType.valueOf(customerType) : CustomerType.CONSUMER;
            // 2️⃣ Generate PDF bytes
            byte[] pdfBytes;
            if (signed && invoice.getSignedFilePath() != null) {
                Path path = Paths.get(invoice.getSignedFilePath());
                pdfBytes = Files.readAllBytes(path);
            } else {
                //CustomerType type = (customerType != null) ? CustomerType.valueOf(customerType) : CustomerType.CONSUMER;
                pdfBytes = pdfService.generateInvoicePdf(invoice, type);
            }

            if (type == CustomerType.BUSINESS) {
                String pdfPath = invoice.getPdfFilePath();
                if (pdfPath == null || pdfPath.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ByteArrayResource(("PDF not found for BUSINESS invoice " + invoiceId).getBytes()));
                }

                //  Dynamic SIGNER assignment
                String signerEmail = "stevegraham.karikalan@etiquette.ltd"; // Replace with actual dynamic email (from billing or customer)
                User signer = userService.createOrGetSigner(signerEmail);
                invoice.setSigningAuthorityEmail(signer.getEmail()); // Assign signer email to invoice
                invoiceRepository.save(invoice);

                // Send email to signer with secure login link
                String subject = "Invoice #" + invoice.getInvoiceNumber() + " for E-Signature";
                String body = "Dear " + signer.getUsername() + ",<br><br>"
                        + "Please review and sign the attached invoice.<br><br>"
                        + "Sign here: <a href=\"http://localhost:8080/invoices/" + invoice.getInvoiceNumber() + "/sign\">Click to Sign Invoice</a><br><br>"
                        + "Thank you.";

                try {
                    emailService.sendInvoiceForESign(signer.getEmail(), subject, body, pdfPath);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    // Optional: log failure but don’t block invoice download
                }
            }


            // 5️⃣ Return PDF as download
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + invoice.getId() + ".pdf")
                    .contentLength(resource.contentLength())
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new ByteArrayResource(("Error generating PDF: " + e.getMessage()).getBytes()));
        }
    }






 /*  @GetMapping("/download-invoice")
    public ResponseEntity<ByteArrayResource> downloadInvoice(@RequestParam("invoiceId") Long invoiceId) {
        try {
            Invoice invoice = invoiceService.getInvoiceById(invoiceId);
            if (invoice == null) {
                return ResponseEntity.notFound().build();
            }

            System.out.println("Generating PDF for Invoice ID: " + invoice.getId());

            try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                	// Set the font and draw the company logo
                	// Fetch company settings dynamically
                    CompanySettings settings = companySettingsService.getCompanySettings();
                    System.out.println("Logo Path: " + settings.getLogoPath());


                    // Set the font and draw the company logo
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    PDImageXObject companyLogo = PDImageXObject.createFromFile("C:/Users/etiqu/OneDrive/Documents/Java Product Invoice/products/public"+settings.getLogoPath(), document);
                    float widthInPoints = 7 * 12; // Assuming 12pt font size for 1em
                    contentStream.drawImage(companyLogo, 50, 720, widthInPoints, 75);  // Updated width in points
                    // Adjust logo size and position
                      
                    // Left-top: Company Info (aligned under the logo)
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 700); // Adjust the position below the logo
                    contentStream.setFont(PDType1Font.HELVETICA, 12);

                    // Use dynamic values from the settings
                    contentStream.showText(settings.getCompanyName());
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText(settings.getCompanyAddress());
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText(settings.getCompanyPhone());
                    	
                    contentStream.endText();
                    	
                	// Right-top: Invoice Title
                	contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                	contentStream.beginText();
                	contentStream.newLineAtOffset(400, 750); // Adjust to the right top
                	contentStream.showText("INVOICE");
                	contentStream.endText();

                	// Right-below Invoice Title: Invoice Date, Customer Info
                	contentStream.setFont(PDType1Font.HELVETICA, 12);
                	contentStream.beginText();
                	contentStream.newLineAtOffset(400, 720); // Position below "INVOICE"
                	contentStream.showText("Date: " + invoice.getDate());
                	contentStream.newLineAtOffset(0, -15);
                	contentStream.showText("Invoice #: INV-" + invoice.getId());
                	contentStream.newLineAtOffset(0, -15);
                	contentStream.showText("Bill To: " + invoice.getCustomerName());
                	contentStream.newLineAtOffset(0, -15);
                	contentStream.showText("Phone: " + invoice.getCustomerPhoneNumber());
                	contentStream.endText();
                		
                	// Draw a line separator before the product table
                	contentStream.moveTo(50, 660);
                	contentStream.lineTo(550, 660);
                	contentStream.stroke();
                		
                	// Table Headers for Products
                	contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                	float tableYStart = 640;
                	float tableXStart = 50;
                	float colWidth = 130;
                	
                	contentStream.beginText();
                	contentStream.newLineAtOffset(tableXStart, tableYStart);
                	contentStream.showText("Product Name");
                	contentStream.newLineAtOffset(colWidth, 0);
                	contentStream.showText("Quantity");
                	contentStream.newLineAtOffset(colWidth, 0);
                	contentStream.showText("Unit Price");
                	contentStream.newLineAtOffset(colWidth, 0);
                	contentStream.showText("Total Price");
                	contentStream.endText();
                		
                	// Table Rows for Products
                	contentStream.setFont(PDType1Font.HELVETICA, 12);
                	float currentY = tableYStart - 20;
                	double subtotal = 0;
                		
                	List<Invoice.Item> items = invoice.getItems();
                	
                	for (Invoice.Item item : items) {
                	    double totalPrice = item.getPrice() * item.getQuantity();
                	    subtotal += totalPrice;

                	    contentStream.beginText();
                	    contentStream.newLineAtOffset(tableXStart, currentY);
                	    contentStream.showText(item.getProductName());
                	    contentStream.newLineAtOffset(colWidth, 0);
                	    contentStream.showText(String.valueOf(item.getQuantity()));
                	    contentStream.newLineAtOffset(colWidth, 0);
                	    contentStream.showText(String.format("%.2f", item.getPrice()));
                	    contentStream.newLineAtOffset(colWidth, 0);
                	    contentStream.showText(String.format("%.2f", totalPrice));
                	    contentStream.endText();

                	    currentY -= 20;
                	}

                	// Draw a line separator before totals
                	contentStream.moveTo(50, currentY - 10);
                	contentStream.lineTo(550, currentY - 10);
                	contentStream.stroke();

                	// Totals: Subtotal, Tax, and Total (CGST, SGST)
                	double cgst = subtotal * 0.09;
                	double sgst = subtotal * 0.09;
                	double totalTax = cgst + sgst;
                	double totalAmount = subtotal + totalTax;

                	// Subtotal and taxes
                	contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                	contentStream.beginText();
                	contentStream.newLineAtOffset(400, currentY - 30);
                	contentStream.showText("Subtotal:");
                	contentStream.newLineAtOffset(100, 0);
                	contentStream.showText(String.format("%.2f", subtotal));
                	contentStream.endText();
                	contentStream.beginText();
                	contentStream.newLineAtOffset(400, currentY - 50);
                	contentStream.showText("CGST @9%:");
                	contentStream.newLineAtOffset(100, 0);
                	contentStream.showText(String.format("%.2f", cgst));
                	contentStream.endText();

                	contentStream.beginText();
                	contentStream.newLineAtOffset(400, currentY - 70);
                	contentStream.showText("SGST @9%:");
                	contentStream.newLineAtOffset(100, 0);
                	contentStream.showText(String.format("%.2f", sgst));
                	contentStream.endText();

                	// Total amount in bold
                	contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                	contentStream.beginText();
                	contentStream.newLineAtOffset(400, currentY - 90);
                	contentStream.showText("Total:");
                	contentStream.newLineAtOffset(100, 0);
                	contentStream.showText(String.format("%.2f", totalAmount));
                	contentStream.endText();

                	
                	// Convert totalAmount to words and format it
                	


                	// Final Total Amount (with larger bold font)
                	contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14); // Emphasize the final amount
                	contentStream.beginText();
                	contentStream.newLineAtOffset(400, currentY - 90); // Adjust the position near the bottom right
                	contentStream.showText("Total:");
                	contentStream.newLineAtOffset(100, 0); 
                	contentStream.showText(String.format("%.2f", totalAmount));
                	contentStream.endText();

                	// Balance Due
                	contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                	contentStream.beginText();
                	contentStream.newLineAtOffset(50, currentY - 120); // Place just above the signature line
                	contentStream.showText("Balance Due: INR " + String.format("%.2f", totalAmount));
                	contentStream.endText();

                	


                	// Authorized Signature
                	contentStream.beginText();
                	contentStream.newLineAtOffset(50, currentY - 190);
                	contentStream.showText("Authorized Signature:");
                	contentStream.endText();


                }

                // Save document to output stream
                document.save(outputStream);
                ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

                // Return response with PDF
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + invoice.getId() + ".pdf")
                        .contentLength(resource.contentLength())
                        .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                        .body(resource);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ByteArrayResource(("Error generating PDF: " + e.getMessage()).getBytes()));
        }
    }*/
    
  
 /*   @GetMapping("/download-invoice")
    public ResponseEntity<byte[]> downloadInvoice() throws Exception {
        byte[] pdfBytes = pdfService.generateInvoicePdf();
        

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }*/






    

    




    
 // Method to view invoice history with pagination
    @GetMapping("/invoice-history")
    public ResponseEntity<Page<InvoiceDTO>> viewInvoiceHistory(
            @RequestParam(defaultValue = "0") int page, // Default to first page
            @RequestParam(defaultValue = "10") int size, // Default to 10 items per page
            Authentication authentication) {
    	// Validate the page size, ensuring it's at least 1
        if (size < 1) {
            size = 10; // Fallback to a default page size
        }

        // Optional: Retrieve the logged-in username if you need to filter by user
        String username = authentication.getName();

        // Create a Pageable object using the page and size parameters
        Pageable pageable = PageRequest.of(page, size);

        // Fetch paginated invoices from the service layer
        Page<InvoiceDTO> invoicePage = invoiceService.getAllInvoices(pageable);

        // Log for debugging
        System.out.println("Controller - Retrieved Invoices: " + invoicePage.getTotalElements());

        // Return the paginated list of invoices as a ResponseEntity
        return ResponseEntity.ok(invoicePage);
    }


  /*  @PostMapping("/invoice")
    public ResponseEntity<byte[]> generateInvoice(@RequestBody List<Long> productIds) throws Exception {
        List<Product> products = productsRepository.findAllById(productIds);
        byte[] pdfBytes = pdfService.generateInvoicePdf(); // generate PDF for all products

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }*/


    @GetMapping("/billing/search")
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


    @GetMapping("/billing/find-by-barcode")
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

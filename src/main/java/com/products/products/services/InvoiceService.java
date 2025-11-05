package com.products.products.services;

import com.products.products.models.InvoiceDTO;
import com.products.products.models.InvoiceItemDTO;
import com.products.products.models.Company;
import com.products.products.models.Invoice;
import com.products.products.models.Product;
import com.products.products.repositories.InvoiceRepository;
import com.products.products.services.ProductsRepository; // Correct import for ProductsRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ProductsRepository productsRepository;
    
    

    // Get paginated invoices
    public Page<InvoiceDTO> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable).map(this::convertToDTO);
    }
    

    // Fetch all invoices without pagination
    public List<InvoiceDTO> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll(); // Fetch all invoices
        System.out.println("Invoices retrieved: " + invoices.size()); // Log the number of invoices

        // Log each invoice details for debugging
        for (Invoice invoice : invoices) {
            System.out.println("Invoice ID: " + invoice.getId() + 
                               ", Customer Name: " + invoice.getCustomerName() + 
                               ", Phone Number: " + invoice.getCustomerPhoneNumber() + 
                               ", Total Amount: " + invoice.getTotalAmount());
        }

        return invoices.stream()
                       .map(this::convertToDTO)
                       .collect(Collectors.toList());
    }

    public Invoice createInvoice(List<Integer> quantities, List<Long> productIds, Company company) {
        Invoice invoice = new Invoice();

        // Populate invoice items based on product IDs and quantities
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Optional<Product> optionalProduct = productsRepository.findById(productId);

            // Check if product exists and quantity is valid
            if (optionalProduct.isPresent() && quantities.get(i) > 0) {
                Product product = optionalProduct.get();
                Invoice.Item item = new Invoice.Item(product.getName(), quantities.get(i), product.getPrice());
                item.setCompany(company);
                // Use the addItem method to ensure the relationship is set correctly
                invoice.addItem(item); // This maintains the bidirectional relationship
            }
        }

        // Set the date and total amount (which is now calculated in addItem)
        invoice.setDate(java.time.LocalDateTime.now()); // Set the current date
        invoice.setCompany(company); // Set the company for the invoice
        

        // Save the invoice to the database
        return invoiceRepository.save(invoice);
    }

    public Invoice saveInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Long getNextInvoiceId() {
        return invoiceRepository.count() + 1; // Get the next invoice ID
    }

    public Invoice getInvoiceById(Long id) {
        // Fetch the invoice by ID (assuming you have a repository or DAO)
        return invoiceRepository.findById(id).orElse(null);
    }

    public InvoiceDTO convertToDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setDate(invoice.getDate());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setCustomerName(invoice.getCustomerName());
        dto.setCustomerPhoneNumber(invoice.getCustomerPhoneNumber());
        dto.setCustomerGstin(invoice.getCustomerGstin());
        dto.setOrderType(invoice.getOrderType());
        dto.setTransportMode(invoice.getTransportMode());
        dto.setVehicleNumber(invoice.getVehicleNumber());
        dto.setReverseCharge(invoice.isReverseCharge());
        dto.setSupplyDate(invoice.getSupplyDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());

        // Populate items from Invoice to InvoiceDTO
        List<InvoiceItemDTO> itemDTOs = invoice.getItems().stream()
            .map(item -> new InvoiceItemDTO(item.getProductName(), item.getQuantity(), item.getPrice()))
            .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        // Debugging the conversion
        System.out.println("Converting Invoice ID: " + invoice.getId() + " to DTO");
        
        return dto;
    }

    public Optional<Invoice> getInvoiceByPhoneNumber(String customerPhone) {
        return invoiceRepository.findByCustomerPhoneNumber(customerPhone);
    }

    public Invoice createInvoice(Invoice invoice) {
        // 1️⃣ Save invoice to generate DB ID
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // 2️⃣ Generate human-readable invoice number
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String invoiceNumber = "INV-" + savedInvoice.getId() + "-" + date;

        savedInvoice.setInvoiceNumber(invoiceNumber);
        return invoiceRepository.save(savedInvoice);
    }
    public Invoice getInvoiceByInvoiceNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElse(null);
    }

}
